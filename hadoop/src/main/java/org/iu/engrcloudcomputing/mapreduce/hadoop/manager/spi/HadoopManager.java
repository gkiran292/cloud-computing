package org.iu.engrcloudcomputing.mapreduce.hadoop.manager.spi;

import io.grpc.ManagedChannel;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface HadoopManager {

    Map<String, String> initiateCluster(String kvStoreComponentName, String masterComponentName, int masterPort, int kvStorePort)
            throws IOException, GeneralSecurityException;

    List<String> runMapReduce(ManagedChannel channel, String kvStoreIpAddress, int kvStorePort, String masterIpAddress,
                              int masterPort, int mapperCount, int reducerCount, String mapperJar, String reducerJar,
                              String initialKey);

    boolean destroyCluster(String kvStoreComponentName, String masterComponentName);
}
