FROM jenkins

#
USER root
RUN mkdir -p /root/.ci_repositories
#RUN apt-get update && apt-get install -y sudo
#RUN mkdir -p /root/.ci_repositories && chown -R jenkins /root/.ci_repositories
# drop back to the regular jenkins user - good practice
USER jenkins

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

RUN wget -O /tmp/apache-maven-${MAVEN_VERSION}.tar.gz http://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
&& tar xzf /tmp/apache-maven-${MAVEN_VERSION}.tar.gz -C /tmp \
&& echo "${SHA}  /tmp/apache-maven-3.5.0.tar.gz" | sha256sum -c -

ENV MAVEN_HOME /tmp/apache-maven-${MAVEN_VERSION}
ENV PATH $PATH:$MAVEN_HOME/bin

#
CMD ["/usr/local/bin/jenkins.sh"]
