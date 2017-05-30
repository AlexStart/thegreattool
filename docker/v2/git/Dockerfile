FROM alpine

RUN mkdir -p $HOME//.local_repositories && apk update && apk add git && apk add git-daemon

RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

EXPOSE 9418

CMD ["nohup", "git", "daemon", "--reuseaddr", "--base-path=$HOME/.local_repositories", "--export-all", "--enable=receive-pack", ">", "/var/log/git-daemon.log", "&"]