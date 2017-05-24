FROM mongo

RUN cp /etc/mongod.conf.orig /etc/mongod.conf \
    && sed -ie 's/bindIp: 127.0.0.1/bindIp: 0.0.0.0/g' /etc/mongod.conf

RUN mongo -version
