
1. config server 기동이 가장 먼저되어야한다.
> cd configserver
> ./gradlew bootRun 

다만, cloud bus 사용하게 된다면 config server전에 docker 이용해서 rabbitMQ 실행

2. eureka server 기동 
> cd eureka
> ./gradlew bootJar
> java -jar xxxxxxx.jar 
> java -jar build/libs/xxxx.jar 

3. api gateway 기동
> cd apigateway
> ./gradlew bootJar
> java -jar xxxxxxx.jar 

4. 각 서비스객체가 기동(user, product, order) 
> cd user, product, order
> ./gradlew bootJar
> java -jar xxxxxxx.jar

ps) 특이사항 
order 기동시 Kafak(비동기) - zookeeper - docker-compose  


endpoint)
http://localhost:port/user-service/user/singIn
http://localhost:port/product-service/product/create
http://localhost:port/order-service/order/create 






