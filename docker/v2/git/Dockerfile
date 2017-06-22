FROM alpine

RUN apk update && apk add git && apk add git-daemon

RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

EXPOSE 9418

# TODO base-path is hardcoded..
CMD ["nohup", "git", "daemon", "--reuseaddr", "--base-path=/javacloud/datavolume/.local_repositories", "--export-all", "--enable=receive-pack", "/javacloud/datavolume/.local_repositories", ">", "/var/log/git-daemon.log", "&"]