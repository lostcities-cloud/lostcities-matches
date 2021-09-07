FROM gradle:7.2.0-jdk16

WORKDIR /accounts

COPY ./ ./

ARG actor
ARG token

ENV GITHUB_ACTOR=$actor
ENV GITHUB_TOKEN=$token

RUN gradle clean build --no-daemon

EXPOSE 8080

CMD gradle bootRun --no-daemon