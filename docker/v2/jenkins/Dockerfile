FROM jenkins
ENV JENKINS_OPTS "--httpPort=8082 --prefix=/jenkins"
ARG jenkins_install_wizard=false 
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=$jenkins_install_wizard
EXPOSE 8082
#
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

# Maven
ARG MAVEN_VERSION=3.5.0
ARG SHA=beb91419245395bd69a4a6edad5ca3ec1a8b64e41457672dc687c173a495f034
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries


RUN wget -O /tmp/apache-maven-${MAVEN_VERSION}.tar.gz http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && mkdir -p /var/jenkins_home/maven3 \
    && tar xzf /tmp/apache-maven-${MAVEN_VERSION}.tar.gz -C /var/jenkins_home/maven3/
#    && rm -f /tmp/apache-maven-${MAVEN_VERSION}.tar.gz


#RUN echo "${SHA}  /var/jenkins_home/apache-maven-3.5.0-bin.tar.gz" | sha256sum -c -
#RUN tar -xzf apache-maven-3.5.0-bin.tar.gz


#RUN curl -fsSL -o /var/jenkins_home/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
#  && echo "${SHA}  /var/jenkins_home/apache-maven.tar.gz" | sha256sum -c - \
#  && cd /var/jenkins_home \
#  && tar -xzf /var/jenkins_home/apache-maven.tar.gz
#  && rm -f /tmp/apache-maven.tar.gz

#ENV MAVEN_CONFIG /var/jenkins_home/apache-maven-$MAVEN_VERSION/ref
#ENV PATH $PATH:/var/jenkins_home/apache-maven-$MAVEN_VERSION/bin

#COPY mvn-entrypoint.sh /var/jenkins_home/apache-maven-$MAVEN_VERSION/bin/mvn-entrypoint.sh
#COPY settings-docker.xml /var/jenkins_home/apache-maven-$MAVEN_VERSION/ref/

#VOLUME "/var/jenkins_home/.m2"

#RUN /usr/local/bin/mvn-entrypoint.sh
#RUN mvn -version

#
CMD ["/usr/local/bin/jenkins.sh"]
