package org.iu.engrcloudcomputing.mapreduce.mapred.manager;

import com.google.api.services.compute.model.Metadata;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.KeyValueStoreGrpc;
import org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Keyvalue;
import org.iu.engrcloudcomputing.mapreduce.mapred.dto.TaskInfo;
import org.iu.engrcloudcomputing.mapreduce.mapred.helper.Constants;
import org.iu.engrcloudcomputing.mapreduce.mapred.helper.GoogleComputeOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MapReduceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private String kvStoreDetails;
    private String masterDetails;
    private int mappers;
    private int reducers;
    private String mapperComponentName;
    private String reducerComponentName;
    private String initialKey;
    private ConcurrentMap<String, String> mapperConcurrentMap;
    private ConcurrentMap<String, String> reducerConcurrentMap;
    private ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap;
    private ExecutorService executorService = Executors.newFixedThreadPool(50);
    private static final String STARTUP_SCRIPT_URL_KEY = "startup-script-url";
    private static final String STARTUP_SCRIPT_URL_VALUE = "gs://" + Constants.PROJECT_ID + "/vm_startup.sh";
    private static final String COMPONENT_NAME_KEY = "component";
    private static final String KV_STORE_KEY = "kv-store";
    private static final String MASTER_DETAILS_KEY = "master";
    private static final String UUID_KEY = "uuid";
    private static final String SCRIPT_KEY = "script";
    private static final String script = "run_mapper_reducer.sh";
    private static final String MAPPER_PREFIX = "m";
    private static final String REDUCER_PREFIX = "r";

    public MapReduceManager(String kvStoreDetails, String masterDetails, int mappers, int reducers, String mapperComponentName,
                            String reducerComponentName, String initialKey, ConcurrentMap<String, String> mapperConcurrentMap,
                            ConcurrentMap<String, String> reducerConcurrentMap, ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap) {
        this.kvStoreDetails = kvStoreDetails;
        this.masterDetails = masterDetails;
        this.mappers = mappers;
        this.reducers = reducers;
        this.mapperComponentName = mapperComponentName;
        this.reducerComponentName = reducerComponentName;
        this.initialKey = initialKey;
        this.mapperConcurrentMap = mapperConcurrentMap;
        this.reducerConcurrentMap = reducerConcurrentMap;
        this.taskInfoConcurrentMap = taskInfoConcurrentMap;
    }

    public List<String> mapReduce() throws IOException, ExecutionException, InterruptedException, URISyntaxException {

        KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub = createConnection();

        Keyvalue.KeyValuePair keyValuePair = blockingStub.get(Keyvalue.Key.newBuilder().setKey(initialKey).build());

        String inputValue = keyValuePair.getValue();
        String[] split = inputValue.split(",");

        List<String> filePaths = new ArrayList<>(Arrays.asList(split));
        List<String> totalKeys = splitAndStoreInput(blockingStub, filePaths);

        //Logic for splitting input
        List<String> inputKeys = splitInputKeys(totalKeys, mappers);
        mapInputToComponents(MAPPER_PREFIX, mapperComponentName, inputKeys, taskInfoConcurrentMap, blockingStub);

        //spawn and wait for mapper instances to be up (handles retry too)
        handleTasks(mapperComponentName);
        cleanUpInstances();

        //process the keys for reducers
        List<String> reducerKeyList = processKeysForReducers(mapperConcurrentMap, reducers);
        mapInputToComponents(REDUCER_PREFIX, mapperComponentName, reducerKeyList, taskInfoConcurrentMap, blockingStub);
        //spawn reducers
        handleTasks(reducerComponentName);
        cleanUpInstances();

        //mappers and reducers have finished their tasks
        List<String> finalKeys = new ArrayList<>();
        for (Map.Entry<String, String> mapEntry : reducerConcurrentMap.entrySet()) {
            String keyString = mapEntry.getKey();

            Keyvalue.KeyValuePair finalKVPair = blockingStub.get(Keyvalue.Key.newBuilder().setKey(keyString).build());
            String[] s = finalKVPair.getKey().split("_");
            LOGGER.debug("Setting the final value for key: {}, reducerId: {}", s[1], s[0]);
            Keyvalue.Code responseCode = blockingStub.set(Keyvalue.KeyValuePair.newBuilder().setKey(s[1])
                    .setValue(finalKVPair.getValue()).build());

            if (responseCode.getResponseCode() != 200) {
                LOGGER.error("Couldn't store the value for the key, key: {}, value: {}", s[1], finalKVPair.getValue());
            } else {
                finalKeys.add(s[1]);
                LOGGER.debug("Successfully stored for the key, key: {}, value: {}", s[1], finalKVPair.getValue());
            }
        }

//        for (String str : finalKeys) {
//            Keyvalue.KeyValuePair finalKVPair = blockingStub.get(Keyvalue.Key.newBuilder().setKey(str).build());
//
//            LOGGER.info(finalKVPair.getKey() + " ====> " + finalKVPair.getValue());
//        }

        return finalKeys;
    }

    private void cleanUpInstances() throws ExecutionException, InterruptedException {

        List<Future<Integer>> futures = new ArrayList<>();
        for (Map.Entry<String, TaskInfo> entry : taskInfoConcurrentMap.entrySet()) {
            String key = entry.getKey();
            futures.add(executorService.submit(() -> new GoogleComputeOps(key).deleteInstance()));
        }

        for (Future<Integer> future : futures) {
            Integer status = future.get();
            if (status != 0) {
                LOGGER.warn("Failed to clean up some of the instances");
            }
        }
        taskInfoConcurrentMap.clear();
    }

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
                break;
            } catch (TimeoutException ignored) {}
            finally {
                isRunning = taskInfoConcurrentMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsTaskFinished());
            }
        }
    }

    private void mapInputToComponents(String prefix, String componentName, List<String> inputKeys,
                                      ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap,
                                      KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub) {

        int i = 0;
        for (String input : inputKeys) {
            final String uuid = prefix + (++i);
            storeKeyValue(blockingStub, uuid, input);
            spawnTask(taskInfoConcurrentMap, uuid, input, componentName);
        }
    }

    private void waitAndRetryTasks(ConcurrentMap<String, TaskInfo> taskMap, String componentName)
            throws ExecutionException, InterruptedException {

        boolean hasAllComponentsNotFinished = true;
        spawnTaskProcesses(taskMap, componentName);
        Thread.sleep(10 * 1000);

        //wait for mappers to finish
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

        LOGGER.info("All component instances are started and waiting for the results, instances : {}", String.join(",", taskMap.keySet()));
    }

    private List<String> processKeysForReducers(ConcurrentMap<String, String> mapperConcurrentMap,
                                                int reducerCount) {

        Set<String> mapperSet = new HashSet<>();
        Set<String> keySet = new HashSet<>();

        List<String> reducerKeyList = new ArrayList<>();

        for (Map.Entry<String, String> mapEntry : mapperConcurrentMap.entrySet()) {

            String key = mapEntry.getKey();

            String[] mapper_key = key.split("_");

            keySet.add(mapper_key[1]);
            mapperSet.add(mapper_key[0]);
        }

        List<String> reducerKeys = splitInputKeys(new ArrayList<>(keySet), reducerCount);

        for (String splitKeys : reducerKeys) {
            String[] keys = splitKeys.split(",");

            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                for (String mapperId : mapperSet) {
                    sb.append(StringUtils.join(mapperId + "_" + key, ","));
                }
            }

            String finalKey = sb.toString();
            reducerKeyList.add(StringUtils.stripEnd(finalKey, ","));
        }

        return reducerKeyList;
    }

    private List<String> splitInputKeys(List<String> totalKeys, int nodeCount) {

        final int chunkSize;

        if (totalKeys.size() % nodeCount == 0) {
            chunkSize = totalKeys.size() / nodeCount;
        } else {
            chunkSize = (totalKeys.size() + nodeCount) / nodeCount;
        }

        final AtomicInteger counter = new AtomicInteger();
        return totalKeys.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values().stream()
                .map(list -> StringUtils.join(list, ","))
                .collect(Collectors.toList());
    }

    private void spawnTask(ConcurrentMap<String, TaskInfo> map, String uuid, String inputKey, String componentName) {

        Future<Integer> future = (executorService.submit(() ->
                new GoogleComputeOps(uuid).startInstance(getMetaData(uuid, componentName), true)));

        map.put(uuid, new TaskInfo(inputKey, future));
        LOGGER.info("TaskId: {}", uuid);
    }

    private void spawnTaskProcesses(ConcurrentMap<String, TaskInfo> map, String componentName) {

        Set<String> list = map.keySet();
        for (String key : list) {
            if (!map.get(key).getIsTaskFinished()) {
                spawnTask(map, key, map.get(key).getInput(), componentName);
            }
        }
    }

    private Metadata getMetaData(String uuid, String componentName) {

        Metadata metadata = new Metadata();
        List<Metadata.Items> itemsList = new ArrayList<>();
        itemsList.add(getItem(STARTUP_SCRIPT_URL_KEY, STARTUP_SCRIPT_URL_VALUE));
        itemsList.add(getItem(KV_STORE_KEY, kvStoreDetails));
        itemsList.add(getItem(MASTER_DETAILS_KEY, masterDetails));
        itemsList.add(getItem(UUID_KEY, uuid));
        itemsList.add(getItem(COMPONENT_NAME_KEY, componentName));
        itemsList.add(getItem(SCRIPT_KEY, script));
        metadata.setItems(itemsList);
        return metadata;
    }

    private Metadata.Items getItem(String key, String value) {
        Metadata.Items item = new Metadata.Items();
        item.setKey(key);
        item.setValue(value);

        return item;
    }

    private KeyValueStoreGrpc.KeyValueStoreBlockingStub createConnection() {
        String ipAddress = kvStoreDetails.split(":")[0];
        int port = Integer.parseInt(kvStoreDetails.split(":")[1]);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(ipAddress, port).usePlaintext().build();
        return KeyValueStoreGrpc.newBlockingStub(channel);
    }

    private void storeKeyValue(KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub, String key, String value) {

        Keyvalue.Code code = blockingStub.set(Keyvalue.KeyValuePair.newBuilder().setKey(key).setValue(value).build());

        if (code == null || code.getResponseCode() != 200) {
            LOGGER.error("Couldn't store the key: {}, value: {}", key, value);
        }

        LOGGER.info("Key Value Successfully stored, key: {}, value: {}", key, value);
    }

    private List<String> splitAndStoreInput(KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub,
                                            List<String> filePaths) throws IOException {

        List<String> totalKeyList = new ArrayList<>();

        for (String filePath : filePaths) {

            Path destFilePath = Paths.get(filePath);
            Storage storage = StorageOptions.getDefaultInstance().getService();

            Blob blob = storage.get(BlobId.of(Constants.PROJECT_ID, filePath));
            blob.downloadTo(destFilePath);

            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    LOGGER.error("Couldn't create a new file {}", filePath);
                    throw new IOException("Couldn't create a new file " + filePath);
                }
            }

            BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()));

            String line;
            int count = 0;
            String key;
            while ((line = br.readLine()) != null) {
                count++;
                key = (filePath + "_" + count);
                storeKeyValue(blockingStub, (filePath + "_" + count), line);
                totalKeyList.add(key);
            }

            br.close();
        }

        return totalKeyList;
    }
}
