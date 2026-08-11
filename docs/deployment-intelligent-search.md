# Tutorial de implantação: migração para Pesquisa Inteligente (Biblivre)

Guia operacional para a equipe de implantação migrar uma instalação Biblivre já em produção (Docker Compose + `.env`) para uma versão com **modo inteligente** (busca híbrida: lexical via `tsvector`/GIN + semântica via embedding/HNSW).

Documento complementar: [pgvector-migration.md](./pgvector-migration.md) (detalhes de infraestrutura PostgreSQL/pgvector).

---

## 1. O que muda (visão rápida)

| Antes | Depois |
|---|---|
| Busca clássica pelos índices Biblivre (`indexing_*`) | Continua existindo e funcionando |
| Sem extensão `vector` | PostgreSQL precisa do **pgvector** |
| Sem tabela de busca inteligente | Por schema de biblioteca: `biblio_record_search` (`tsv` + `embedding`) |
| — | Serviço de **embeddings** alcançável pela rede Docker |
| — | App com config `biblivre.search.intelligent.*` |
| — | Reindexação bibliográfica para popular vetores |
| — | Flag de UI Flagsmith `search_intelligent` (menu SPA) |

O modo inteligente **não** usa os índices antigos na consulta híbrida. Ele consulta só `biblio_record_search` e funde resultados lexicais + semânticos (RRF). Os índices clássicos seguem necessários para a busca tradicional.

**Dimensão alvo atual:** `embedding public.vector(1024)` com modelo padrão `bge-m3` (updates `v6_0_0$9_0_0$alpha` … `v6_0_0$9_0_2$alpha`).

---

## 2. Arquitetura típica neste deployment

O Compose da aplicação Biblivre **não** sobe o PostgreSQL; ambos entram na rede externa `biblivre-net`:

```text
[ cliente ]
    │
    ▼
[ container biblivre :8080 ] ──── biblivre-net ──── [ PostgreSQL ]
         │
         └──────────────────────── biblivre-net ──── [ serviço de embeddings ]
                                                      (ex.: Ollama)
```

Compose de referência atual:

```yaml
services:
  biblivre:
    image: cleydyr/biblivre:${VERSION}
    container_name: ${PREFIX}-${VERSION}
    restart: unless-stopped
    mem_limit: 512m
    mem_reservation: 256m
    env_file:
      - ./aws.env
      - ../common/jvm.env
    ports:
      - "${PORT}:8080"
    volumes:
      - ${LOGS_FOLDER}:/usr/local/tomcat/logs
      - ${BIBLIVRE_FOLDER}:/root/Biblivre
      - ${TEMP_FOLDER}:/usr/local/tomcat/temp
      - ../common/server.xml:/usr/local/tomcat/conf/server.xml:ro
    networks:
      - biblivre-net

networks:
  biblivre-net:
    external: true
```

A migração tem **três frentes**, nesta ordem:

1. **PostgreSQL** com pgvector  
2. **Serviço de embeddings** na mesma rede (ou URL alcançável)  
3. **Imagem/app Biblivre** + variáveis + reindex + flag de UI  

---

## 3. Pré-requisitos e decisões

Antes de tocar produção, feche:

1. **Versão da imagem** `cleydyr/biblivre:${VERSION}` que inclui os updates `9_0_0`–`9_0_2` (e SPA com rota `/spa/search/intelligent`).
2. **Onde roda o Postgres** (container na `biblivre-net`, VM, RDS, etc.) e a **major version** (13/14/15/16/17/18…).
3. **Como instalar pgvector** nesse Postgres (pacote, imagem `pgvector/pgvector`, managed service).
4. **Onde rodar embeddings** (recomendado: Ollama com `bge-m3` na `biblivre-net`).
5. **Flagsmith**: ambiente com a feature `search_intelligent` e chave `BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY` já usada (ou a criar).
6. **Janela de manutenção**: backup + possível dump/restore do Postgres + reindex (pode demorar em acervos grandes).
7. **Memória do container app**: `mem_limit: 512m` pode ser apertado durante reindex + chamadas HTTP de embedding. Planeje subir temporariamente (ex.: 1–2 GiB) na janela de reindex.

