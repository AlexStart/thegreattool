FROM httpd

RUN echo "LoadModule proxy_module modules/mod_proxy.so" >> /usr/local/apache2/conf/httpd.conf && \
echo "LoadModule proxy_http_module modules/mod_proxy_http.so" >> /usr/local/apache2/conf/httpd.conf && \
echo "ServerName localhost" >> /usr/local/apache2/conf/httpd.conf


#RUN \
#  apt-get update && \
#  apt-get install -y apache2 && \
#  a2enmod proxy && \
#  a2enmod proxy_http
  #cp /usr/local/apache2/conf/extra/httpd-vhosts.conf /usr/local/apache2/conf/extra/httpd-vhosts.conf.original
  
#COPY ./httpd-vhosts.conf /usr/local/apache2/conf/extra

#CMD ["apachectl", "restart"]

RUN apachectl restart

#FROM ubuntu

#RUN \
#  apt-get update && \
#  apt-get install -y apache2 && \
#  a2enmod proxy && \
#  a2enmod proxy_http && \
#  echo "ServerName localhost" | tee /etc/apache2/conf-available/fqdn.conf && \
#  a2enconf fqdn

#CMD ["apachectl", "start"]