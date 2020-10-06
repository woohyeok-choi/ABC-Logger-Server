FROM openjdk:13-alpine

ENV APP_USER abclogger
RUN adduser -D -g '' $APP_USER

ENV PORT_NUMBER 50051
ENV MONGO_SERVER_NAME localhost
ENV MONGO_PORT_NUMBER 27017
ENV MONGO_DB_NAME data
ENV MONGO_ROOT_USER mongo
ENV MONGO_ROOT_PASSWORD mongo
ENV MONGO_WRITE_USER $MONGO_ROOT_USER
ENV MONGO_WRITE_PASSWORD $MONGO_ROOT_PASSWORD
ENV ADMIN_EMAIL abclogger@abclogger
ENV ADMIN_PASSWORD abclogger
ENV ERROR_RECIPIENTS abclogger@abclogger
ENV LOG_PATH /home/abclogger/logs

RUN mkdir -p /home/abclogger
RUN mkdir -p $LOG_PATH
RUN chown -R $APP_USER /home/abclogger

USER $APP_USER

COPY ./jars/abc-logger-server-0.9.7-all.jar /home/abclogger/abc-logger-server.jar

WORKDIR /home/abclogger

CMD ["java", "-server", "-XX:InitialRAMFraction=2", "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:UseContainerSupport", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", "-jar", "abc-logger-server.jar"]