### Modelo e dimensões (obrigatório alinhado)

| Item | Valor padrão |
|---|---|
| Modelo | `bge-m3` |
| Dimensões | `1024` |
| Provider app | `openai_compatible` (HTTP `POST {base-url}/embeddings`) |
| Coluna DB | `public.vector(1024)` |

Se mudar o modelo, **mude juntos** config da app, coluna/índice (via update adequado) e reindex completo. Não misture dimensões.

---

## 4. Checklist pré-migração

- [ ] Backup completo do PostgreSQL (`pg_dump` / `pg_dumpall`)
- [ ] Backup/cópia do diretório `${BIBLIVRE_FOLDER}` montado em `/root/Biblivre`
- [ ] Anotar `VERSION`, `PORT`, `PREFIX`, host do Postgres, schemas de bibliotecas
- [ ] Confirmar conectividade Docker: app → Postgres e (futuro) app → embeddings na `biblivre-net`
- [ ] Validar em **staging** o mesmo caminho (mesmo major do Postgres)
- [ ] Plano de rollback (seção 11)

---

## 5. Etapa A — Preparar o PostgreSQL (pgvector)

O Biblivre **não exige** a imagem Docker `pgvector/pgvector` em si: exige que a extensão `vector` possa ser carregada:

```sql
CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
```

No boot, o updater tenta criar a extensão (global) e, **por schema de biblioteca**, a tabela `biblio_record_search` + índices GIN (`tsv`) e HNSW (`embedding`). Se os binários da extensão não existirem, a migration falha, faz rollback dessa update, loga erro e **não marca a versão como instalada**. A app pode subir, mas a busca inteligente fica quebrada até corrigir o Postgres e reiniciar a app.

### A.1 — Postgres gerenciado (RDS, Cloud SQL, Azure…)

1. Confirme suporte a **pgvector** na major em uso.
2. Habilite a extensão conforme o provedor.
3. Prefira pré-criar como superuser:

   ```sql
   CREATE EXTENSION IF NOT EXISTS vector WITH SCHEMA public;
   ```

4. Garanta que o role da app possa usar a extensão (ou que ela já exista em `public`).

### A.2 — Postgres em VM / pacotes OS

1. Instale o pacote da mesma major (ex.: `postgresql-16-pgvector`) ou compile o pgvector contra essa major.
2. Reinicie o Postgres se o pacote exigir.
3. Pré-crie a extensão como superuser (recomendado).

### A.3 — Postgres em Docker (imagem `postgres` → `pgvector/pgvector`)

Use quando **não** for possível instalar a extensão na imagem atual. Caminho seguro: **dump → nova imagem/volume → restore** (não delete o volume antigo até validar).

1. Com o container antigo ainda no ar:

   ```bash
   docker exec <old-db> pg_dumpall -U biblivre > biblivre-pre-pgvector.dump.sql
   ```

2. Pare o stack do banco. **Não** apague o volume antigo ainda.
3. Troque a image para `pgvector/pgvector:pgXX` (**mesma major**, se possível).
4. Em PostgreSQL **18+**, o volume de dados costuma montar em `/var/lib/postgresql` (não `/var/lib/postgresql/data`). Confira a doc da imagem.
5. Suba o novo container com volume **novo/vazio** e restaure:

   ```bash
   docker exec -i <new-db> psql -U biblivre -d biblivre4 < biblivre-pre-pgvector.dump.sql
   ```

6. Coloque/mantenha o serviço do banco na rede `biblivre-net` com o **mesmo hostname** que o app já usa em `DATABASE_HOST_NAME` (ou atualize o `.env` do app).
7. Valide:

   ```sql
   SELECT extname FROM pg_extension WHERE extname = 'vector';
   ```

### A.4 — Validação mínima do banco (antes do app novo)

```sql
-- extensão
SELECT extname, extversion FROM pg_extension WHERE extname = 'vector';

-- schemas de biblioteca (ajuste conforme o ambiente)
SELECT nspname FROM pg_namespace
WHERE nspname NOT IN ('pg_catalog', 'information_schema', 'pg_toast', 'public')
ORDER BY 1;
```

