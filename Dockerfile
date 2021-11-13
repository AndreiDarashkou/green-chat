FROM gradle:jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle bootJar --no-daemon

FROM postgres:14.1-alpine

CMD ["java", "-version"]

RUN apk update && apk upgrade
RUN apk add openjdk17=17.0.1_p12-r0

CMD ["java", "-version"]

EXPOSE 6565

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/

ENTRYPOINT ["java","-jar","/app/green-chat.jar"]