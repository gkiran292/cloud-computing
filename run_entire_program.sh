#! /bin/bash
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
cd cloud-computing/hadoop
mvn clean compile assembly:single
java -jar target/hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar application.properties