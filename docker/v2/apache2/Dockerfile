#FROM httpd:alpine

#RUN \
#  apt-get update && \
#  apt-get install -y apache2 && \
#  a2enmod proxy && \
#  a2enmod proxy_http
  #cp /usr/local/apache2/conf/extra/httpd-vhosts.conf /usr/local/apache2/conf/extra/httpd-vhosts.conf.original
  
#COPY ./httpd-vhosts.conf /usr/local/apache2/conf/extra

#CMD ["apachectl", "restart"]

#RUN service apache2 restart

FROM ubuntu

RUN \
  apt-get update && \
  apt-get install -y apache2 && \
  a2enmod proxy && \
  a2enmod proxy_http && \
  echo "ServerName localhost" | sudo tee /etc/apache2/conf-available/fqdn.conf && \
  a2enconf fqdn

CMD ["apachectl", "start"]