# Log4j2 Elastic Appender
Appender for the Log4J2 logger to send logs directly to the Elasticsearch server.

## Maven
Use Apache Maven to build and install
```
mvn clean install
```

After that, add Logback Elastic Appender to your project
```xml
<dependency>
  <groupId>ru.gnkoshelev.elastic-logger</groupId>
  <artifactId>log4j2-elastic-appender</artifactId>
  <version>0.3.0</version>
</dependency>
```

## log4j2.properties example
```properties
appenders = console, elastic

appender.console.type = Console
appender.console.name = STDOUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = %m%n

appender.elastic.type = Elastic
appender.elastic.name = ELASTIC
appender.elastic.shutdownTimeoutMs = 5000
appender.elastic.batchSize = 100
appender.elastic.periodMs = 2000
appender.elastic.capacity = 1000000
appender.elastic.threadCount = 1
appender.elastic.indexPattern = index_prefix_here-%d
appender.elastic.url = http://localhost:8080/
appender.elastic.apiKey = ELK applicationKeyHere
appender.elastic.retryCount = 3

rootLogger.level = info

rootLogger.appenderRefs = console, elastic
rootLogger.appenderRef.console.ref = STDOUT
rootLogger.appenderRef.elastic.ref = ELASTIC
```