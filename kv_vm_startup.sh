#! /bin/bash
sudo apt-get -y update
sudo apt-get -y install default-jdk
sudo apt-get -y install maven
sudo apt-get -y install nfs-common
sudo apt-get -y install git

port=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/port -H "Metadata-Flavor: Google")
filename=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/filename -H "Metadata-Flavor: Google")
component=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")

sudo mkdir /usr/app
sudo chmod 777 /usr/app
cd /usr/app
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
cd /usr/app/cloud-computing/$component
mvn clean compile assembly:single

java -jar /usr/app/cloud-computing/$component/target/$component-1.0-SNAPSHOT-jar-with-dependencies.jar -p "$port" -d "$filename" > logfile.log
# execute task
