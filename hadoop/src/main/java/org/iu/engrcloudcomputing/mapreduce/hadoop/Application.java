package org.iu.engrcloudcomputing.mapreduce.hadoop;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.iu.engrcloudcomputing.mapreduce.hadoop.autogenerated.KeyValueStoreGrpc;
import org.iu.engrcloudcomputing.mapreduce.hadoop.autogenerated.Keyvalue;
import org.iu.engrcloudcomputing.mapreduce.hadoop.exception.KVStoreStorageException;
import org.iu.engrcloudcomputing.mapreduce.hadoop.manager.impl.HadoopManagerImpl;
import org.iu.engrcloudcomputing.mapreduce.hadoop.manager.spi.HadoopManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessResult;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private static final String INITIAL_KEY = "master";

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, URISyntaxException, GeneralSecurityException {

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

        String kvStoreAddress = properties.getProperty("kvstore.IpAddress");
        LOGGER.debug("Key-Value Store ipAddress: {}", kvStoreAddress);

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

        String inputFilePaths = properties.getProperty("master.FilePaths");
        LOGGER.debug("Input file paths for map reduce task: {}", inputFilePaths);

        String fileUri = properties.getProperty("output.FileUri");
        LOGGER.debug("Final Output file URI: {}", fileUri);

        String workingFolder = properties.getProperty("working.Folder");
        LOGGER.debug("Working folderPath: {}", workingFolder);

        String nfsServerDetails = properties.getProperty("nfs.ServerDetails");
        LOGGER.debug("NFS Server details: {}", nfsServerDetails);

        ManagedChannel kvStoreChannel = ManagedChannelBuilder.forAddress(kvStoreAddress, kvStorePort).usePlaintext().build();
        KeyValueStoreGrpc.KeyValueStoreBlockingStub kvStoreBlockingStub = KeyValueStoreGrpc.newBlockingStub(kvStoreChannel);

        HadoopManager hadoopManager = new HadoopManagerImpl();

        LOGGER.info("Initiating cluster with master masterPort: {}", masterPort);
        String masterIpAddress = hadoopManager.initiateCluster(masterComponent, nfsServerDetails, workingFolder, masterPort);
        LOGGER.debug("Master masterIpAddress: {}", masterIpAddress);

        Keyvalue.Code initialKey =
                kvStoreBlockingStub.set(Keyvalue.KeyValuePair.newBuilder().setKey(INITIAL_KEY).setValue(inputFilePaths).build());

        if (initialKey.getResponseCode() != 200) {
            LOGGER.error("Couldn't store the initial Key in KV Store with ipAddress: {}, port: {}", kvStoreAddress, kvStorePort);
            throw new KVStoreStorageException("Couldn't store the initial key in KV Store");
        }

        LOGGER.info("Cluster initiated with master masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);

        Thread.sleep(1000);
        ManagedChannel masterChannel = ManagedChannelBuilder.forAddress(masterIpAddress, masterPort).usePlaintext().build();
        LOGGER.info("Running MapReduce task with master masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);
        List<String> finalKeys = hadoopManager.runMapReduce(masterChannel, kvStoreAddress, kvStorePort, masterIpAddress,
                masterPort, mappers, reducers, mapperComponent, reducerComponent, INITIAL_KEY);
        LOGGER.info("Running MapReduce task with master masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);

        // Print values fetched from KV Store
        List<String> finalOutput = new ArrayList<>();
        for (String key : finalKeys) {

            Keyvalue.KeyValuePair keyValuePair = kvStoreBlockingStub.get(Keyvalue.Key.newBuilder().setKey(key).build());
            finalOutput.add(keyValuePair.getKey() + " ==> " + keyValuePair.getValue());
        }

        persistInFile(finalOutput, fileUri);
        LOGGER.info("Persisted result of Map Reduce in file: {}", fileUri);

        LOGGER.info("Shutting down the cluster with masterIpAddress: {}, masterPort: {}", masterIpAddress, masterPort);
        boolean isDestroyed = hadoopManager.destroyCluster(masterComponent, masterIpAddress, masterPort);

        if (isDestroyed) {
            LOGGER.info("Cluster is successfully destroyed with masterIpAddress: {}, masterPort: {}",
                    masterIpAddress, masterPort);
        }

        LOGGER.info("MapReduce task is completed successfully");
    }

    private static void persistInFile(List<String> input, String filePath) throws IOException, URISyntaxException {

        File file = new File(new URI(filePath));
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