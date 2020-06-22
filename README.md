# Biblivre

Biblioteca Livre Internacional

## Build e deploy

Existem duas formas de se compilar e implantar o Biblivre: usando uma imagem do Docker (muito mais rápido e prático) ou instalando manualmente as dependências na sua máquina (permite modificações mais profundas). Devido à dificuldade de se instalar manualmente cada dependência, esta documentação não irá apresentar a manual.

### Dependências
Para compilar, fazer o build e deploy é necessário ter instalado na sua máquina
1. JDK (versão 8 ou posterior)
1. Maven
1. Docker
1. Docker Compose

### Docker Compose

#### Como usar

Na raiz do projeto, compile normalmente rodando o script `mvn clean verify -P docker -Ddockerfile.skip=true`.
Depois que todo o processo de implantação terminar (cerca de 5 minutos, mas depende da capacidade de processamento da máquina e da largura de banda de conexão à internet disponível), você poderá acessar o Biblivre em `localhost:8080/Biblivre6`. Além disso o servidor de aplicação vai rodar em modo debug e por isso você pode fazer o debug remoto da aplicação usando a porta 8000.

#### Configurações

As seguintes variáveis de ambiente são configuráveis através do arquivo .env:

*APP_HTTP_HOST_PORT* (porta de acesso ao Biblivre pelo host; valor padrão 8080)
*APP_DEBUG_HOST_PORT* (porta de debug do Java pelo host (JPDA); valor padrão 8000)
*POSTGRES_PASSWORD* (senha do usuário do banco de dados; valor padrão "abracadabra")
*POSTGRES_DB* (nome do banco de dados; valor padrão "biblivre4")
*DATABASE_HOST_PORT* (porta da aplicação de banco de dados; valor padrão 5432)
*DATABASE_HOST_NAME* (nome do host da aplicação de banco de dados; valor padrão "database", que é o nome de host do contêiner que roda o PostgreSQL)
