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


#RUN mkdir -p ~/maven ~/maven/ref \
#  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-$MAVEN_VERSION-bin.tar.gz \
#  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha256sum -c - \
#  && tar -xzf /tmp/apache-maven.tar.gz -C ~/maven --strip-components=1 \
#  && rm -f /tmp/apache-maven.tar.gz \
#  && ln -s ~/maven/bin/mvn /usr/local/bin/mvn

#COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
#COPY settings-docker.xml ~/maven/ref/

VOLUME "~/.m2"

#RUN /usr/local/bin/mvn-entrypoint.sh
#RUN mvn -version

#
CMD ["/usr/local/bin/jenkins.sh"]
