# Docker File
FROM openjdk:21-ea-24-oracle

WORKDIR /app

COPY target/usuario-0.0.1-SNAPSHOT.jar app.jar

COPY Wallet_B15D60FA8BF9RULL /app/oracle_wallet/

EXPOSE 8080

CMD ["java","-jar","app.jar"]