FROM openjdk:8-jre-alpine

ENV PORT_NUMBER 50051
ENV LOG_PATH /home/app/logs
ENV POSTGRE_USER postgre
ENV POSTGRE_PASSWORD $POSTGRE_USER
ENV POSTGRE_SERVER_NAME localhost
ENV POSTGRE_PORT_NUMBER 5432
ENV POSTGRE_DB_NAME $POSTGRE_USER

RUN adduser -D -g '' $POSTGRE_USER

RUN mkdir /home/app
RUN chown -R $POSTGRE_USER /home/app

USER $POSTGRE_USER

COPY ./jars/abc-logger-server-0.9.2-all.jar /home/app/abc-logger-server-0.9.2-all.jar
WORKDIR /home/app
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "abc-logger-server-0.9.2-all.jar"]