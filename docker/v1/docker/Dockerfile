FROM ubuntu:latest

RUN apt-get update

RUN apt-get -y install software-properties-common python-software-properties

RUN apt-get install wget pwgen

# Install Apache2
RUN apt-get update && apt-get install -y openssh-server apache2

RUN mkdir -p /var/lock/apache2 /var/run/apache2 /var/run/sshd

RUN a2enmod proxy
RUN a2enmod proxy_http

EXPOSE 22 80

WORKDIR /

# Install Java.
RUN add-apt-repository ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java8-installer
RUN apt-get install -y oracle-java8-set-default

# Install Maven and Gradle
RUN apt-get install -y maven
RUN apt-get install -y gradle


# Install Tomcat and copy JavaCloud web application inside
RUN apt-get install -y tomcat8

RUN mv /var/lib/tomcat8/webapps/ROOT/ /var/lib/tomcat8/webapps/_ROOT_

RUN wget http://javacloud.sam-solutions.net:8082/job/cloud-all_master/ws/project/cloud-mvc/target/ROOT.war 
RUN mv ROOT.war /var/lib/tomcat8/webapps

RUN mkdir /javacloud && mkdir /javacloud/.javacloud_home && mkdir /javacloud/.javacloud_home/.local_repositories && chmod -R 777 /javacloud/.javacloud_home && \
mkdir /javacloud/.javacloud_home/tmp_dir

RUN chown -R tomcat8 /javacloud/.javacloud_home

RUN sed -ie 's/#JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Duser.home=\/javacloud\/.javacloud_home -Dgit.remote.server.port=9418 -DTMPDIR=\/javacloud\/.javacloud_home\/tmp_dir/g' /usr/share/tomcat8/bin/catalina.sh

EXPOSE 8080 
EXPOSE 8009

# Install Jenkins
RUN wget -q -O - https://pkg.jenkins.io/debian/jenkins-ci.org.key | apt-key add -
RUN sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
RUN apt-get update && apt-get install -y jenkins
RUN sed -ie 's/8080/8082/g' /etc/default/jenkins
RUN sed -ie 's/-Djava.awt.headless=true/-Djava.awt.headless=true -Djenkins.install.runSetupWizard=false/g' /etc/default/jenkins 

EXPOSE 8082

# Install GIT
RUN apt-get update && apt-get install -y git
RUN git config --global user.name "JavaCloud Docker" && git config --global user.email "youremail@domain.com"

EXPOSE 9418

# Install MySQL
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && apt-get install -y mysql-server

# Install MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 0C49F3730359A14518585931BC711F9BA15703C6 && \
echo "deb [ arch=amd64,arm64 ] http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.4 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.4.list && \
apt-get update && apt-get install -y mongodb-org


# Startup script
RUN mkdir /javacloud/bin && touch /javacloud/bin/javacloud.sh && echo "#!/bin/bash" >> /javacloud/bin/javacloud.sh && echo "EXPORT JAVA_OPTS=\"$JAVA_OPTS -Djavacloud.home=/javacloud/.javacloud_home\"" >> /javacloud/bin/javacloud.sh && \
echo "apachectl start" >> /javacloud/bin/javacloud.sh && echo "service jenkins start" >> /javacloud/bin/javacloud.sh && echo "sleep 10" >> /javacloud/bin/javacloud.sh && echo "service tomcat8 start" >> /javacloud/bin/javacloud.sh && \
echo "nohup git daemon --reuseaddr --base-path=/javacloud/.javacloud_home/.local_repositories --export-all --enable=receive-pack > /var/log/git-daemon.log &" >> /javacloud/bin/javacloud.sh && \
echo "service mysql start" >> /javacloud/bin/javacloud.sh && \
echo "nohup /usr/bin/mongod -f /etc/mongod.conf > /var/log/mongodb/mongo-daemon.log &" >> /javacloud/bin/javacloud.sh && \
echo "tail -f /dev/null" >> /javacloud/bin/javacloud.sh && \
chmod a+x javacloud/bin/javacloud.sh

ENV PATH $PATH:/javacloud/bin

# Define default command.

CMD ["javacloud.sh"]

