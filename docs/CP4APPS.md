###### inventory-ms-spring

# Running Inventory App on CP4Apps

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-garage-ref-storefront/refarch-cloudnative-storefront*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Inventory application on CP4Apps](#inventory-application-on-cp4apps)
    + [Get the Inventory application](#get-the-inventory-application)
    + [Application manifest](#application-manifest)
    + [Project Setup](#project-setup)
    + [Deploy MySQL to Openshift](#deploy-mysql-to-openshift)
    + [Populate the MySQL Database](#populate-the-mysql-database)
    + [Deploy the app using Kabanero Pipelines](#deploy-the-app-using-kabanero-pipelines)
      * [Access tekton dashboard](#access-tekton-dashboard)
      * [Create registry secrets](#create-registry-secrets)
      * [Create Webhook for the app repo](#create-webhook-for-the-app-repo)
      * [Deploy the app](#deploy-the-app)
* [Conclusion](#conclusion)

This project will demonstrate how to deploy a Spring Boot Application with a MySQL database onto a Kubernetes Cluster.

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

* [RedHat Openshift Cluster](https://cloud.ibm.com/kubernetes/catalog/openshiftcluster)

* IBM Cloud Pak for Applications
  + [Using IBM Console](https://cloud.ibm.com/catalog/content/ibm-cp-applications)
  + [OCP4 CLI installer](https://www.ibm.com/support/knowledgecenter/en/SSCSJL_4.1.x/install-icpa-cli.html)

* Docker Desktop
  + [Docker for Mac](https://docs.docker.com/docker-for-mac/)
  + [Docker for Windows](https://docs.docker.com/docker-for-windows/)

* Command line (CLI) tools
  + [oc](https://www.okd.io/download.html)
  + [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
  + [appsody](https://appsody.dev/docs/getting-started/installation)

## Inventory application on CP4Apps

### Get the Inventory application

- Clone inventory repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/inventory-ms-spring.git
cd inventory-ms-spring
```

### Application manifest

When you see the project structure, you should be able to find an `app-deploy.yaml`. This is generated as follows.

```
appsody deploy --generate-only
```

This generates a default `app-deploy.yaml` and on top of this we added necessary configurations that are required by the Inventory application.

### Project Setup

- Create a new project if it does not exist. Or if you have an existing project, skip this step.

```
oc new-project storefront
```

- Once the namespace is created, we need to add it as a target namespace to Kabanero.

Verify if kabanero is present as follows.

```
$ oc get kabaneros -n kabanero
NAME       AGE   VERSION   READY
kabanero   9d    0.6.1     True
```

- Edit the yaml file configuring kabanero as follows.

```
$ oc edit kabanero kabanero -n kabanero
```

- Finally, navigate to the spec label within the file and add the following targetNamespaces label.

```
spec:
  targetNamespaces:
    - storefront
```

### Deploy MySQL to Openshift

- Add security context constriants to the default service account.

```
oc adm policy add-scc-to-user anyuid system:serviceaccount:storefront:default
```

- Now deploy the inventory databases as follows.

```
oc apply --recursive --filename MySQL/
```

- Verify if the pods are up and running.

```
$ oc get pods
NAME                                   READY   STATUS    RESTARTS   AGE
inventory-mysql-8667979689-6qfkz       1/1     Running   0          17h
```

### Populate the MySQL Database

- Load the data into the database as follows.

```
$ oc exec -it inventory-mysql-8667979689-6qfkz bash
root@inventory-mysql-8667979689-6qfkz:/# mysql -udbuser -ppassword
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 137430
Server version: 5.7.14 MySQL Community Server (GPL)

Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```

- Once you are here, paste the contents of [mysql_data.sql](./../scripts/mysql_data.sql).

- You can exit from the console using `exit`.

```
mysql> exit
Bye
```

- To come out of the container, enter `exit`.

```
root@inventory-mysql-8667979689-6qfkz:/# exit
```

### Deploy the app using Kabanero Pipelines

#### Access tekton dashboard

- Open IBM Cloud Pak for Applications and click on `Instance` section. Then select `Manage Pipelines`.

![CP4Apps](static/cp4apps_pipeline.png?raw=true)

- This will open up the Tekton dashboard.

![Tekton dashboard](static/tekton.png?raw=true)

#### Create registry secrets

- To create a secret, in the menu select `Secrets` > `Create` as below.

![Secret](static/secret.png?raw=true)

Provide the below information.

```
Name - <Name for secret>
Namespace - <Your pipeline namespace>
Access To - Docker registry>
username - <registry user name>
Password/Token - <registry password or token>
Service account - kabanero-pipeline
```

- You will see a secret like this once created.

![Docker registry secret](static/docker_registry_secret.png?raw=true)

#### Create Webhook for the app repo

- For the Github repo, create the webhook as follows. To create a webhook, in the menu select `Webhooks` > `Create webhook`

We will have below

![Webhook](static/webhook.png?raw=true)

Provide the below information.

```
Name - <Name for webhook>
Repository URL - <Your github repository URL>
Access Token - <For this, you need to create a Github access token with permission `admin:repo_hook` or select one from the list>
```

To know more about how to create a personal access token, refer [this](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line).

- Now, enter the pipeline details.

![Pipeline Info](static/pipeline_info.png?raw=true)

- Once you click create, the webhook will be generated.

![Inv Webhook](static/webhook_inv.png?raw=true)

- You can also see in the app repo as follows.

![Inv Repo Webhook](static/webhook_inv_repo.png?raw=true)

#### Deploy the app

Whenever we make changes to the repo, a pipeline run will be triggered and the app will be deployed to the openshift cluster.

- To verify if it is deployed, run below command.

```
oc get pods
```

If it is successful, you will see something like below.

```
$ oc get pods
NAME                                   READY   STATUS    RESTARTS   AGE
inventory-ms-spring-5bd8dcb784-b5l2c   1/1     Running   0          19h
inventory-mysql-8667979689-6qfkz       1/1     Running   0          8d
```

- You can access the app as below.

```
oc get route
```

This will return you something like below.

```
$ oc get route
NAME                  HOST/PORT                                                                                                                      PATH   SERVICES              PORT       TERMINATION   WILDCARD
inventory-ms-spring   inventory-ms-spring-storefront.csantana-demos-ocp43-fa9ee67c9ab6a7791435450358e564cc-0000.us-east.containers.appdomain.cloud          inventory-ms-spring   8080-tcp                 None
```

- Grab the route and hit `/micro/inventory` which returns you a list of items.

For instance it will be http://inventory-ms-spring-storefront.csantana-demos-ocp43-fa9ee67c9ab6a7791435450358e564cc-0000.us-east.containers.appdomain.cloud/micro/inventory

## Conclusion

You have successfully deployed and tested the Inventory Microservice and a MySQL database on Openshift using IBM Cloud Paks for Apps.

To see the Inventory application working in a more complex microservices use case, checkout our Microservice Reference Architecture Application [here](https://github.com/ibm-garage-ref-storefront/refarch-cloudnative-storefront).
