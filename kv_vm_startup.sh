#! /bin/bash
sudo apt-get -y update
sudo apt-get -y install default-jdk
sudo apt-get -y install maven
sudo apt-get -y install nfs-common
sudo apt-get -y install git

nfs_server=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-server -H "Metadata-Flavor: Google")
nfs_dir=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-dir -H "Metadata-Flavor: Google")
port=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/port -H "Metadata-Flavor: Google")
filename=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/filename -H "Metadata-Flavor: Google")
component=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")

sudo mkdir /usr/app
sudo chmod go+rw /usr/app
cd /usr/app
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
sudo mkdir -p $nfs_dir
sudo mount $nfs_server $nfs_dir
sudo chmod go+rw $nfs_dir
cd /usr/app/cloud-computing/$component
mvn clean compile assembly:single

java -jar /usr/app/cloud-computing/$component/target/$component-1.0-SNAPSHOT-jar-with-dependencies.jar -p "$port" -d "file:///$nfs_dir/$filename"
# execute task
