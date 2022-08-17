# Biblivre

Biblioteca Livre Internacional - an open source library management system and OPAC: www.biblivre.org.br

## Build and deploy

There are basically two ways to build and deploy:
1. installing the dependencies manually in your system; and
2. using Docker and Docker Compose

2\. is the easiest way as it only requires setting up Docker and Docker Compose, while 1\. gives you more control over the system parts and also makes the database persistent by default. However 1\. is trickier to perform, revert the changes and start over when compared to 2\., so we will only provide instructions on how to generate the war file and build the initial database.

### Dependencies
To build and deploy it's necessary to have in your system
1. JDK (version 17 or newer)
1. Maven
1. Docker (optional, but required for testing and for using the developer, docker and docker-compose profiles)
1. Docker Compose (optional, but required for using the docker-compose profile)

If you're setting up the system manually, you'll also need as a minimum:
1. Tomcat (version 10 or newer)
1. PostgreSQL (version 11 or newer)

### Manual installation

#### Generating the WAR file
Run

```
mvn clean package
```

and it will produce the Biblivre6.war file as a result in the target folder. You'll use that WAR file to deploy the app to Tomcat.

#### Creating the initial database
```
sudo -u postgres psql < sql/createdatabase.sql
sudo -u postgres psql biblivre4 < sql/biblivre4.sql
```

#### Deploying the WAR file
Copy the generated Biblivre6.war file to the webapps folder inside the Tomcat installation. This will make Tomcat deploy the application automatically to the Biblivre6 context. So after deploying you can access the application at http://localhost:8080/Biblivre6.

The default user is admin and the default password is abracadabra.

### Docker Compose

#### Running Biblivre

After installing the dependencies, download or clone (using git) this repository. If you download the repository, extract the zip file to a known folder.

On the project root folder, run `mvn clean package -P docker-compose`.
It will take around 5 minutes, but it depends on the processing power of the computer and the available Internet bandwidth to download dependencies if needed. After all deploy process is done, you can access Biblivre at `http://localhost`.

The default user is admin and the default password is abracadabra.

#### Modifying and debugging Biblivre
The `developer` profile will activate the debug flag to allow you to debug Biblivre remotely. It will also execute the tests automatically (which requires Docker to be installed and running in your machine). If you'd like to skip the tests, you can add the `-Dmaven.test.skip=true` option to the `mvn` command (like in `mvn clean package -P developer -Dmaven.test.skip=true`). The `docker-compose` profile already maps the port 8000 to the host and adds the debug options to the Tomcat server that runs in the container.

#### Configurations

The following variables are used in Biblivre and can be set on the .env file:

* `APP_HTTP_HOST_PORT` (HTTP access port to Biblivre app; default: 8080)
* `APP_DEBUG_HOST_PORT` (debug port (JPDA); default: 8000)
* `POSTGRES_PASSWORD` (password for the database; default "abracadabra")
* `POSTGRES_DB` (database name; default "biblivre4")
* `DATABASE_HOST_PORT` (database host port; default: 5432)
* `DATABASE_HOST_NAME` (database hostname; default: "database", which is also the hostname of the container that hosts PostgreSQL)