Ainda **não** espere ver `biblio_record_search` até a app nova rodar as updates (a menos que você as tenha criado manualmente — não é o fluxo normal).

---

## 6. Etapa B — Serviço de embeddings

A app fala com um endpoint **OpenAI-compatible**:

`POST {base-url}/embeddings`  
Authorization: `Bearer {api-key}`

Exemplo com **Ollama** na mesma rede:

### Exemplo de serviço (Compose auxiliar)

Pode ser um segundo Compose no host, desde que use `biblivre-net`:

```yaml
services:
  embeddings:
    image: ollama/ollama:latest
    container_name: biblivre-embeddings
    restart: unless-stopped
    # Exponha só se precisar depurar do host; o app usa o DNS interno
    # ports:
    #   - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    networks:
      - biblivre-net

volumes:
  ollama_data:

networks:
  biblivre-net:
    external: true
```

Depois de subir:

```bash
docker exec -it biblivre-embeddings ollama pull bge-m3
# teste local no container:
docker exec -it biblivre-embeddings \
  curl -s http://127.0.0.1:11434/v1/embeddings \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer ollama' \
  -d '{"model":"bge-m3","input":["teste de embedding"]}'
```

Do container Biblivre (após deploy), o `base-url` típico será:

`http://biblivre-embeddings:11434/v1`

> Sem serviço de embedding alcançável, a reindex ainda pode criar linhas em `biblio_record_search` com `embedding = NULL` (só lexical/FTS). A parte semântica só funciona com vetores preenchidos.

---

## 7. Etapa C — Variáveis de ambiente da aplicação

O Biblivre lê `biblivre.search.intelligent.*` (Spring Boot). Em Docker, prefira overrides por env (relaxed binding) no `.env` / `aws.env` / `jvm.env`.

### Variáveis recomendadas

| Variável | Exemplo | Notas |
|---|---|---|
| `BIBLIVRE_SEARCH_INTELLIGENT_ENABLED` | `true` | Liga o backend do modo inteligente |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_PROVIDER` | `openai_compatible` | Default se omitido |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_BASE_URL` | `http://biblivre-embeddings:11434/v1` | **Sem** path `/embeddings` no final |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_API_KEY` | `ollama` | Qualquer bearer aceito pelo provedor |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_MODEL` | `bge-m3` | Deve bater com o modelo puxado |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_DIMENSIONS` | `1024` | Deve bater com a coluna |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_BATCH_SIZE` | `32` | Lotes no reindex |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_CONNECT_TIMEOUT` | `2s` | Timeout de conexão HTTP com o provedor |
| `BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_READ_TIMEOUT` | `30s` | Timeout de leitura da resposta de embedding |
| `BIBLIVRE_SEARCH_INTELLIGENT_CANDIDATE_LIMIT` | `40` | Opcional |
| `BIBLIVRE_SEARCH_INTELLIGENT_RRF_K` | `50` | Opcional |
| `BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY` | *(chave do ambiente)* | UI SPA; feature `search_intelligent` |

> **Atenção:** o `application.yml` empacotado pode trazer `enabled: true` e um `base-url` de desenvolvimento. Em produção, **sempre** sobrescreva `BASE_URL` (e demais campos) com hosts da `biblivre-net` — nunca IPs de lab.

### Onde colocar no deployment atual

Opções equivalentes:

1. Acrescentar as variáveis em `./aws.env` ou `../common/jvm.env` (já referenciados por `env_file`), **ou**
2. Adicionar um `env_file` dedicado, ex. `./intelligent-search.env`, no Compose.

Exemplo de `intelligent-search.env`:

```env
BIBLIVRE_SEARCH_INTELLIGENT_ENABLED=true
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_PROVIDER=openai_compatible
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_BASE_URL=http://biblivre-embeddings:11434/v1
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_API_KEY=ollama
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_MODEL=bge-m3
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_DIMENSIONS=1024
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_BATCH_SIZE=32
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_CONNECT_TIMEOUT=2s
BIBLIVRE_SEARCH_INTELLIGENT_EMBEDDING_READ_TIMEOUT=30s
```

Trecho de Compose sugerido:

```yaml
services:
  biblivre:
    image: cleydyr/biblivre:${VERSION}
    container_name: ${PREFIX}-${VERSION}
    restart: unless-stopped
    mem_limit: 1536m          # subir na janela de reindex; depois pode revisar
    mem_reservation: 512m
    env_file:
      - ./aws.env
      - ../common/jvm.env
      - ./intelligent-search.env
    # ... resto igual (ports, volumes, networks)
```

Mantenha as variáveis existentes de banco (`DATABASE_HOST_NAME`, `DATABASE_PORT`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`, etc.) inalteradas, salvo se o hostname do Postgres mudar na etapa A.

---

## 8. Etapa D — Deploy da nova imagem Biblivre

Ordem recomendada na janela:

1. Postgres com pgvector validado (extensão `vector` ok).  
2. Serviço de embeddings no ar + modelo `bge-m3` puxado + teste `/v1/embeddings`.  
3. Atualizar `.env` da unidade (`VERSION=...`) e arquivos de env da busca inteligente.  
4. Subir a nova imagem:

   ```bash
   cd /caminho/da/unidade
   docker compose pull   # se a imagem for remota
   docker compose up -d
   ```

5. Acompanhar logs do container:

   ```bash
   docker logs -f ${PREFIX}-${VERSION}
   ```

   Procure erros de update relacionados a `vector` / `biblio_record_search`. Se a extensão faltar, a update não fica marcada como instalada — corrija o Postgres e **reinicie** o app.

6. Validar schema **por biblioteca**:

   ```sql
   SELECT extname FROM pg_extension WHERE extname = 'vector';

   SET search_path TO <schema_da_biblioteca>, public;
   \d biblio_record_search
   -- embedding deve ser vector(1024)
   ```

   Índices esperados:

   - `ix_biblio_record_search_tsv` — `USING gin (tsv)`
   - `ix_biblio_record_search_embedding` — `USING hnsw (embedding public.vector_cosine_ops)`

Repita o `\d` para **todos** os schemas de biblioteca do tenant/host.

---

## 9. Etapa E — Reindexação bibliográfica

Com migrations ok e embeddings alcançáveis:

1. Na UI: **Administração → Manutenção → reindexar bibliográfico**  
   (ou módulo `administration.indexing`, action `reindex`, `record_type=biblio`).
2. O reindex clássico roda e, em seguida, a app repovoa `biblio_record_search` (texto + `tsv` + embedding em lotes).
3. Em acervos grandes: monitore CPU/RAM do app, do Postgres e do Ollama; a operação pode ser longa.
4. Validação:

   ```sql
   SET search_path TO <schema_da_biblioteca>, public;

   SELECT COUNT(*) AS total,
          COUNT(embedding) AS with_vec,
          model_id
   FROM biblio_record_search
   GROUP BY model_id;
   ```

   Esperado: `with_vec` ≈ total de registros bibliográficos indexáveis; `model_id` = `bge-m3` (ou o configurado).

Se `with_vec` for muito menor que `total`, revise conectividade/logs de embedding (`Failed to embed` / `Failed to batch-embed`). Linhas com `embedding NULL` ainda entram na parte lexical.

---

## 10. Etapa F — Habilitar a UI (Flagsmith)

O menu **Pesquisa Inteligente** na SPA depende da feature flag booleana:

- Nome da flag: `search_intelligent`
- Código: `SEARCH_INTELLIGENT_FEATURE`
- Chave de ambiente no JVM/Docker: `BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY`

Passos:

1. No Flagsmith do ambiente de produção, crie/ative `search_intelligent` para o environment correto.
2. Confirme que o container recebe `BIBLIVRE_FLAGSMITH_ENVIRONMENT_KEY`.
3. Abra a SPA, verifique o item de navegação e a rota `/spa/search/intelligent`.
4. Faça uma busca de fumaça (termo conhecido + frase semântica).

