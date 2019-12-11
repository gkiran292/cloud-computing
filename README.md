# cloud-computing
Map Reduce on Cloud (GCP) Report

Note: Code samples which are provided in the report are part of the code. Full code can be found in git repo https://github.com/gkiran292/cloud-computing. I will be uploading the code only without any jars. The submission would contain the shell script which runs the entire process. The cloud-computing folder (this is the main project that is going to be downloaded into your system) contains the input files and it is going to take it by default. If you want to provide the different file then you need to change in the application.properties file. Log files of the respective components can be found in the respective components. Output and input files would be present in the submission.

Master log file found in: mapred
Key-Value Store log file found in: keyvaluestore

User exposed application jar i.e., Hadoop (name of the jar) has the local log. However, this can be seen if you run the script run_entire_program.sh.

This submission would also contain outputs of the program. I have implemented fault tolerant system which prompts the usage of preemptible VMs for mappers and reducers. The Master is not fault tolerant (yet).

To run this code, you need to have the owner access as I need to spawn master and Key-Value store VM from the local machine. After the master and KV Store VMs are spawned the master is going to use the service account for running map reduce task.

Assumptions for running Hadoop application in local:
1.	You are running linux based system
2.	You have java 8 in your system
3.	You have git in your system (Ubuntu: sudo apt-get install git, Mac: brew install git)
4.	You have maven in your system (Ubuntu: sudo apt-get install maven,Mac: brew install maven)
5.	You must be logged into your developer console.

How to run the program once all the requirements are met?
Change the permission of run_entire_program.sh and execute the script 
./run_entire_program.sh

-OR-

If you don’t want to install maven in your system. Then you can run the jar as well. The submission would be containing the jar.

java -jar hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar application.properties

Note: You don’t need to specify the main class as I was given comment in the last assignment. The main class has already been configured in the pom.xml file. I am still mentioning the main class.

org.iu.engrcloudcomputing.mapreduce.hadoop.Application.class

Code that is present in the autogenerated folder of every component is the autogenerated code from protopbuf. I have also copied code from the official google-cloud-samples to start and delete instances, store the input files in the Google cloud storage. Those two snippets were taken from the link below.
Link - https://github.com/googleapis/google-cloud-java

Details of the VM used for different components
Sample VM create json:
{
  "canIpForward": false,
  "cpuPlatform": "Intel Haswell",
  "creationTimestamp": "2019-12-05T13:50:12.831-08:00",
  "deletionProtection": false,
  "description": "",
  "disks": [
    {
      "autoDelete": true,
      "boot": true,
      "deviceName": "kv-store-1",
      "guestOsFeatures": [
        {
          "type": "VIRTIO_SCSI_MULTIQUEUE"
        }
      ],
      "index": 0,
      "interface": "SCSI",
      "kind": "compute#attachedDisk",
      "licenses": [
        "projects/debian-cloud/global/licenses/debian-9-stretch"
      ],
      "mode": "READ_WRITE",
      "source": "projects/gopikiran-talangalashama/zones/us-east1-b/disks/kv-store-1",
      "type": "PERSISTENT"
    }
  ],
  "displayDevice": {
    "enableDisplay": false
  },
  "id": "9046831703322314107",
  "kind": "compute#instance",
  "labelFingerprint": "42WmSpB8rSM=",
  "machineType": "projects/gopikiran-talangalashama/zones/us-east1-b/machineTypes/n1-standard-1",
  "metadata": {
    "fingerprint": "I_9DaGgCOQo=",
    "items": [
      {
        "key": "startup-script-url",
        "value": "gs://gopikiran-talangalashama/kv_vm_startup.sh"
      },
      {
        "key": "port",
        "value": "9000"
      },
      {
        "key": "filename",
        "value": "kvstore.txt"
      },
      {
        "key": "component",
        "value": "keyvaluestore"
      }
    ],
    "kind": "compute#metadata"
  },
  "name": "kv-store-1",
  "networkInterfaces": [
    {
      "accessConfigs": [
        {
          "kind": "compute#accessConfig",
          "name": "External NAT",
          "natIP": "35.231.250.4",
          "networkTier": "PREMIUM",
          "type": "ONE_TO_ONE_NAT"
        }
      ],
      "fingerprint": "AuzytzJ0iSg=",
      "kind": "compute#networkInterface",
      "name": "nic0",
      "network": "projects/gopikiran-talangalashama/global/networks/default",
      "networkIP": "10.142.0.25",
      "subnetwork": "projects/gopikiran-talangalashama/regions/us-east1/subnetworks/default"
    }
  ],
  "reservationAffinity": {
    "consumeReservationType": "ANY_RESERVATION"
  },
  "scheduling": {
    "automaticRestart": true,
    "onHostMaintenance": "MIGRATE",
    "preemptible": false
  },
  "selfLink": "projects/gopikiran-talangalashama/zones/us-east1-b/instances/kv-store-1",
  "serviceAccounts": [
    {
      "email": "my-account@gopikiran-talangalashama.iam.gserviceaccount.com",
      "scopes": [
        "https://www.googleapis.com/auth/cloud-platform"
      ]
    }
  ],
  "startRestricted": false,
  "status": "RUNNING",
  "tags": {
    "fingerprint": "42WmSpB8rSM="
  },
  "zone": "projects/gopikiran-talangalashama/zones/us-east1-b"
}

