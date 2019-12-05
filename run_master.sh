#! /bin/bash
sh -su
apt-get -y update
apt-get -y install default-jdk
apt-get -y install maven
apt-get -y install nfs-common

while getopts "c:m:" opt
do
   case "$opt" in
      p ) component="$OPTARG" ;;
      m ) master="$OPTARG" ;;
   esac
done

cd /usr/app/cloud-computing/$component
mvn clean compile:assembly single

# execute task
java -jar target/$component-1.0-SNAPSHOT-jar-with-dependencies.jar -m "$master" > logfile.log
