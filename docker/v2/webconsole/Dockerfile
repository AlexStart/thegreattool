FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

# Install Maven and Gradle 
RUN apt-get update && apt-get install -y maven && apt-get install -y gradle

# Install GIT 
RUN apt-get update && apt-get install -y git 
RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

# TODO
#RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/javacloud-jenkins:8082\/jenkins -Dci.jenkins.postfix=\/jenkins -Ddb.mysql.url=jdbc:mysql:\/\/javacloud-mysql:3306 -Ddb.mongo.host=javacloud-mongodb -Dgit.remote.server.host=javacloud-git -Dgit.remote.server.port=9418 -DTMPDIR=\/tmp/g' /usr/local/tomcat/bin/catalina.sh
RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/localhost:8080\/jenkins -Dci.jenkins.postfix=\/jenkins -Ddb.mysql.url=jdbc:mysql:\/\/javacloud-mysql:3306 -Ddb.mongo.host=javacloud-mongodb -Dgit.remote.server.host=javacloud-git -Dgit.remote.server.port=9418 -DTMPDIR=\/tmp/g' /usr/local/tomcat/bin/catalina.sh

ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml

# TODO temporary!
RUN wget -P /usr/local/tomcat/webapps/ http://mirrors.jenkins-ci.org/war/latest/jenkins.war