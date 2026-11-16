# Preenchimento da catalogação por ISBN

Na catalogação bibliográfica, a aba **Formulário** pode mostrar o campo **Preencher por ISBN**. O catalogador informa um ISBN-10 ou ISBN-13 (com ou sem hífens) e clica em **Buscar**. O Biblivre consulta um provedor externo e preenche o formulário com os campos MARC21 devolvidos.

- Se o campo ISBN estiver vazio, usa o primeiro 020 $a já digitado no formulário.
- Cada campo MARC devolvido substitui todas as ocorrências desse campo no formulário. Se algum deles já tiver valor, o Biblivre pede confirmação antes de substituir.
- Campos que o provedor não devolve ficam intactos.
- Nada é salvo automaticamente: o catalogador revisa e salva como de costume.

A consulta é feita pelo servidor (`GET /api/v2/cataloging/isbn_lookup/{isbn}`) e exige a permissão de salvar registros bibliográficos. A API key nunca chega ao navegador.

## Configuração

A funcionalidade vem **desabilitada**. Ela é configurada por servidor, em `application.yml` ou por variáveis de ambiente:

| Propriedade | Variável de ambiente | Padrão | Descrição |
|---|---|---|---|
| `biblivre.cataloging.isbn-lookup.provider` | `BIBLIVRE_CATALOGING_ISBN_LOOKUP_PROVIDER` | `none` | `none`, `google-books` ou `open-library` |
| `biblivre.cataloging.isbn-lookup.api-key` | `BIBLIVRE_CATALOGING_ISBN_LOOKUP_API_KEY` | vazio | Chave opcional enviada ao provedor |
| `biblivre.cataloging.isbn-lookup.base-url` | `BIBLIVRE_CATALOGING_ISBN_LOOKUP_BASE_URL` | URL do provedor | Substitui a URL do provedor (proxy, testes) |
| `biblivre.cataloging.isbn-lookup.connect-timeout` | `BIBLIVRE_CATALOGING_ISBN_LOOKUP_CONNECT_TIMEOUT` | `2s` | Tempo máximo para conectar |
| `biblivre.cataloging.isbn-lookup.read-timeout` | `BIBLIVRE_CATALOGING_ISBN_LOOKUP_READ_TIMEOUT` | `5s` | Tempo máximo para a resposta |

Um nome de provedor desconhecido impede a aplicação de iniciar, com a lista dos provedores disponíveis na mensagem de erro.

Exemplo com Docker Compose:

```yaml
environment:
  BIBLIVRE_CATALOGING_ISBN_LOOKUP_PROVIDER: google-books
  BIBLIVRE_CATALOGING_ISBN_LOOKUP_API_KEY: ${GOOGLE_BOOKS_API_KEY}
```

## Provedores

### `google-books`

[Google Books API](https://developers.google.com/books), o catálogo por trás do Google Play Livros. Boa cobertura de títulos brasileiros, com resumo, idioma e categorias.

Sem chave, as requisições usam uma cota anônima compartilhada que costuma estar esgotada (a API responde HTTP 429 e o Biblivre mostra a mensagem de erro de consulta). Na prática, configure uma chave:

1. No [Google Cloud Console](https://console.cloud.google.com/), crie ou escolha um projeto.
2. Em **APIs e serviços → Biblioteca**, ative a **Books API**.
3. Em **APIs e serviços → Credenciais**, crie uma **chave de API**:
   - Em **Restrições de API**, limite a chave à Books API.
   - Em **Restrições de aplicativo**, escolha **Endereços IP** (o IP de saída do servidor do Biblivre) ou **Nenhuma**. **Não** use **Sites** (referenciadores HTTP): a consulta parte do servidor, sem cabeçalho `Referer`, e o Google recusa com HTTP 403 (`API_KEY_HTTP_REFERRER_BLOCKED` no log do Biblivre).
4. Defina a chave em `BIBLIVRE_CATALOGING_ISBN_LOOKUP_API_KEY`.

### `open-library`

[Open Library Books API](https://openlibrary.org/dev/docs/api/books), mantida pelo Internet Archive. Não usa chave. Traz local de publicação e menção de responsabilidade, mas não traz resumo nem idioma, e os assuntos são etiquetas livres.

## Campos MARC21 preenchidos

| Campo | Conteúdo | Google Books | Open Library |
|---|---|---|---|
| 020 $a | ISBN (ISBN-13 do provedor quando houver) | sim | sim |
| 041 $a | Idioma (ISO 639-2/B, ex.: `por`) | sim | — |
| 100 $a | Primeiro autor | sim | sim |
| 245 $a $b $c | Título, subtítulo, menção de responsabilidade | sim | sim |
| 260 $a $b $c | Local, editora, ano | editora e ano | sim |
| 300 $a | Número de páginas (`N p.`) | sim | sim |
| 520 $a | Resumo | sim | — |
| 650 $a | Assuntos (até 10, 2º indicador `4`) | sim | sim |
| 700 $a | Demais autores | sim | sim |

Os nomes de autor chegam na ordem direta ("George Orwell"). Ajuste para a forma de entrada da biblioteca ("Orwell, George") antes de salvar.

## Novos provedores

Um provedor é um bean Spring que implementa `biblivre.cataloging.isbnlookup.IsbnMetadataProvider`: devolve um `Record` MARC21 (marc4j) para um ISBN já normalizado e validado. O nome retornado por `name()` passa a ser aceito em `biblivre.cataloging.isbn-lookup.provider`. Para reaproveitar o mapeamento existente, converta a resposta em `BookMetadata` e use `BookMetadataMarcMapper.toRecord`.