Backend com `BIBLIVRE_SEARCH_INTELLIGENT_ENABLED=false` rejeita o modo mesmo com a flag de UI ligada.

---

## 11. Rollback

A busca clássica **não depende** da extensão/tabela nova. Rollback seguro:

1. **Rápido (só desligar inteligente):**

   ```env
   BIBLIVRE_SEARCH_INTELLIGENT_ENABLED=false
   ```

   E/ou desligar a flag Flagsmith `search_intelligent`. Reinicie o app se necessário.

2. **Reverter imagem:** volte `VERSION` para a tag anterior e `docker compose up -d`.  
   Deixar `vector` / `biblio_record_search` no banco **não quebra** a busca clássica.

3. **Postgres:** só reverta dump/volume se a etapa A.3 (troca de imagem/volume) tiver falhado; caso contrário não é necessário.

---

## 12. Troubleshooting

| Sintoma | Verificação | Ação |
|---|---|---|
| Update `vector` falha no boot | Logs do app; `CREATE EXTENSION vector` manual | Instalar binários pgvector; reiniciar app |
| Tabela ausente em um schema | `\dt` no schema | Reiniciar app para reaplicar updates scoped |
| `embedding` com dimensão errada | `\d biblio_record_search` | Garantir updates até `9_0_2`; reindex |
| `with_vec = 0` | Logs de embed; curl do app → Ollama | Corrigir `BASE_URL`/rede/modelo; reindex |
| Menu inteligente ausente | Flagsmith + env key | Ativar `search_intelligent` |
| Busca inteligente vazia/erro | `ENABLED=true`, embeddings, reindex | Conferir seções 7–9 |
| OOM / container kill no reindex | `docker inspect` / exit 137 | Subir `mem_limit`; reduzir `BATCH_SIZE` |

Teste de rede a partir do container da app:

```bash
docker exec -it ${PREFIX}-${VERSION} \
  curl -sS -o /dev/null -w '%{http_code}\n' \
  http://biblivre-embeddings:11434/api/tags
```

(Ajuste host/porta ao seu serviço.)

---

## 13. Checklist final de produção

- [ ] Backup Postgres + `${BIBLIVRE_FOLDER}` feitos
- [ ] pgvector disponível na major de produção
- [ ] Extensão `vector` em `public`
- [ ] Serviço de embeddings na `biblivre-net`, modelo `bge-m3`, dims 1024
- [ ] `VERSION` nova implantada; updates `9_0_0`–`9_0_2` em **todos** os schemas
- [ ] Env de produção **sem** URLs de desenvolvimento
- [ ] `biblio_record_search` com GIN + HNSW; `COUNT(embedding)` coerente
- [ ] Flagsmith `search_intelligent` ligada
- [ ] Busca clássica e inteligente validadas (fumaça)
- [ ] Plano de rollback documentado para a unidade

---

## 14. Sequência condensada (runbook)

```text
1. Backup DB + /root/Biblivre
2. Preparar Postgres (pgvector) e validar CREATE EXTENSION vector
3. Subir embeddings na biblivre-net; pull bge-m3; testar /v1/embeddings
4. Preencher intelligent-search.env (ENABLED + BASE_URL + MODEL + DIMENSIONS)
5. Atualizar VERSION; docker compose up -d; acompanhar logs de update
6. Validar \d biblio_record_search em cada schema (vector(1024))
7. Reindex bibliográfico; validar COUNT(embedding)
8. Ativar flag Flagsmith search_intelligent
9. Teste de fumaça UI + rollback notes
```

---

## Referências internas

- [docs/pgvector-migration.md](./pgvector-migration.md) — caminhos de infraestrutura pgvector  
- Updates Java: `v6_0_0$9_0_0$alpha` … `v6_0_0$9_0_2$alpha`  
- DAO híbrido: `IntelligentSearchDAO.populateIntelligentSearch` (`vector_hits` + `text_hits` + RRF)  
- Config: `biblivre.search.intelligent` em `application.yml` / env `BIBLIVRE_SEARCH_INTELLIGENT_*`
