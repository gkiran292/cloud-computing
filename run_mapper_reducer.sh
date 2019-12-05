#! /bin/bash
sudo apt-get -y install default-jdk
sudo apt-get -y install maven

while getopts "c:k:m:u:" opt
do
   case "$opt" in
      p ) component="$OPTARG" ;;
      k ) kv_store="$OPTARG" ;;
      m ) master="$OPTARG" ;;
      u ) uuid="$OPTARG" ;;
   esac
done

cd /usr/app/cloud-computing/$component
mvn clean compile assembly:single

# execute task
java -jar target/$component-1.0-SNAPSHOT-jar-with-dependencies.jar -k "$kv_store" -m "$master" -u "$uuid"
