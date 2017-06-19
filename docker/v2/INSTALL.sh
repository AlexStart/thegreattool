mkdir ~/javacloud
cd ~/javacloud
git clone ssh://git@borr.sam-solutions.net:8887/jcc/thegreattool.git
cd ./docker/v2
sudo docker-compose build # it takes a while... )
# Start...
sudo docker-compose up -d