The same configuration was used for master and Key-Value Store nodes.
For mappers and reducers, the same configuration except VMs are preemptible.

//Scheduling
Scheduling scheduling = new Scheduling();
if (isPreemptable) {
    scheduling.setPreemptible(true);
    scheduling.setOnHostMaintenance("TERMINATE");
    scheduling.setAutomaticRestart(false);
} else {
    scheduling.setPreemptible(false);
    scheduling.setOnHostMaintenance("MIGRATE");
    scheduling.setAutomaticRestart(true);
}
instance.setScheduling(scheduling);

The above code takes care of creating preemptible and non-preemptible VMs.

Detailed design:

1.	The main jar which is going to be run in your local system accepts the local path of the input file/files (present in the application properties file). The first thing the code is going to do is copy the file from the local path to the GCP storage as Blob. The file is downloaded by the master and stores line by line in the key-value store. There are things that I could have done better like KV-Store itself making it generic. I am going to talk about the fallacies in the end.
2.	The main jar that is “hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar” has mainly 3 methods
a.	initiateCluster – Spawns the master and key value store nodes
b.	runMapReduce – Makes the gRPC call to the master VM for launching Map Reduce tasks
c.	destroyCluster – Destroys(delete) the Master and Key-Value store VMs.
3.	Once the initiateCluster is called the system waits for a 2 mins for the instance to be up and install all the dependencies and run the “mapred-1.0-SNAPSHOT-jar-with-dependencies.jar”. Master node exposes gRPC method for the calling jar to initiate runMapReduce.
4.	Question: How does the master run the jar when the application calls initiateCluster which basically just spawns the Master node VM?
-	I am using the capability provided by the GCP to run the startup script when the VM starts. Below is the VM startup script
#! /bin/bash
sudo apt-get -y update
sudo apt-get -y install git
sudo mkdir /usr/app
sudo chmod 777 /usr/app
cd /usr/app
rm -rf cloud-computing
git clone https://github.com/gkiran292/cloud-computing.git
component=$(curlhttp://metadata.google.internal/computeMetadata/v1/instance/attributes/component -H "Metadata-Flavor: Google")
kv_store=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/kv-store -H "Metadata-Flavor: Google")
master=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/master -H "Metadata-Flavor: Google")
uuid=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/uuid -H "Metadata-Flavor: Google")
script=$(curl http://metadata.google.internal/computeMetadata/v1/instance/attributes/script -H "Metadata-Flavor: Google")
echo component-$component,kv_store-$kv_store,master-$master,uuid-$uuid,script-$script
# excute the run_script in background
sh /usr/app/cloud-computing/$script -c "$component" -k "$kv_store" -m "$master" -u "$uuid"

-	I pass the variables like “$component” in the metadata section of the VM creation.

private Metadata getMetaData(String uuid, String componentName) {

    Metadata metadata = new Metadata();
    List<Metadata.Items> itemsList = new ArrayList<>();
    itemsList.add(getItem(STARTUP_SCRIPT_URL_KEY,STARTUP_SCRIPT_URL_VALUE));
    itemsList.add(getItem(KV_STORE_KEY, kvStoreDetails));
    itemsList.add(getItem(MASTER_DETAILS_KEY, masterDetails));
    itemsList.add(getItem(UUID_KEY, uuid));
    itemsList.add(getItem(COMPONENT_NAME_KEY, componentName));
    itemsList.add(getItem(SCRIPT_KEY, script));
    metadata.setItems(itemsList);
    return metadata;
}

-	This method poses another question: Would the method createInstance return after the startup script provided is executed or would it return when the VM is started?
o	The method createInstance from the google-client-library will return when the instance is started, and I need to do Thread.sleep(120*1000) for the current thread to sleep for 2 mins so that the master node finishes up all the installation and runs the jar.
-	The same is done with the key-value store during initiate cluster.
5.	Now that our master and key-value store are up and running. We call the runMapReduce method for running map reduce task.
-	The master application downloads the input files from the google storage and assigns each mappers number of lines.
-	How does the master do it?
o	Master divides the total lines in the files with the number of mappers.
-	Master then waits for specific time for the mappers to complete their tasks. Now if the mappers won’t respond in the time specified. The Master deletes the existing mapper and spawns the new mapper with the same mapper id.
-	Now what if the unresponsive mappers send the reply?
o	If the unresponsive mapper sends the reply, then it is handled in the code. Basically, the response is stored and marked as true. So now the reply from the new mapper is discarded.
-	Mapper instances are destroyed after the master reply from all the mappers.
-	Now it is time for the reducers to be spawned.
-	Master segregates the unique keys and assigns it to the reducers based on the number of number of reducers.
-	Master waits for the reducers to finish up their job.
-	All the intermediate data is being stored in the key-value store.
-	For-example: data from mapper1 is stored in the kv store as m1_word = 4.
-	Once the reducers have finished up their tasks. We have the data in the kv store.
-	For-example: data from reducer1 is stored in the kv store as r1_word = 6 (because reducer collects “word” from all the mappers)
-	Reducers are destroyed once master receives the data.
-	Now the master returns the keys to the Hadoop (local application) application.
6.	Now that Hadoop application has the keys. It queries the KV store for the value and stores it in the file specified in the application properties file.
7.	Now the application calls destroy cluster method which deletes the master and key value store instances.

Above is the overall steps that is being performed.

Waiting for the instances to be up and running code snippet. It launches another VM if the VM fails

    boolean hasAllComponentsNotFinished = true;
    spawnTaskProcesses(taskMap, componentName);
    Thread.sleep(10 * 1000);

    //wait for instances to start 
    while (hasAllComponentsNotFinished) {
        hasAllComponentsNotFinished = false;

        for (Map.Entry<String, TaskInfo> entry : taskMap.entrySet()) {
            TaskInfo value = entry.getValue();
            int status = value.getFuture().get();
            String key = entry.getKey();

            if (status != 0) {
                LOGGER.warn("Component failed or timed out uuid: {}", key);
                spawnTask(taskMap, key, value.getInput(), componentName);
                hasAllComponentsNotFinished = true;
            }
        }
    }

Fault tolerance handling code snippet

private void handleTasks(String componentName) throws ExecutionException, InterruptedException {
    boolean isRunning = taskInfoConcurrentMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsTaskFinished());
    while (!isRunning) {
        waitAndRetryTasks(taskInfoConcurrentMap, componentName);

        try {
            executorService.submit(() -> {
                boolean bool = true;
                while (bool) {
                    bool = !taskInfoConcurrentMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsTaskFinished());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            }).get(Constants.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ignored) {}
        finally {
            isRunning = taskInfoConcurrentMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsTaskFinished());
        }
    }
}

