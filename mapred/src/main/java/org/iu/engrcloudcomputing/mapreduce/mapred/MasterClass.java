package org.iu.engrcloudcomputing.mapreduce.mapred;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.iu.engrcloudcomputing.mapreduce.mapred.dto.TaskInfo;
import org.iu.engrcloudcomputing.mapreduce.mapred.helper.CommandOptions;
import org.iu.engrcloudcomputing.mapreduce.mapred.helper.Constants;
import org.iu.engrcloudcomputing.mapreduce.mapred.master.impl.InitiateMapReduceService;
import org.iu.engrcloudcomputing.mapreduce.mapred.master.impl.MapperAckService;
import org.iu.engrcloudcomputing.mapreduce.mapred.master.impl.ReducerAckService;
import org.iu.engrcloudcomputing.mapreduce.mapred.master.impl.ShutDownMasterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

public class MasterClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    public static void main(String[] args) throws IOException, InterruptedException {

        ConcurrentMap<String, String> mapperConcurrentMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, String> reducerConcurrentMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, TaskInfo> taskInfoConcurrentMap = new ConcurrentHashMap<>();
        CountDownLatch mapReduceTaskLatch = new CountDownLatch(1);
        CountDownLatch serverStatusLatch = new CountDownLatch(1);
        String masterDetails = null;

        //Get Command line values
        CommandOptions cmd = new CommandOptions(args);

        //KV store information
        if (cmd.hasOption("-m")) {
            masterDetails = cmd.valueOf("-m");
        }

        //Register mapperAckService and reducerAckService

        LOGGER.info("Master is initializing...");
        if (masterDetails == null) {
            LOGGER.warn("Master Details is null proceeding with default port: {}", 9001);
        }
        int port = Integer.parseInt(masterDetails != null ? masterDetails.split(":")[1] : Constants.DEFAULT_PORT);
        Server server = ServerBuilder.forPort(port)
                .addService(new MapperAckService(mapperConcurrentMap, taskInfoConcurrentMap))
                .addService(new ReducerAckService(reducerConcurrentMap, taskInfoConcurrentMap))
                .addService(new InitiateMapReduceService(mapperConcurrentMap, reducerConcurrentMap, mapReduceTaskLatch,
                        taskInfoConcurrentMap))
                .addService(new ShutDownMasterService(serverStatusLatch)).build();
        server.start();
        LOGGER.info("Master is initialized");

        serverStatusLatch.await();
        Thread.sleep(100);
        server.shutdownNow();
    }
}