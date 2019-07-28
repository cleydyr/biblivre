#!/usr/bin/env bash
mvn install:install-file -Dfile=a2j-2.0.4.jar -DgroupId=org.jzkit -DartifactId=a2j -Dversion=2.0.4 -Dpackaging=jar
mvn install:install-file -Dfile=jzkit2_core-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_core -Dversion=2.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=jzkit2_jdbc_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_jdbc_plugin -Dversion=2.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=jzkit2_service-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_service -Dversion=2.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=jzkit2_z3950_plugin-2.2.3.jar -DgroupId=org.jzkit -DartifactId=jzkit2_z3950_plugin -Dversion=2.2.3 -Dpackaging=jar
mvn install:install-file -Dfile=z3950server-1.0.2.jar -DgroupId=br.org.biblivre -DartifactId=z3950server -Dversion=1.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=itext-4.2.1.jar -DgroupId=com.lowagie.legacy -DartifactId=itext -Dversion=4.2.1 -Dpackaging=jar
mvn install:install-file -Dfile=jdbm-1.0.jar -DgroupId=org.apache.jdbm -DartifactId=jdbm -Dversion=1.0 -Dpackaging=jar
