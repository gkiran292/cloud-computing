package org.iu.engrcloudcomputing.mapreduce.hadoop.manager.spi;

import io.grpc.ManagedChannel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface HadoopManager {

    String initiateCluster(String componentName, int port) throws IOException, GeneralSecurityException;

    List<String> runMapReduce(ManagedChannel channel, String kvStoreIpAddress, int kvStorePort, String masterIpAddress,
                              int masterPort, int mapperCount, int reducerCount, String mapperJar, String reducerJar,
                              String initialKey);

    boolean destroyCluster(String instanceName, String ipAddress, int port);
}
