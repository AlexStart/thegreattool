FROM tomcat

RUN apt-get update && apt-get install -y --no-install-recommends vim

# Install Open JDK 8
RUN apt-get update && \
apt-get install -y openjdk-8-jdk

# Install Maven and Gradle 
#RUN apt-get update && apt-get install -y maven && apt-get install -y gradle

# Install Maven 
ARG MAVEN_VERSION=3.5.0 
ARG SHA=beb91419245395bd69a4a6edad5ca3ec1a8b64e41457672dc687c173a495f034 
RUN wget -O /tmp/apache-maven-${MAVEN_VERSION}.tar.gz http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \ 
&& tar xzf /tmp/apache-maven-${MAVEN_VERSION}.tar.gz -C /tmp \ 
&& echo "${SHA}  /tmp/apache-maven-3.5.0.tar.gz" | sha256sum -c - 

ENV MAVEN_HOME /tmp/apache-maven-${MAVEN_VERSION} 
ENV PATH $PATH:$MAVEN_HOME/bin 

# Install Gradle
ENV GRADLE_HOME /opt/gradle
ENV GRADLE_VERSION 3.5

ARG GRADLE_DOWNLOAD_SHA256=0b7450798c190ff76b9f9a3d02e18b33d94553f708ebc08ebe09bdf99111d110
RUN set -o errexit -o nounset \
	&& echo "Downloading Gradle" \
	&& wget --no-verbose --output-document=gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
	\
	&& echo "Checking download hash" \
	&& echo "${GRADLE_DOWNLOAD_SHA256} *gradle.zip" | sha256sum --check - \
	\
	&& echo "Installing Gradle" \
	&& unzip gradle.zip \
	&& rm gradle.zip \
	&& mv "gradle-${GRADLE_VERSION}" "${GRADLE_HOME}/" \
&& ln --symbolic "${GRADLE_HOME}/bin/gradle" /usr/bin/gradle

# Install GIT 
RUN apt-get update && apt-get install -y git 
RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

# TODO
#RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/javacloud-jenkins:8082\/jenkins -Dci.jenkins.postfix=\/jenkins -Ddb.mysql.url=jdbc:mysql:\/\/javacloud-mysql:3306 -Ddb.mongo.host=javacloud-mongodb -Dgit.remote.server.host=javacloud-git -Dgit.remote.server.port=9418 -DTMPDIR=\/tmp/g' /usr/local/tomcat/bin/catalina.sh
#RUN sed -ie 's/JAVA_OPTS="$JAVA_OPTS/JAVA_OPTS="$JAVA_OPTS -Dci.jenkins.host=http:\/\/localhost:8080\/jenkins -Dci.jenkins.postfix=\/jenkins -Ddb.mysql.url=jdbc:mysql:\/\/javacloud-mysql:3306 -Ddb.mongo.host=javacloud-mongodb -Dgit.remote.server.host=javacloud-git -Dgit.remote.server.port=9418 -DTMPDIR=\/tmp/g' /usr/local/tomcat/bin/catalina.sh

ADD tomcat-users.xml /usr/local/tomcat/conf/

ADD context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml

ADD cloud-override.properties $HOME/cloud-override.properties

# TODO temporary!
RUN wget -P /usr/local/tomcat/webapps/ http://mirrors.jenkins-ci.org/war/latest/jenkins.war