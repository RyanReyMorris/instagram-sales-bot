FROM openjdk:17
LABEL authors="ryanreymorris"
WORKDIR /instagram_sales_bot
COPY target/InstagramSalesBot-0.0.1-SNAPSHOT.jar .
EXPOSE 8081 5005
ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "InstagramSalesBot-0.0.1-SNAPSHOT.jar"]