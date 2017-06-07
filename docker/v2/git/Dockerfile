FROM alpine

RUN git config --global user.name "JavaCloud" && git config --global user.email "javacloud@sam-solutions.com" 

EXPOSE 9418

# TODO base-path is hardcoded..
CMD ["nohup", "git", "daemon", "--reuseaddr", "--base-path=/javacloud/datavolume/.local_repositories", "--export-all", "--enable=receive-pack", ">", "/var/log/git-daemon.log", "&"]