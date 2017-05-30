FROM alpine

RUN apk update && apk add git && apk add git-daemon

RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

EXPOSE 9418

CMD ["tail", "-f", "/dev/null"]