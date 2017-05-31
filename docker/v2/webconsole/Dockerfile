FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

# Install Open JDK 8
RUN apt-get update && \
apt-get install -y openjdk-8-jdk

# Install Maven and Gradle 
#RUN apt-get update && apt-get install -y maven && apt-get install -y gradle

# Maven 
ARG MAVEN_VERSION=3.5.0 
ARG SHA=beb91419245395bd69a4a6edad5ca3ec1a8b64e41457672dc687c173a495f034 
RUN wget -O /tmp/apache-maven-${MAVEN_VERSION}.tar.gz http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \ 
&& tar xzf /tmp/apache-maven-${MAVEN_VERSION}.tar.gz -C /tmp \ 
&& echo "${SHA}  /tmp/apache-maven-3.5.0.tar.gz" | sha256sum -c - 

ENV MAVEN_HOME /tmp/apache-maven-${MAVEN_VERSION} 
ENV PATH $PATH:$MAVEN_HOME/bin 

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