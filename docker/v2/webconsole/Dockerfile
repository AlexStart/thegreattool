FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml