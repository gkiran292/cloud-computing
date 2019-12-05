#! /bin/bash
sh -su
apt-get -y update
apt-get -y install git
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
nfs_server = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-server -H "Metadata-Flavor: Google")
nfs_dir = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/nfs-dir -H "Metadata-Flavor: Google")
component = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")
kv_store = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/kv-store -H "Metadata-Flavor: Google")
master = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/master -H "Metadata-Flavor: Google")
uuid = $(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/uuid -H "Metadata-Flavor: Google")

# excute the run_script in background
nohup sh ~/cloud-computing/run_mapper_reducer.sh -n "$(nfs_server)" -s "$(nfs_dir)" -p "$(component)" -k "$(kv_store)" -m "$(master)" -u "$(uuid)" &
#Return