Future Improvements:

1.	Need to improve Key-Value store. Currently, it is storing the key and value in the file as strings. The code entirely fails if at all some serializable object is given, which is pretty bad!!!
2.	I had a short discussion with Dr. Prateek Sharma on how I wait for the mappers to complete before triggering another mapper. He suggested me to do a ping mechanism. I need to create another grpc method just to give out the status. Currently, the code waits for 6 mins for the all the mappers and reducers to complete their job respectively.
Test Cases:

1.	I used 5 mappers and 3 reducers for all the tests
2.	I have tested the code on default file that is there in the folder. It took about 17 minutes to run the entire job.
 
3.	In the picture above you can see that one of the mappers timed out. So, the master instantiated another mapper. The same is applicable for reducers too.
4.	The submission contains the sample output of inverted index mapper and reducer jobs. Also, log files of Master, Key-Value Store, Hadoop application. I couldn’t get the log files of the mappers and reducers as it is hard to do. Here, I could run the program in the debug mode, go the master system copy the file and upload it into git. I wasn’t able to do it for mappers and reducers as I don’t have control over from the local application.

 
5.	I have used Debian-9-stretch for all the instances.
6.	Weird observations in the VM:

 

-	In the above picture, the server says “kernel reports TIME_ERROR: 0x41 Clock Unsynchronized”
I don’t know why this is happening!!!
This is not affecting the application though!
Some of the failures while developing this and the approach:

Before arriving to the decision of using Google Storage for storing input files, I was exploring NFS (Google File Store) for storing and sharing files between instances. I was somehow not able to save it properly. Hence, I moved to GFS. Note that I am using GFS only for storing the input files, all the intermediate data required by the mapper, reducer and master are stored in KV store.

I was kind of over-confident on this assignment as I was sure that I had to make the minimum changes because I thought I had my previous assignment modularized. But I was only 50% right as I had to make good number of changes in the code. Besides creating and deleting instances, I had to take care of Fault Tolerance which I had not taken care in the previous assignment.
I can confidently say that the design of the entire framework works well and also being susceptible to the future changes with minimal code change as the components are modularized. However, I am kind of disappointed with myself as I was not able to design a generic KV store in the beginning. Given some time I could really improve upon this as I had learnt a lot in the entire series of assignments. Yes! Key value store is the bottle neck. Moreover, I made a design mistake in the first assignment for the key value store. Now the key value store considered primitive types only (float, int, double, long, string). I should have made it to consider any serializable objects. I still have some design issues in the KV store. I hope I could meet you after the exam for some of the questions that I have, so that I could improve upon it during the winter break. I think it would add a lot of value in my resume.

Final TODO list:
1.	Make Key Value store generic – it should store serializable objects
2.	Expose a GRPC service in mappers so that server can get the status of the mapper by pinging it instead of waiting.
