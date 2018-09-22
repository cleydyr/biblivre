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

#### WeDeploy
[![Deploy](https://cdn.wedeploy.com/images/deploy.svg)](https://console.wedeploy.com/deploy?repo=https://github.com/cleydyr/biblivre/tree/master)

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

- Nem todas as dependências do Biblivre estão disponíveis em repositórios públicos, por esse motivo é necessário instalar manualmente algumas dependências do projeto. Para tanto, a partir da pasta `/lib/` do código-fonte, execute os seguintes comandos (ou, se estiver utilizando *nix execute o script maven_deps.sh **na pasta** `/lib`):

```bash
mvn install:install-file -Dfile=a2j-2.0.4.jar -DgroupId=org.jzkit -DartifactId=a2j -Dversion=2.0.4 -Dpackaging=jar

mvn install:install-file -Dfile=jzkit2_core-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_core -Dversion=2.2.3 -Dpackaging=jar

mvn install:install-file -Dfile=jzkit2_jdbc_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_jdbc_plugin -Dversion=2.2.3 -Dpackaging=jar

mvn install:install-file -Dfile=jzkit2_service-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_service -Dversion=2.2.3 -Dpackaging=jar

mvn install:install-file -Dfile=jzkit2_z3950_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_z3950_plugin -Dversion=2.2.3 -Dpackaging=jar

mvn install:install-file -Dfile=marc4j-2.5.1.beta.jar -DgroupId=org.marc4j -DartifactId=marc4j -Dversion=2.5.1.beta -Dpackaging=jar

mvn install:install-file -Dfile=z3950server-1.0.2.jar -DgroupId=br.org.biblivre -DartifactId=z3950server -Dversion=1.0.2 -Dpackaging=jar

mvn install:install-file -Dfile=itext-4.2.1.jar -DgroupId=com.lowagie -DartifactId=itext -Dversion=4.2.1 -Dpackaging=jar

mvn install:install-file -Dfile=normalizer-2.6.jar -DgroupId=com.ibm.icu -DartifactId=normalizer -Dversion=2.6 -Dpackaging=jar
```

Depois disso você deve atualiza as folhas de estilo executando `mvn sass:update-stylesheets` na **raiz do projeto**.

Para compilar e criar o arquivo WAR você deve executar `mvn package`. O resultado deve estar presente na pasta target.
