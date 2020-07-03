# Biblivre-5

Biblioteca Livre Internacional 5.0.5

## Build e deploy

Existem duas formas de se compilar e implantar o Biblivre 5: usando uma imagem do Docker (muito mais rápido e prático) ou instalando manualmente as dependências na sua máquina (permite modificações mais profundas).

### Docker

#### Como usar
Execute `docker run -p 8080:8080 cleydyr/biblivre5-dev`
Depois que todo o processo de implantação terminar (cerca de 5 minutos, mas depende da capacidade de processamento da máquina e da largura de banda de conexão à internet disponível), você poderá acessar o Biblivre 5 em `localhost:8080/Biblivre4` (note que o 4 no fim da URL ainda persiste, mesmo sendo o Biblivre 5 que está rodando). Além disso o servidor de aplicação vai rodar em modo debug e por isso você pode fazer o debug remoto da aplicação usando a porta 8000.

#### Variáveis de ambiente
Por padrão, a imagem vai baixar o código do repositório do usuário cleydyr no Github correspondente ao *branch* 5.x. Você pode configurar o nome de usuário e o branch usando as variáveis de ambiente `GITHUB_USER` e `BRANCH_NAME`. Por exemplo para implantar o Biblivre 5 usando o código do branch `meu-branch` do usuário `fulano` no Github execute o comando:
`docker run -p 8080:8080 -p 8000:8000 -e "BRANCH_NAME=meu-branch" -e "GITHUB_USER=fulano" cleydyr/biblivre5-dev`

### Configuração manual

#### Dependências

- [PostgreSQL 9.1](https://www.postgresql.org/);
- [Apache Tomcat 7.0](http://tomcat.apache.org/);
- [Apache Maven 3.2](https://maven.apache.org/);
- Uma IDE de sua preferência. Durante o desenvolvimento do Biblivre a IDE que utilizamos foi o [Eclipse](http://www.eclipse.org/downloads/eclipse-packages/);

#### Configuração

Após baixar o código-fonte, para rodar o Biblivre em sua máquina será necessário realizar algumas configurações no seu ambiente de trabalho, conforme instruções a seguir:

##### PostgreSQL

- Baixe e instale o PostgreSQL;
- Dentro da pasta `/sql/` do código-fonte, execute os seguintes comandos para criar a base de dados: 

  - `"<Caminho do PostgreSQL>\bin\psql.exe" -U postgres -f createdatabase.sql`

- O comando acima criará a base de dados biblivre4 e o usuário biblivre. O Biblivre 5 é uma grande atualização do Biblivre 4, porém incremental. Para menor impacto com os usuários que já possuíam o Biblivre 4, decidimos realizar um upgrade da base de dados em vez de criar uma nova. O usuário biblivre será criado com a senha padrão 'abracadabra'. Garanta que seu ambiente PostgreSQL não esteja aberto para conexões externas.

  - `"<Caminho do PostgreSQL>\bin\psql.exe" -U postgres -f biblivre4.sql -d biblivre4`

- O comando acima criará o schema inicial da base biblivre4.

##### Apache Tomcat

- Baixe e instale o Apache Tomcat;
- Caso esteja usando o Eclipse IDE, configure a [integração com Tomcat](http://www.eclipse.org/webtools/jst/components/ws/M5/tutorials/InstallTomcat.html).

##### Apache Maven

- Baixe e instale o Apache Maven;

- Caso esteja usando o Eclipse IDE, configure a [integração com o Maven](http://www.eclipse.org/m2e/);

- Para compilar e criar o arquivo WAR você deve executar `mvn package`. O resultado deve estar presente na pasta target.
