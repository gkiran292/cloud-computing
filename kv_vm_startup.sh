#! /bin/bash
sh -su
apt-get -y update
apt-get -y install default-jdk
apt-get -y install maven
apt-get -y install nfs-common
apt-get -y install git

nfs_server = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-server -H "Metadata-Flavor: Google")
nfs_dir = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-dir -H "Metadata-Flavor: Google")
port = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/port -H "Metadata-Flavor: Google")
filename = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/filename -H "Metadata-Flavor: Google")
component = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")

mkdir -p $(nfs_dir)
mount $(nfs_server) $(nfs_dir)
chmod go+rw $(nfs_dir)
cd ~/cloud-computing/$(component)
mvn clean compile:assembly single

nohup java -jar target/$(component)-1.0-SNAPSHOT-jar-with-dependencies.jar -p "$(port)" -d "file:///$(nfs_dir)/$(filename)" &
# execute task
