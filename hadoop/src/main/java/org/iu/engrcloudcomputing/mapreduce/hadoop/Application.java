package org.iu.engrcloudcomputing.mapreduce.hadoop;

import com.google.cloud.storage.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.iu.engrcloudcomputing.mapreduce.hadoop.autogenerated.KeyValueStoreGrpc;
import org.iu.engrcloudcomputing.mapreduce.hadoop.autogenerated.Keyvalue;
import org.iu.engrcloudcomputing.mapreduce.hadoop.exception.KVStoreStorageException;
import org.iu.engrcloudcomputing.mapreduce.hadoop.helper.Constants;
import org.iu.engrcloudcomputing.mapreduce.hadoop.manager.impl.HadoopManagerImpl;
import org.iu.engrcloudcomputing.mapreduce.hadoop.manager.spi.HadoopManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static ExecutorService executorService = Executors.newFixedThreadPool(50);

    private static final String INITIAL_KEY = "master";

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException, GeneralSecurityException, ExecutionException {

        String propFilePath;
        try {
            propFilePath = args[0];
        } catch (Exception e) {
            LOGGER.error("Property file missing");
            throw new IOException("Property file not found");
        }

        LOGGER.debug("Loading values from the properties file: {}", propFilePath);

        InputStream input = new FileInputStream(propFilePath);
        Properties properties = new Properties();
        properties.load(input);

        int kvStorePort = Integer.parseInt(properties.getProperty("kvstore.Port"));
        LOGGER.debug("Key-Value Store port: {}", kvStorePort);

        String kvStoreComponent = properties.getProperty("kvstore.Component");
        LOGGER.debug("Key-Value Component name: {}", kvStoreComponent);

        int masterPort = Integer.parseInt(properties.getProperty("master.Port"));
        LOGGER.debug("Master masterPort: {}", masterPort);

        int mappers = Integer.parseInt(properties.getProperty("mapper.Count"));
        LOGGER.debug("Number of mappers: {}", mappers);
        int reducers = Integer.parseInt(properties.getProperty("reducer.Count"));
        LOGGER.debug("Number of reducers: {},", reducers);

        // "/Users/gkiran/Documents/git-gkiran292/mapred/target/mapred-1.0-SNAPSHOT-jar-with-dependencies.jar"
        String masterComponent = properties.getProperty("master.Component");
        LOGGER.debug("Master component name: {}", masterComponent);

        // "/Users/gkiran/Documents/git-gkiran292/mapper/target/mapper-1.0-SNAPSHOT-jar-with-dependencies.jar"
        String mapperComponent = properties.getProperty("mapper.Component");
        LOGGER.debug("Mapper component name: {}", mapperComponent);

        // "/Users/gkiran/Documents/git-gkiran292/reducer/target/reducer-1.0-SNAPSHOT-jar-with-dependencies.jar"
        String reducerComponent = properties.getProperty("reducer.Component");
        LOGGER.debug("Reducer component name: {}", reducerComponent);

        String inputFileNames = properties.getProperty("master.Files");
        LOGGER.debug("Input file paths for map reduce task: {}", inputFileNames);

        String outputFileName = properties.getProperty("output.File");
        LOGGER.debug("Final Output file: {}", outputFileName);

        HadoopManager hadoopManager = new HadoopManagerImpl();

        LOGGER.info("Initiating cluster with master masterPort: {}", masterPort);
        Map<String, String> vms = hadoopManager.initiateCluster(kvStoreComponent, masterComponent, masterPort, kvStorePort);
        String masterIpAddress = vms.get(masterComponent);
        String kvStoreIpAddress = vms.get(kvStoreComponent);
        LOGGER.debug("Master masterIpAddress: {}", masterIpAddress);

        //Waiting for master and kvstore to complete initiation
        Thread.sleep(60 * 1000);

        ManagedChannel kvStoreChannel = ManagedChannelBuilder.forAddress(kvStoreIpAddress, kvStorePort).usePlaintext().build();
        KeyValueStoreGrpc.KeyValueStoreBlockingStub kvStoreBlockingStub = KeyValueStoreGrpc.newBlockingStub(kvStoreChannel);

        String files = processFiles(inputFileNames);
        Keyvalue.Code initialKey =
                kvStoreBlockingStub.set(Keyvalue.KeyValuePair.newBuilder().setKey(INITIAL_KEY).setValue(files).build());

        if (initialKey.getResponseCode() != 200) {
            LOGGER.error("Couldn't store the initial Key in KV Store with ipAddress: {}, port: {}", kvStoreIpAddress, kvStorePort);
            throw new KVStoreStorageException("Couldn't store the initial key in KV Store");
        }

        LOGGER.info("Cluster initiated with master masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);

        LOGGER.info("Waiting for the master instance to finish up installing prerequisite packages, instance: {}", masterComponent);

        ManagedChannel masterChannel = ManagedChannelBuilder.forAddress(masterIpAddress, masterPort).usePlaintext().build();
        LOGGER.info("Running MapReduce task with master masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);
        List<String> finalKeys = hadoopManager.runMapReduce(masterChannel, kvStoreIpAddress, kvStorePort, masterIpAddress,
                masterPort, mappers, reducers, mapperComponent, reducerComponent, INITIAL_KEY);

        // Store the values fetched from KV Store
        List<String> finalOutput = new ArrayList<>();
        for (String key : finalKeys) {

            Keyvalue.KeyValuePair keyValuePair = kvStoreBlockingStub.get(Keyvalue.Key.newBuilder().setKey(key).build());
            finalOutput.add(keyValuePair.getKey() + " ==> " + keyValuePair.getValue());
        }

        persistInFile(finalOutput, outputFileName);
        LOGGER.info("Persisted result of Map Reduce in file: {}", outputFileName);

        LOGGER.info("Shutting down the cluster with masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);
        LOGGER.info("Shutting down the cluster with masterIpAddress: {}, masterPort: {}", kvStoreIpAddress, kvStorePort);
        boolean isDestroyed = hadoopManager.destroyCluster(masterComponent, kvStoreComponent);

        if (isDestroyed) {
            LOGGER.info("Cluster is successfully destroyed with masterIpAddress: {}, masterPort: {}",
                    masterIpAddress, masterPort);
        }

        LOGGER.info("MapReduce task is completed successfully");
        LOGGER.info("Output file stored successfully in the location: {}", "file://" + System.getProperty("user.dir") + "/" + outputFileName);
        System.exit(0);
    }

    private static String processFiles(String inputFileNames) throws ExecutionException, InterruptedException {

        String[] fileNames = inputFileNames.split(",");
        List<Future<String>> futures = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (String fN : fileNames) {
            futures.add(executorService.submit(() -> uploadFilesToBucket(fN)));
//            names.add(uploadFilesToBucket(fN));
        }

        for (Future<String> future : futures) {
            if (future.get() != null) {
                names.add(future.get());
            }
        }

        return StringUtils.join(names, ",");
    }

    private static String uploadFilesToBucket(String fileName) {

        String[] arr = fileName.split("/");
        String name = arr[arr.length-1];

        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(Constants.PROJECT_ID, name);
        Blob blob = storage.get(blobId);

        if (blob != null && blob.exists()) {
            return name;
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

        try {
            storage.create(blobInfo, Files.readAllBytes(new File(fileName).toPath()));
        } catch (IOException e) {
            LOGGER.error("Error reading the file. File path may be wrong, filePath: {}", fileName);
        }

        return name;
    }

    private static void persistInFile(List<String> input, String filePath) throws IOException {

        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                LOGGER.error("Couldn't create a new file {}", filePath);
                throw new IOException("Couldn't create a new file " + filePath);
            }
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), false));

        for (String s : input) {
            try {
                bw.write(s + "\n");
            } catch (IOException e) {
                LOGGER.error("Couldn't write to file {}", filePath);
            }
        }
        bw.close();
    }
}
