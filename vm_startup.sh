#! /bin/bash
sudo apt-get -y update
sudo apt-get -y install git
sudo mkdir /usr/app
sudo chmod 777 /usr/app
cd /usr/app
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
component=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")
kv_store=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/kv-store -H "Metadata-Flavor: Google")
master=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/master -H "Metadata-Flavor: Google")
uuid=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/uuid -H "Metadata-Flavor: Google")
script=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/script -H "Metadata-Flavor: Google")

# excute the run_script in background
nohup sh /usr/app/cloud-computing/$script -c "$component" -k "$kv_store" -m "$master" -u "$uuid" &
#Return
