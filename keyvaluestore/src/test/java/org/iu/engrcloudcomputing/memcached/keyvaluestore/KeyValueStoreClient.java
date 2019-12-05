package org.iu.engrcloudcomputing.memcached.keyvaluestore;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.iu.engrcloudcomputing.memcached.keyvaluestore.autogenerated.KeyValueStoreGrpc;
import org.iu.engrcloudcomputing.memcached.keyvaluestore.autogenerated.Keyvalue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyValueStoreClient {

    public static void main(String[] args) {

//        ExecutorService threadPool = Executors.newFixedThreadPool(50);
//
//
//        threadPool.execute(new ParallelClient("master", "//Users//gkiran//Downloads/Walden-LifeInTheWot"));
////        for (int i = 0; i < 50; i++) {
////            String key = "India"+ i;
////            String value = "Country" + i;
////            threadPool.execute(new ParallelClient(key, value));
////        }
//        threadPool.shutdown();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("34.74.108.131", 9000).usePlaintext().build();
        KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValueStoreBlockingStub = KeyValueStoreGrpc.newBlockingStub(channel);

        Keyvalue.Code responseCode = keyValueStoreBlockingStub.set(Keyvalue.KeyValuePair.newBuilder()
                .setKey("master").setValue("value").build());
        System.out.println("Response Code: " + responseCode.getResponseCode());

        Keyvalue.KeyValuePair keyValuePair = keyValueStoreBlockingStub.get(Keyvalue.Key.newBuilder().setKey("master").build());
        System.out.println("Key: " + keyValuePair.getKey() + " Value: " + keyValuePair.getValue());
    }
}
