# Preenchimento de endereço por CEP

No cadastro de usuário da circulação, o campo de CEP pode mostrar o botão **Buscar**. O operador informa um CEP brasileiro (com ou sem hífen) e clica em **Buscar**. O Biblivre consulta o [ViaCEP](https://viacep.com.br) e preenche os campos de endereço devolvidos.

O botão aparece na interface clássica e na interface SPA, no formulário de usuário da circulação.

- O CEP precisa ter 8 dígitos. Hífens e espaços são ignorados.
- Cada campo devolvido com valor substitui o campo correspondente do formulário. Se algum deles já tiver valor, o Biblivre pede confirmação antes de substituir.
- Campos que o ViaCEP devolve vazios ficam intactos. Número e complemento nunca são preenchidos por esta consulta.
- CEP genérico (sem logradouro) preenche o que houver — em geral bairro, cidade e UF — e avisa para completar a rua manualmente.
- Nada é salvo automaticamente: o operador revisa e salva como de costume.

A consulta é feita pelo servidor (`GET /api/v2/circulation/address_lookup/{cep}`) e exige a permissão de salvar usuários da circulação (`circulation.user` / `save`). O navegador fala só com o Biblivre; a chamada ao ViaCEP parte do servidor.

Com a funcionalidade desligada, o botão some. A interface consulta `GET /api/v2/circulation/address_lookup/enabled` (SPA) ou o atributo já renderizado na página (interface clássica).

## Configuração

A funcionalidade vem **habilitada**. Ela é configurada por servidor, em `application.yml` ou por variáveis de ambiente:

| Propriedade | Variável de ambiente | Padrão | Descrição |
|---|---|---|---|
| `biblivre.circulation.address-lookup.enabled` | `BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_ENABLED` | `true` | `true` ou `false` |
| `biblivre.circulation.address-lookup.base-url` | `BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_BASE_URL` | `https://viacep.com.br` | Substitui a URL do ViaCEP (proxy, testes) |
| `biblivre.circulation.address-lookup.connect-timeout` | `BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_CONNECT_TIMEOUT` | `2s` | Tempo máximo para conectar |
| `biblivre.circulation.address-lookup.read-timeout` | `BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_READ_TIMEOUT` | `5s` | Tempo máximo para a resposta |

O servidor chama `GET {base-url}/ws/{cep}/json/`.

Exemplo com Docker Compose, apontando para um proxy e mantendo a consulta ligada:

```yaml
environment:
  BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_ENABLED: true
  BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_BASE_URL: https://viacep.interno.example
```

Para esconder o botão e recusar a consulta:

```yaml
environment:
  BIBLIVRE_CIRCULATION_ADDRESS_LOOKUP_ENABLED: false
```

Com a consulta desligada, `GET /api/v2/circulation/address_lookup/{cep}` responde HTTP 503.

## Campos preenchidos

| Campo do usuário | Conteúdo ViaCEP | Observação |
|---|---|---|
| `address` | `logradouro` | Vazio em CEP genérico; o operador completa a rua |
| `address_neighborhood` | `bairro` | |
| `address_city` | `localidade` | |
| `address_state` | `uf` | Sigla da unidade federativa |

`address_zip` permanece como o operador digitou. Número e complemento ficam de fora do preenchimento.

## Respostas da API

| Situação | HTTP |
|---|---|
| CEP encontrado | 200, com `street`, `neighborhood`, `city`, `state` e `incomplete` |
| CEP inválido (não tem 8 dígitos) | 400 |
| CEP inexistente, ou resposta sem logradouro, bairro, cidade e UF | 404 |
| Sem permissão de salvar usuário | 403 |
| Consulta desligada, ou falha de rede, tempo esgotado ou resposta ilegível do ViaCEP | 503 |
