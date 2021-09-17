FROM gradle:7.2.0-jdk16

WORKDIR /matches

COPY ./ ./

ARG actor
ARG token

ENV GITHUB_ACTOR=$actor
ENV GITHUB_TOKEN=$token
ENV GRADLE_USER_HOME="/var/lib/gradle"
ENV GRADLE_OPTS="-Dorg.gradle.project.buildDir=/tmp/gradle-build -Dorg.gradle.jvmargs=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

RUN gradle clean build --no-daemon

EXPOSE 8080
EXPOSE 5005

CMD gradle bootRun --no-daemon