
## Technologies Used
Project is created with:

* Java 17
* Spring Boot version 3.0.4
* Spring Data JPA
* H2 Database
* JUnit, Mockito
* Swagger
* Docker
* Lombok


## How to run

To get this project up and running, navigate to root directory of an application and execute following commands:

* Create a jar file.
```
$ mvn package
```

* Then build docker image using already built jar file.

```
$ docker build -t taskmanager .

```

* Run whole setup

```
$ docker run -p 8080:8080 taskmanager
```
You can open Swagger UI at address:
http://localhost:8080/swagger-ui/index.html



## Room for Improvement

* Implementing filtering using Criteria API in TaskController class.


