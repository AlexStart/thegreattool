FROM httpd

RUN \
  apt-get update && \
  apt-get install -y apache2 && \
  a2enmod proxy && \
  a2enmod proxy_http && \

CMD ["apachectl", "restart"]