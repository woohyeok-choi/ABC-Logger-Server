FROM ubuntu:bionic

EXPOSE 3307

RUN apt-get update && \
    apt-get install -y openssl rsyslog curl

RUN mkdir -p /home/bi/config
RUN mkdir -p /home/bi/logs

WORKDIR /home/bi

RUN curl https://info-mongodb-com.s3.amazonaws.com/mongodb-bi/v2/mongodb-bi-linux-x86_64-ubuntu1804-v2.14.0.tgz -o bi-connector.tgz

RUN tar -xzf bi-connector.tgz && \
    install -m755 mongodb-bi*/bin/mongo* /usr/local/bin

RUN service rsyslog start

ENV CONFIG_PATH /home/bi/config.yml

CMD /bin/sh -c "mongosqld --config $CONFIG_PATH"


