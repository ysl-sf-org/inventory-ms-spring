##### inventory-ms-spring

# Microservice Apps Integration with MySQL Database

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://cloudnativereference.dev/*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Implementation Details](#implementation-details)
* [Running the application on Docker](#running-the-application-on-docker)
    + [Get the Inventory application](#get-the-inventory-application)
    + [Run the MySQL Docker Container](#run-the-mysql-docker-container)
    + [Populate the MySQL Database](#populate-the-mysql-database)
    + [Run the Inventory application](#run-the-inventory-application)
    + [Validating the application](#validating-the-application)
    + [Exiting the application](#exiting-the-application)
* [Conclusion](#conclusion)

## Introduction

This project will demonstrate how to deploy a Spring Boot Application with a MySQL database.

![Application Architecture](static/inventory.png?raw=true)

Here is an overview of the project's features:
- Leverages [`Spring Boot`](https://projects.spring.io/spring-boot/) framework to build a Microservices application.
- Uses [`MySQL`](https://www.mysql.com/) as the inventory database.
- Uses [`Spring Data JPA`](http://projects.spring.io/spring-data-jpa/) to persist data to MySQL database.
- Uses [`Docker`](https://docs.docker.com/) to package application binary and its dependencies.

### APIs

* Get all items in inventory:
    + `http://localhost:8080/micro/inventory`

## Pre-requisites:

* [Appsody](https://appsody.dev/)
    + [Installing on MacOS](https://appsody.dev/docs/installing/macos)
    + [Installing on Windows](https://appsody.dev/docs/installing/windows)
    + [Installing on RHEL](https://appsody.dev/docs/installing/rhel)
    + [Installing on Ubuntu](https://appsody.dev/docs/installing/ubuntu)
For more details on installation, check [this](https://appsody.dev/docs/installing/installing-appsody/) out.

* Docker Desktop
    + [Docker for Mac](https://docs.docker.com/docker-for-mac/)
    + [Docker for Windows](https://docs.docker.com/docker-for-windows/)

## Implementation Details

We created a new spring boot project using appsody as follows.

```
appsody repo add kabanero https://github.com/kabanero-io/kabanero-stack-hub/releases/download/0.6.5/kabanero-stack-hub-index.yaml

appsody init kabanero/java-spring-boot2
```

And then we defined the necessary code for the application on top on this template.

## Running the application on Docker

### Get the Inventory application

- Clone inventory repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/inventory-ms-spring.git
cd inventory-ms-spring
```

### Run the MySQL Docker Container

Run the below command to get MySQL running via a Docker container.

```bash
# Start a MySQL Container with a database user, a password, and create a new database
docker run --name inventorymysql \
    -e MYSQL_ROOT_PASSWORD=admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 3306:3306 \
    -d mysql:5.7.14
```

If it is successfully deployed, you will see something like below.

```
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
d88a6e5973de        mysql:5.7.14        "docker-entrypoint.s…"   3 minutes ago       Up 3 minutes        0.0.0.0:3306->3306/tcp   inventorymysql
```
### Populate the MySQL Database

Now let us populate the MySQL with data.

- Firstly, `ssh` into the MySQL container.

```
docker exec -it inventorymysql bash
```

- Now, run the below command for table creation.

```
mysql -udbuser -ppassword
```

- This will take you to something like below.

```
root@d88a6e5973de:/# mysql -udbuser -ppassword
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.14 MySQL Community Server (GPL)

Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```

- Go to `scripts > mysql_data.sql`. Copy the contents from [mysql_data.sql](./scripts/mysql_data.sql) and paste the contents in the console.

- You can exit from the console using `exit`.

```
mysql> exit
Bye
```

- To come out of the container, enter `exit`.

```
root@d88a6e5973de:/# exit
```

### Run the Inventory application

- To run the inventory application, run the below command.

```
appsody run --docker-options "-e MYSQL_HOST=host.docker.internal -e MYSQL_PORT=3306 -e MYSQL_DATABASE=inventorydb -e MYSQL_USER=dbuser -e MYSQL_PASSWORD=password"
```

- If it is successfully running, you will see something like below.

```
[Container] 2020-04-16 10:49:30.744  INFO 179 --- [  restartedMain] o.c.s.w.s.ServerTracingAutoConfiguration : Creating WebMvcConfigurer bean with class io.opentracing.contrib.spring.web.interceptor.TracingHandlerInterceptor
[Container] 2020-04-16 10:49:30.955  INFO 179 --- [  restartedMain] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
[Container] 2020-04-16 10:49:31.025  WARN 179 --- [  restartedMain] aWebConfiguration$JpaWebMvcConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
[Container] 2020-04-16 10:49:31.067  INFO 179 --- [  restartedMain] o.s.b.a.w.s.WelcomePageHandlerMapping    : Adding welcome page: class path resource [public/index.html]
[Container] 2020-04-16 10:49:31.596  INFO 179 --- [  restartedMain] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 4 endpoint(s) beneath base path '/actuator'
[Container] 2020-04-16 10:49:31.715  INFO 179 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path '/micro'
[Container] 2020-04-16 10:49:31.718  INFO 179 --- [  restartedMain] application.Main                         : Started Main in 9.908 seconds (JVM running for 11.343)
```

- You can also verify it as follows.

```
$ docker ps
CONTAINER ID        IMAGE                           COMMAND                  CREATED              STATUS              PORTS                                                                                              NAMES
f8cb15d28534        appsody/java-spring-boot2:0.3   "/.appsody/appsody-c…"   About a minute ago   Up About a minute   0.0.0.0:5005->5005/tcp, 0.0.0.0:8080->8080/tcp, 0.0.0.0:8443->8443/tcp, 0.0.0.0:35729->35729/tcp   refarch-cloudnative-micro-inventory-spring
d88a6e5973de        mysql:5.7.14                    "docker-entrypoint.s…"   43 minutes ago       Up 43 minutes       0.0.0.0:3300->3306/tcp                                                                             inventorymysql
```

### Validating the application

Now, you can validate the application as follows.

- Try to hit http://localhost:8080/micro/inventory and you should be able to see a list of items.

- You can also do it using the below command.

```
curl http://localhost:8080/micro/inventory
```

![Inventory api](static/inventory_api_result.png?raw=true)

- Also you can access the swagger ui at http://localhost:8080/micro/swagger-ui.html

![Inventory Swagger UI](static/swagger_inventory.png?raw=true)

- We also enabled sonarqube as part of the application.

To run the sonarqube as a docker container, run the below command.

```
docker run -d --name sonarqube -p 9000:9000 sonarqube
```

To test the application, run the below command.

```
./mvnw sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Now, access `http://localhost:9000/`, login using the credentials `admin/admin`, and then you will see something like below.

![Inventory SonarQube](static/inventory_sonarqube.png?raw=true)

- We included contract testing as part of our application too.

To run Pact as a docker container, run the below command.

```
cd pact_docker/
docker-compose up -d
```

To publish the pacts to pacts broker, run the below command.

```
./mvnw clean install pact:publish -Dpact.broker.url=http://localhost:8500 -Ppact-consumer
```

To verify the results, run the below command.

```
 ./mvnw test -Dpact.verifier.publishResults='true' -Dpactbroker.host=localhost -Dpactbroker.port=8500 -Ppact-producer
```

Now you can access the pact broker to see if the tests are successful at http://localhost:8500/.

![Inventory Pact Broker](static/inventory_pactbroker.png?raw=true)

### Exiting the application

To exit the application, just press `Ctrl+C`.

It shows you something like below.

```
[Container] [INFO] ------------------------------------------------------------------------
[Container] [INFO] BUILD SUCCESS
[Container] [INFO] ------------------------------------------------------------------------
[Container] [INFO] Total time:  13:35 min
[Container] [INFO] Finished at: 2020-04-16T11:02:43Z
[Container] [INFO] ------------------------------------------------------------------------
[Container] Wait received error on APPSODY_RUN/DEBUG/TEST signal: interrupt
Closing down development environment.
```

## Conclusion

You have successfully deployed and tested the Inventory Microservice and a MySQL database in local Docker Containers using Appsody.

To see the Inventory application working in a more complex microservices use case, checkout our Microservice Reference Architecture Application [here](https://github.com/ibm-garage-ref-storefront/docs).
