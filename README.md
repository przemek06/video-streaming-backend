# Video Streaming Backend Application
Application enabling users to upload and watch videos. 

## Setup
First, you need to create .env file in resources directory. In this file, you need to give information required to connect to MySql database and Firebase Storage project.
There needs to be at least one user in database for this application to be usable. To build and run this applcation, please use Maven in project's root directory:</br>
```
mvn clean package
```
And then execute .jar file created in target directory.
```
java -jar target/videostreaming-0.0.1-SNAPSHOT.jar
```

## Technologies
Main technologies used in this project:
* Spring Webflux
* Spring Firebase Admin
* Spring R2DBC
* Spring Security
* jjwt API

## Features
Main application featues:
* Authenticate with username and password
* Upload videos and store them with Firebase Storage safely and reliably
* Upload images to represent uploaded videos
* Get signed URLs enabling You to watch uploaded videos and retrieve photos

To do:
* User registration
