### About project

This is the server for proxy map tiles to openStreet map

### Building

With gradle 5.4 and java 11.0.2 

1. Run gradle build in up directory

2. Move config.json from /api/main/resources to /etc/proxyCache, or you ca change 
this path in api/main/kotlin/ApplicationKt

3. Next  java -jar api/build/libs/api-1.0-SNAPSHOT.jar

### Technologies

* Project Reactor [https://github.com/reactor/reactor] - for non blocking api

* Vertx [https://github.com/eclipse-vertx/vert.x] - as web server

* Rxjava2 [https://github.com/vert-x3/vertx-rx] - for compability with vertx and project reactor
