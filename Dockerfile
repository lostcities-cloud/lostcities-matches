FROM gradle:7.2.0-jdk16

WORKDIR /accounts

COPY ./ ./

RUN gradle clean build --no-daemon

EXPOSE 8080