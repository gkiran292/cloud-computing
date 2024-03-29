package org.iu.engrcloudcomputing.mapreduce.mapred.master.impl;

import io.grpc.stub.StreamObserver;
import org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.BeginMapReduceGrpc;
import org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master;
import org.iu.engrcloudcomputing.mapreduce.mapred.dto.TaskInfo;
import org.iu.engrcloudcomputing.mapreduce.mapred.manager.MapReduceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class InitiateMapReduceService extends BeginMapReduceGrpc.BeginMapReduceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    private ConcurrentMap<String, String> mapperConcurrentMap;
    private ConcurrentMap<String, String> reducerConcurrentMap;
    private CountDownLatch mapReduceTaskLatch;
    private static final String MESSAGE = "Master is assigned to another cluster";
    private ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap;

    public InitiateMapReduceService(ConcurrentMap<String, String> mapperConcurrentMap,
                                    ConcurrentMap<String, String> reducerConcurrentMap, CountDownLatch mapReduceTaskLatch,
                                    ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap) {
        this.mapperConcurrentMap = mapperConcurrentMap;
        this.reducerConcurrentMap = reducerConcurrentMap;
        this.mapReduceTaskLatch = mapReduceTaskLatch;
        this.taskInfoConcurrentMap = taskInfoConcurrentMap;
    }

    @Override
    public void mapReduce(Master.MapReduceParams request, StreamObserver<Master.MapReduceResponse> responseObserver) {

        String initialKey = request.getInitialKey();
        String mapperComponentName = request.getMapperJar();
        String reducerComponentName = request.getReducerJar();
        int mappers = request.getMappers();
        int reducers = request.getReducers();
        String kvStoreIpAddress = request.getKvStoreIpAddress();
        int kvStorePort = request.getKvStorePort();
        String masterIpAddress = request.getMasterIpAddress();
        int masterPort = request.getMasterPort();


        if (mapReduceTaskLatch.getCount() < 1) {
            responseObserver.onNext(Master.MapReduceResponse.newBuilder()
                    .setMessage(Master.Message.newBuilder().setResponseCode(400).setResponseMessage(MESSAGE).build())
                    .build());
            responseObserver.onCompleted();
            return;
        }

        String kvStoreDetails = kvStoreIpAddress + ":" + kvStorePort;
        String masterDetails = masterIpAddress + ":" + masterPort;
        MapReduceManager mapReduceManager = new MapReduceManager(kvStoreDetails, masterDetails, mappers, reducers,
                mapperComponentName, reducerComponentName, initialKey, mapperConcurrentMap,
                reducerConcurrentMap, taskInfoConcurrentMap);

        List<String> finalKeys;
        try {
            finalKeys = mapReduceManager.mapReduce();
            responseObserver.onNext(Master.MapReduceResponse.newBuilder()
                    .setMessage(Master.Message.newBuilder().setResponseCode(200).setResponseMessage("OK").build())
                    .setKeys(Master.Keys.newBuilder().addAllKey(finalKeys).build())
                    .build());
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOGGER.error("Exception in map reduce task");
            LOGGER.error(e.getMessage());
            responseObserver.onNext(Master.MapReduceResponse.newBuilder()
                    .setMessage(Master.Message.newBuilder().setResponseCode(500).setResponseMessage("ERROR").build())
                    .build());
        } finally {
            responseObserver.onCompleted();
            mapReduceTaskLatch.countDown();
        }
    }
}
