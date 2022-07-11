package org.mengyun.tcctransaction.sample.grpc.redpacket.api;

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
    value = "by gRPC proto compiler (version 1.17.1)",
    comments = "Source: RedPacketService.proto")
public final class RedPacketServiceGrpc {

  private RedPacketServiceGrpc() {}

  public static final String SERVICE_NAME = "RedPacketService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest,
      org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> getGetRedPacketAccountByUserIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getRedPacketAccountByUserId",
      requestType = org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest.class,
      responseType = org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest,
      org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> getGetRedPacketAccountByUserIdMethod() {
    io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest, org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> getGetRedPacketAccountByUserIdMethod;
    if ((getGetRedPacketAccountByUserIdMethod = RedPacketServiceGrpc.getGetRedPacketAccountByUserIdMethod) == null) {
      synchronized (RedPacketServiceGrpc.class) {
        if ((getGetRedPacketAccountByUserIdMethod = RedPacketServiceGrpc.getGetRedPacketAccountByUserIdMethod) == null) {
          RedPacketServiceGrpc.getGetRedPacketAccountByUserIdMethod = getGetRedPacketAccountByUserIdMethod = 
              io.grpc.MethodDescriptor.<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest, org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "RedPacketService", "getRedPacketAccountByUserId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply.getDefaultInstance()))
                  .setSchemaDescriptor(new RedPacketServiceMethodDescriptorSupplier("getRedPacketAccountByUserId"))
                  .build();
          }
        }
     }
     return getGetRedPacketAccountByUserIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto,
      org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> getRecordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "record",
      requestType = org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto.class,
      responseType = org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto,
      org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> getRecordMethod() {
    io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> getRecordMethod;
    if ((getRecordMethod = RedPacketServiceGrpc.getRecordMethod) == null) {
      synchronized (RedPacketServiceGrpc.class) {
        if ((getRecordMethod = RedPacketServiceGrpc.getRecordMethod) == null) {
          RedPacketServiceGrpc.getRecordMethod = getRecordMethod = 
              io.grpc.MethodDescriptor.<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "RedPacketService", "record"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply.getDefaultInstance()))
                  .setSchemaDescriptor(new RedPacketServiceMethodDescriptorSupplier("record"))
                  .build();
          }
        }
     }
     return getRecordMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RedPacketServiceStub newStub(io.grpc.Channel channel) {
    return new RedPacketServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RedPacketServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new RedPacketServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RedPacketServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new RedPacketServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class RedPacketServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getRedPacketAccountByUserId(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest request,
        io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> responseObserver) {
      asyncUnimplementedUnaryCall(getGetRedPacketAccountByUserIdMethod(), responseObserver);
    }

    /**
     */
    public void record(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto request,
        io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> responseObserver) {
      asyncUnimplementedUnaryCall(getRecordMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getGetRedPacketAccountByUserIdMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest,
                org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply>(
                  this, METHODID_GET_RED_PACKET_ACCOUNT_BY_USER_ID)))
          .addMethod(
            getRecordMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto,
                org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply>(
                  this, METHODID_RECORD)))
          .build();
    }
  }

  /**
   */
  public static final class RedPacketServiceStub extends io.grpc.stub.AbstractStub<RedPacketServiceStub> {
    private RedPacketServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RedPacketServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RedPacketServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RedPacketServiceStub(channel, callOptions);
    }

    /**
     */
    public void getRedPacketAccountByUserId(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest request,
        io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getGetRedPacketAccountByUserIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void record(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto request,
        io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRecordMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class RedPacketServiceBlockingStub extends io.grpc.stub.AbstractStub<RedPacketServiceBlockingStub> {
    private RedPacketServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RedPacketServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RedPacketServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RedPacketServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply getRedPacketAccountByUserId(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest request) {
      return blockingUnaryCall(
          getChannel(), getGetRedPacketAccountByUserIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply record(org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto request) {
      return blockingUnaryCall(
          getChannel(), getRecordMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RedPacketServiceFutureStub extends io.grpc.stub.AbstractStub<RedPacketServiceFutureStub> {
    private RedPacketServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RedPacketServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RedPacketServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RedPacketServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply> getRedPacketAccountByUserId(
        org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getGetRedPacketAccountByUserIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply> record(
        org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto request) {
      return futureUnaryCall(
          getChannel().newCall(getRecordMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_RED_PACKET_ACCOUNT_BY_USER_ID = 0;
  private static final int METHODID_RECORD = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RedPacketServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RedPacketServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_RED_PACKET_ACCOUNT_BY_USER_ID:
          serviceImpl.getRedPacketAccountByUserId((org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountRequest) request,
              (io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketAccountReply>) responseObserver);
          break;
        case METHODID_RECORD:
          serviceImpl.record((org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RedPacketTradeOrderDto) request,
              (io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.RecordReply>) responseObserver);
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

  private static abstract class RedPacketServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RedPacketServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.mengyun.tcctransaction.sample.grpc.redpacket.api.RedPacketServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RedPacketService");
    }
  }

  private static final class RedPacketServiceFileDescriptorSupplier
      extends RedPacketServiceBaseDescriptorSupplier {
    RedPacketServiceFileDescriptorSupplier() {}
  }

  private static final class RedPacketServiceMethodDescriptorSupplier
      extends RedPacketServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RedPacketServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (RedPacketServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RedPacketServiceFileDescriptorSupplier())
              .addMethod(getGetRedPacketAccountByUserIdMethod())
              .addMethod(getRecordMethod())
              .build();
        }
      }
    }
    return result;
  }
}
