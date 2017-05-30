FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

# TODO
RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/javacloud-jenkins:8082\/jenkins -Ddb.mysql.url=jdbc:mysql:\/\/javacloud-mysql:3306 -Ddb.mongo.host=javacloud-mongodb -Dgit.remote.server.port=9418 -DTMPDIR=/tmp/g' /usr/local/tomcat/bin/catalina.sh

# Install Maven and Gradle 
RUN apt-get update && apt-get install -y maven && apt-get install -y gradle

# Install GIT 
RUN apt-get update && apt-get install -y git 
RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 
EXPOSE 9418

RUN nohup git daemon --reuseaddr --base-path=$HOME/.local_repositories --export-all --enable=receive-pack > /var/log/git-daemon.log &

ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml