package org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated;

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
    comments = "Source: Master.proto")
public final class MapperAckGrpc {

  private MapperAckGrpc() {}

  public static final String SERVICE_NAME = "MapperAck";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys,
      org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> getMappedKeysMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "mappedKeys",
      requestType = org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys.class,
      responseType = org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys,
      org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> getMappedKeysMethod() {
    io.grpc.MethodDescriptor<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys, org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> getMappedKeysMethod;
    if ((getMappedKeysMethod = MapperAckGrpc.getMappedKeysMethod) == null) {
      synchronized (MapperAckGrpc.class) {
        if ((getMappedKeysMethod = MapperAckGrpc.getMappedKeysMethod) == null) {
          MapperAckGrpc.getMappedKeysMethod = getMappedKeysMethod = 
              io.grpc.MethodDescriptor.<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys, org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "MapperAck", "mappedKeys"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message.getDefaultInstance()))
                  .setSchemaDescriptor(new MapperAckMethodDescriptorSupplier("mappedKeys"))
                  .build();
          }
        }
     }
     return getMappedKeysMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MapperAckStub newStub(io.grpc.Channel channel) {
    return new MapperAckStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MapperAckBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new MapperAckBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MapperAckFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new MapperAckFutureStub(channel);
  }

  /**
   */
  public static abstract class MapperAckImplBase implements io.grpc.BindableService {

    /**
     */
    public void mappedKeys(org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> responseObserver) {
      asyncUnimplementedUnaryCall(getMappedKeysMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getMappedKeysMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys,
                org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message>(
                  this, METHODID_MAPPED_KEYS)))
          .build();
    }
  }

  /**
   */
  public static final class MapperAckStub extends io.grpc.stub.AbstractStub<MapperAckStub> {
    private MapperAckStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MapperAckStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapperAckStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MapperAckStub(channel, callOptions);
    }

    /**
     */
    public void mappedKeys(org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys request,
        io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getMappedKeysMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class MapperAckBlockingStub extends io.grpc.stub.AbstractStub<MapperAckBlockingStub> {
    private MapperAckBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MapperAckBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapperAckBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MapperAckBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message mappedKeys(org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys request) {
      return blockingUnaryCall(
          getChannel(), getMappedKeysMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class MapperAckFutureStub extends io.grpc.stub.AbstractStub<MapperAckFutureStub> {
    private MapperAckFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private MapperAckFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapperAckFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new MapperAckFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message> mappedKeys(
        org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys request) {
      return futureUnaryCall(
          getChannel().newCall(getMappedKeysMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_MAPPED_KEYS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MapperAckImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MapperAckImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_MAPPED_KEYS:
          serviceImpl.mappedKeys((org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Keys) request,
              (io.grpc.stub.StreamObserver<org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.Message>) responseObserver);
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

  private static abstract class MapperAckBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    MapperAckBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.iu.engrcloudcomputing.mapreduce.mapred.autogenerated.Master.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("MapperAck");
    }
  }

  private static final class MapperAckFileDescriptorSupplier
      extends MapperAckBaseDescriptorSupplier {
    MapperAckFileDescriptorSupplier() {}
  }

  private static final class MapperAckMethodDescriptorSupplier
      extends MapperAckBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    MapperAckMethodDescriptorSupplier(String methodName) {
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
      synchronized (MapperAckGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new MapperAckFileDescriptorSupplier())
              .addMethod(getMappedKeysMethod())
              .build();
        }
      }
    }
    return result;
  }
}
