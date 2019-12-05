package org.iu.engrcloudcomputing.mapreduce.hadoop.manager.spi;

import io.grpc.ManagedChannel;
import org.zeroturnaround.exec.ProcessResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Future;

public interface HadoopManager {

    String initiateCluster(String componentName, String nfsServerDetails, String workingFolder, int port) throws IOException, GeneralSecurityException;

    List<String> runMapReduce(ManagedChannel channel, String kvStoreIpAddress, int kvStorePort, String masterIpAddress,
                              int masterPort, int mapperCount, int reducerCount, String mapperJar, String reducerJar,
                              String initialKey);

    boolean destroyCluster(String instanceName, String ipAddress, int port);
}
