package org.iu.engrcloudcomputing.mapreduce.invertedindexreducer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.mutable.MutableInt;
import org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.KeyValueStoreGrpc;
import org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue;
import org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Master;
import org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.ReducerAckGrpc;
import org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.helper.CommandOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReducerClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    public static void main(String[] args) throws IOException {

        String kvStoreIpAddress = null;
        int kvStorePort = Integer.MIN_VALUE;
        String masterIpAddress = null;
        int masterPort = Integer.MIN_VALUE;
        String reducerId = null;

        //Get Command line values
        CommandOptions cmd = new CommandOptions(args);

        //KV store information
        if (cmd.hasOption("-k")) {
            kvStoreIpAddress = cmd.valueOf("-k").split(":")[0];
            kvStorePort = Integer.parseInt(cmd.valueOf("-k").split(":")[1]);
        }

        //Master node information
        if (cmd.hasOption("-m")) {
            masterIpAddress = cmd.valueOf("-m").split(":")[0];
            masterPort = Integer.parseInt(cmd.valueOf("-m").split(":")[1]);
        }

        //Mapper Id
        if (cmd.hasOption("-u")) {
            reducerId = cmd.valueOf("-u");
        }

        LOGGER.info("kvStoreIpAddress: {}, kvStorePort: {}, masterIpAddress: {}, masterPort: {}, reducerId: {}, ", kvStoreIpAddress, kvStorePort, masterIpAddress, masterPort, reducerId);

        KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValueStoreBlockingStub = createKVStoreConnection(kvStoreIpAddress, kvStorePort);
        String keysFromKVStore = readKeysFromKVStore(reducerId, keyValueStoreBlockingStub);

        List<String> mapperKeys = processInputArguments(/*ReducerId*/reducerId, /*reducer keys*/keysFromKVStore, keyValueStoreBlockingStub);

        ReducerAckGrpc.ReducerAckBlockingStub reducerAckBlockingStub = createMasterNodeConnection(masterIpAddress, masterPort);

        Master.Message response = reducerAckBlockingStub.reduceKeys(Master.Keys.newBuilder().addAllKey(mapperKeys).build());

        if (response.getResponseCode() != 200) {
            LOGGER.error("Sending data to master failed, responseCode: {}", response.getResponseCode());
        } else {
            LOGGER.info("Keys are successfully reduced");
        }
    }

    private static String readKeysFromKVStore(String uuid, KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub)
            throws IOException {

        Keyvalue.KeyValuePair keyValuePair = blockingStub.get(Keyvalue.Key.newBuilder().setKey(uuid).build());

        if (keyValuePair.getResponseCode() != 200) {
            LOGGER.error("Failed to fetch the value for the mapper, uuid: {}", uuid);
            throw new IOException("Failed to fetch the value for the uuid " + uuid);
        }

        return keyValuePair.getValue();
    }

    private static List<String> processInputArguments(String reducerId, String inputArgs,
                                                      KeyValueStoreGrpc.KeyValueStoreBlockingStub blockingStub) {

        Map<String, Map<String, MutableInt>> map = new HashMap<>();
        List<String> finalKeys = new ArrayList<>();
        String[] mapperInput = inputArgs.split(",");


        for (String key : mapperInput) {
            Keyvalue.KeyValuePair keyValuePair = blockingStub.get(Keyvalue.Key.newBuilder().setKey(key).build());

            if (keyValuePair.getResponseCode() == 200) {
                reducerFunctionImpl(reducerId, keyValuePair, map);
            }
        }

        for (Map.Entry<String, Map<String, MutableInt>> mapEntry : map.entrySet()) {
            String key = mapEntry.getKey();
            Map<String, MutableInt> val = mapEntry.getValue();

            String finalVal = val.entrySet().stream()
                    .map(keyValPair -> keyValPair.getKey() + ":" + keyValPair.getValue().getValue())
                    .collect(Collectors.joining(","));

            Keyvalue.Code responseCode = blockingStub.set(Keyvalue.KeyValuePair.newBuilder().setKey(key)
                    .setValue(finalVal).build());
            if (responseCode.getResponseCode() != 200) {
                LOGGER.error("Not ale to store key: {}, value: {} in KV Store", key, finalVal);
            } else {
                finalKeys.add(key);
            }
        }

        return finalKeys;
    }

    private static void reducerFunctionImpl(String reducerId, Keyvalue.KeyValuePair keyValuePair,
                                            Map<String, Map<String, MutableInt>> map) {

        String key = keyValuePair.getKey().split("_")[1];
        String value = keyValuePair.getValue();

        String[] words = value.split(",");

        for (String word : words) {
            String[] split = word.split(":");

            LOGGER.debug("Reducing values for key: {}, value: {}, count: {}", key, split[0], split[1]);
            String reducerKey = reducerId + "_" + key;
            if (map.containsKey(reducerKey)) {
                MutableInt mi = map.get(reducerKey).get(split[0]);

                if (mi == null) {
                    map.get(reducerKey).put(split[0], new MutableInt(Integer.parseInt(split[1])));
                } else {
                    mi.add(Integer.parseInt(split[1]));
                }
            } else {
                Map<String, MutableInt> newMap = new HashMap<>();
                newMap.put(split[0], new MutableInt(Integer.parseInt(split[1])));
                map.put(reducerKey, newMap);
            }
            LOGGER.debug("Reducer key: {}", reducerKey);
        }
    }

    private static KeyValueStoreGrpc.KeyValueStoreBlockingStub createKVStoreConnection(String kvStoreIpAddress, int kvStorePort) {
        //TODO: change the argument and get the value from property file
        ManagedChannel channel = ManagedChannelBuilder.forAddress(kvStoreIpAddress, kvStorePort).usePlaintext().build();
        return KeyValueStoreGrpc.newBlockingStub(channel);
    }

    private static ReducerAckGrpc.ReducerAckBlockingStub createMasterNodeConnection(String masterIpAddress, int masterPort) {
        //TODO: change the argument and get the value from property file
        ManagedChannel channel = ManagedChannelBuilder.forAddress(masterIpAddress, masterPort).usePlaintext().build();
        return ReducerAckGrpc.newBlockingStub(channel);
    }
}