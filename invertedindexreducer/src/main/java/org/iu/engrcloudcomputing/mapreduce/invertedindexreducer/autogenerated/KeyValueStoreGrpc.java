package org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: keyvalue.proto")
public final class KeyValueStoreGrpc {

  private KeyValueStoreGrpc() {}

  public static final String SERVICE_NAME = "KeyValueStore";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair,
      org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> getSetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "set",
      requestType = org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair.class,
      responseType = org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair,
      org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> getSetMethod() {
    io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair, org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> getSetMethod;
    if ((getSetMethod = KeyValueStoreGrpc.getSetMethod) == null) {
      synchronized (KeyValueStoreGrpc.class) {
        if ((getSetMethod = KeyValueStoreGrpc.getSetMethod) == null) {
          KeyValueStoreGrpc.getSetMethod = getSetMethod = 
              io.grpc.MethodDescriptor.<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair, org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "KeyValueStore", "set"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code.getDefaultInstance()))
                  .setSchemaDescriptor(new KeyValueStoreMethodDescriptorSupplier("set"))
                  .build();
          }
        }
     }
     return getSetMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key,
      org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> getGetMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "get",
      requestType = org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key.class,
      responseType = org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key,
      org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> getGetMethod() {
    io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key, org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> getGetMethod;
    if ((getGetMethod = KeyValueStoreGrpc.getGetMethod) == null) {
      synchronized (KeyValueStoreGrpc.class) {
        if ((getGetMethod = KeyValueStoreGrpc.getGetMethod) == null) {
          KeyValueStoreGrpc.getGetMethod = getGetMethod = 
              io.grpc.MethodDescriptor.<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key, org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "KeyValueStore", "get"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair.getDefaultInstance()))
                  .setSchemaDescriptor(new KeyValueStoreMethodDescriptorSupplier("get"))
                  .build();
          }
        }
     }
     return getGetMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static KeyValueStoreStub newStub(io.grpc.Channel channel) {
    return new KeyValueStoreStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static KeyValueStoreBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new KeyValueStoreBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static KeyValueStoreFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new KeyValueStoreFutureStub(channel);
  }

  /**
   */
  public static abstract class KeyValueStoreImplBase implements io.grpc.BindableService {

    /**
     */
    public void set(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMethod(), responseObserver);
    }

    /**
     */
    public void get(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> responseObserver) {
      asyncUnimplementedUnaryCall(getGetMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair,
                org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code>(
                  this, METHODID_SET)))
          .addMethod(
            getGetMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key,
                org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair>(
                  this, METHODID_GET)))
          .build();
    }
  }

  /**
   */
  public static final class KeyValueStoreStub extends io.grpc.stub.AbstractStub<KeyValueStoreStub> {
    private KeyValueStoreStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KeyValueStoreStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyValueStoreStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KeyValueStoreStub(channel, callOptions);
    }

    /**
     */
    public void set(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void get(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class KeyValueStoreBlockingStub extends io.grpc.stub.AbstractStub<KeyValueStoreBlockingStub> {
    private KeyValueStoreBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KeyValueStoreBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyValueStoreBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KeyValueStoreBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code set(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair request) {
      return blockingUnaryCall(
          getChannel(), getSetMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair get(org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key request) {
      return blockingUnaryCall(
          getChannel(), getGetMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class KeyValueStoreFutureStub extends io.grpc.stub.AbstractStub<KeyValueStoreFutureStub> {
    private KeyValueStoreFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private KeyValueStoreFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected KeyValueStoreFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new KeyValueStoreFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code> set(
        org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair> get(
        org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key request) {
      return futureUnaryCall(
          getChannel().newCall(getGetMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SET = 0;
  private static final int METHODID_GET = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final KeyValueStoreImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(KeyValueStoreImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SET:
          serviceImpl.set((org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair) request,
              (io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Code>) responseObserver);
          break;
        case METHODID_GET:
          serviceImpl.get((org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.Key) request,
              (io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.KeyValuePair>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class KeyValueStoreBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    KeyValueStoreBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.iu.engrcloudcomputing.mapreduce.invertedindexreducer.autogenerated.Keyvalue.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("KeyValueStore");
    }
  }

  private static final class KeyValueStoreFileDescriptorSupplier
      extends KeyValueStoreBaseDescriptorSupplier {
    KeyValueStoreFileDescriptorSupplier() {}
  }

  private static final class KeyValueStoreMethodDescriptorSupplier
      extends KeyValueStoreBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    KeyValueStoreMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (KeyValueStoreGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new KeyValueStoreFileDescriptorSupplier())
              .addMethod(getSetMethod())
              .addMethod(getGetMethod())
              .build();
        }
      }
    }
    return result;
  }
}
