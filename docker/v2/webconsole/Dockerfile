FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/javacloud-jenkins:8082\/jenkins/g' /usr/local/tomcat/bin/catalina.sh

# Install GIT 
RUN apt-get update && apt-get install -y git 
RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 
EXPOSE 9418

ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml