# Logback Elastic Appender
Appender for the ch.qos.logback logger to send logs directly to the Elasticsearch server.

## Maven
Use Apache Maven to build and install
```
mvn clean install
```

After that, add Logback Elastic Appender to your project
```xml
<dependency>
  <groupId>ru.gnkoshelev.elastic-logger</groupId>
  <artifactId>logback-elastic-appender</artifactId>
  <version>0.3.0</version>
</dependency>
```

## logback.xml example
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>

  <appender name="consoleAppender" class="ch.qos.logback.core.ConsoleAppender">
      <encoder>
          <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg %n
          </Pattern>
      </encoder>
      <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
          <level>TRACE</level>
      </filter>
  </appender>

  <appender name="elasticAppender" class="ru.gnkoshelev.elastic.logger.logback.LogbackElasticAppender">
    <Configuration class="ru.gnkoshelev.elastic.logger.logback.LogbackElasticConfiguration">
       <shutdownTimeoutMs>5000</shutdownTimeoutMs>
       <batchSize>100</batchSize>
       <periodMs>2000</periodMs>
       <capacity>1000000</capacity>
       <threadCount>1</threadCount>
       <indexPattern>index_prefix_here-%d</indexPattern>
       <url>http://localhost:8080/</url>
       <apiKey>ELK applicationKeyHere</apiKey>
       <retryCount>3</retryCount>
    </Configuration>
  </appender>

  <root>
    <level value="INFO"/>
    <appender-ref ref="elasticAppender"/>
    <appender-ref ref="consoleAppender"/>
  </root>
</configuration>
```