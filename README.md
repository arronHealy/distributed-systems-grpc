# Distributed Systems GMIT 2019 Dropwizard - GRPC - Password Service

This repo contains my code for module Distributed systems. 
The aim of the project was to provide a password service to users via a Dropwizard REST API that communicates with a GRPC Server to handle users password info.

All project dependencies in pom.xml files.

## The project is broken into 2 parts:

# 1. Java GRPC Server

The GRPC Server will take a users password and hash it. Client/Server code stubs are generated through a .proto file which descrobes the info you would like to send.

* To run GRPC Server download folder to location of your choosing locally and import as maven project in chosen text editor.
* Make sure you have java 1.7 or above installed. 
* If project imported successfully run maven project as maven generate-sources to generate GRPC client/server code this will be done.
* If this is successful run maven project as maven build to generate a .jar file target folder or if using eclipse you could export as runnable jar.
* If project is successfully built as .jar file you can now run via command prompt in .jar files folder java -jar "your-jarfile".jar
* Project server should be running on port 5000 as default ready for client connections.


# 2. Java Dropwizard REST API

The Dropwizard REST API will receive HTTP requests to manage user data and communicate with GRPC Server to manage hashing of users passwords.

* To run Dropwizard Service download folder as above.
* As above Java requirements.
* This project also makes use of the GRPC client code so if imported run project generate-sources again.
* This project contains a .yaml file which describes configuration of Dropwizard server must be included when running service.
* If generated-sources was successful run project as maven build or package.
* If project built .jar file should be in target folder.
* To run API enter in separate command prompt java -jar target/"your-jarfile".jar server userApiConfig.yaml
* Project should be running on port 9000 and ready for HTTP Requests. "localhost:9000/user"

# API Description:

To see a description of the REST API Endpoints follow link:

http://swaggerhub.com/apis/arron6/DropWizard-GRPC-User-Password-Service-API/1#free
