FROM openjdk:8-jre-alpine

ENV PORT_NUMBER 50051
ENV LOG_PATH /home/app/logs
ENV POSTGRES_USER postgre
ENV POSTGRES_PASSWORD $POSTGRE_USER
ENV POSTGRES_SERVER_NAME localhost
ENV POSTGRES_PORT_NUMBER 5432
ENV POSTGRES_DB_NAME $POSTGRE_USER

RUN adduser -D -g '' $POSTGRE_USER

RUN mkdir /home/app
RUN chown -R $POSTGRE_USER /home/app

USER $POSTGRE_USER

COPY ./jars/abc-logger-server-0.9.2-all.jar /home/app/abc-logger-server-0.9.2-all.jar
WORKDIR /home/app
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "abc-logger-server-0.9.2-all.jar"]