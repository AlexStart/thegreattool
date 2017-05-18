FROM alpine
RUN apk update && apk upgrade
RUN apk add wget
RUN wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" \
http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz
RUN tar -xzf jdk-8u131-linux-x64.tar.gz
RUN mkdir -p "/usr/lib/jvm" && \
mv "jdk1.8.0_131" "/usr/lib/jvm/java-8-oracle"

