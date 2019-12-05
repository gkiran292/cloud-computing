#! /bin/bash
sh -su
apt-get -y install default-jdk
apt-get -y install maven
apt-get -y install nfs-common

while getopts "n:s:c:k:m:u:" opt
do
   case "$opt" in
      n ) nfs_server="$OPTARG" ;;
      s ) nfs_dir="$OPTARG" ;;
      p ) component="$OPTARG" ;;
      k ) kv_store="$OPTARG" ;;
      m ) master="$OPTARG" ;;
      u ) uuid="$OPTARG" ;;
   esac
done

mkdir -p $(nfs_dir)
mount $(nfs_server) $(nfs_dir)
chmod go+rw $(nfs_dir)
cd ~/cloud-computing/$(component)
mvn clean compile:assembly single

# execute task
java -jar target/$(component)-1.0-SNAPSHOT-jar-with-dependencies.jar -s "$(nfs_dir)" -k "$(kv_store)" -m "$(master)" -u "$(uuid)"
