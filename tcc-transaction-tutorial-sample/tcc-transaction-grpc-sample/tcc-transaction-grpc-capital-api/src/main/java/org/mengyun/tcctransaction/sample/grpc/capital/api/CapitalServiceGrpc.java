package org.mengyun.tcctransaction.sample.grpc.capital.api;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.17.1)", comments = "Source: CapitalService.proto")
public final class CapitalServiceGrpc {

    public static final String SERVICE_NAME = "CapitalService";

    private static final int METHODID_GET_CAPITAL_ACCOUNT_BY_USER_ID = 0;

    private static final int METHODID_RECORD = 1;

    // Static method descriptors that strictly reflect the proto.
    private static volatile io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> getGetCapitalAccountByUserIdMethod;

    private static volatile io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> getRecordMethod;

    private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

    private CapitalServiceGrpc() {
    }

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "getCapitalAccountByUserId", requestType = org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest.class, responseType = org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> getGetCapitalAccountByUserIdMethod() {
        io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> getGetCapitalAccountByUserIdMethod;
        if ((getGetCapitalAccountByUserIdMethod = CapitalServiceGrpc.getGetCapitalAccountByUserIdMethod) == null) {
            synchronized (CapitalServiceGrpc.class) {
                if ((getGetCapitalAccountByUserIdMethod = CapitalServiceGrpc.getGetCapitalAccountByUserIdMethod) == null) {
                    CapitalServiceGrpc.getGetCapitalAccountByUserIdMethod = getGetCapitalAccountByUserIdMethod = io.grpc.MethodDescriptor.<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName("CapitalService", "getCapitalAccountByUserId")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply.getDefaultInstance())).setSchemaDescriptor(new CapitalServiceMethodDescriptorSupplier("getCapitalAccountByUserId")).build();
                }
            }
        }
        return getGetCapitalAccountByUserIdMethod;
    }

    @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "record", requestType = org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto.class, responseType = org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply.class, methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
    public static io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> getRecordMethod() {
        io.grpc.MethodDescriptor<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> getRecordMethod;
        if ((getRecordMethod = CapitalServiceGrpc.getRecordMethod) == null) {
            synchronized (CapitalServiceGrpc.class) {
                if ((getRecordMethod = CapitalServiceGrpc.getRecordMethod) == null) {
                    CapitalServiceGrpc.getRecordMethod = getRecordMethod = io.grpc.MethodDescriptor.<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply>newBuilder().setType(io.grpc.MethodDescriptor.MethodType.UNARY).setFullMethodName(generateFullMethodName("CapitalService", "record")).setSampledToLocalTracing(true).setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto.getDefaultInstance())).setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply.getDefaultInstance())).setSchemaDescriptor(new CapitalServiceMethodDescriptorSupplier("record")).build();
                }
            }
        }
        return getRecordMethod;
    }

    /**
     * Creates a new async stub that supports all call types for the service
     */
    public static CapitalServiceStub newStub(io.grpc.Channel channel) {
        return new CapitalServiceStub(channel);
    }

    /**
     * Creates a new blocking-style stub that supports unary and streaming output calls on the service
     */
    public static CapitalServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
        return new CapitalServiceBlockingStub(channel);
    }

    /**
     * Creates a new ListenableFuture-style stub that supports unary calls on the service
     */
    public static CapitalServiceFutureStub newFutureStub(io.grpc.Channel channel) {
        return new CapitalServiceFutureStub(channel);
    }

    public static io.grpc.ServiceDescriptor getServiceDescriptor() {
        io.grpc.ServiceDescriptor result = serviceDescriptor;
        if (result == null) {
            synchronized (CapitalServiceGrpc.class) {
                result = serviceDescriptor;
                if (result == null) {
                    serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME).setSchemaDescriptor(new CapitalServiceFileDescriptorSupplier()).addMethod(getGetCapitalAccountByUserIdMethod()).addMethod(getRecordMethod()).build();
                }
            }
        }
        return result;
    }

    /**
     */
    public static abstract class CapitalServiceImplBase implements io.grpc.BindableService {

        /**
         */
        public void getCapitalAccountByUserId(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest request, io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> responseObserver) {
            asyncUnimplementedUnaryCall(getGetCapitalAccountByUserIdMethod(), responseObserver);
        }

        /**
         */
        public void record(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto request, io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> responseObserver) {
            asyncUnimplementedUnaryCall(getRecordMethod(), responseObserver);
        }

        @java.lang.Override
        public final io.grpc.ServerServiceDefinition bindService() {
            return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(getGetCapitalAccountByUserIdMethod(), asyncUnaryCall(new MethodHandlers<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply>(this, METHODID_GET_CAPITAL_ACCOUNT_BY_USER_ID))).addMethod(getRecordMethod(), asyncUnaryCall(new MethodHandlers<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto, org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply>(this, METHODID_RECORD))).build();
        }
    }

    /**
     */
    public static final class CapitalServiceStub extends io.grpc.stub.AbstractStub<CapitalServiceStub> {

        private CapitalServiceStub(io.grpc.Channel channel) {
            super(channel);
        }

        private CapitalServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CapitalServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CapitalServiceStub(channel, callOptions);
        }

        /**
         */
        public void getCapitalAccountByUserId(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest request, io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> responseObserver) {
            asyncUnaryCall(getChannel().newCall(getGetCapitalAccountByUserIdMethod(), getCallOptions()), request, responseObserver);
        }

        /**
         */
        public void record(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto request, io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> responseObserver) {
            asyncUnaryCall(getChannel().newCall(getRecordMethod(), getCallOptions()), request, responseObserver);
        }
    }

    /**
     */
    public static final class CapitalServiceBlockingStub extends io.grpc.stub.AbstractStub<CapitalServiceBlockingStub> {

        private CapitalServiceBlockingStub(io.grpc.Channel channel) {
            super(channel);
        }

        private CapitalServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CapitalServiceBlockingStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CapitalServiceBlockingStub(channel, callOptions);
        }

        /**
         */
        public org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply getCapitalAccountByUserId(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest request) {
            return blockingUnaryCall(getChannel(), getGetCapitalAccountByUserIdMethod(), getCallOptions(), request);
        }

        /**
         */
        public org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply record(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto request) {
            return blockingUnaryCall(getChannel(), getRecordMethod(), getCallOptions(), request);
        }
    }

    /**
     */
    public static final class CapitalServiceFutureStub extends io.grpc.stub.AbstractStub<CapitalServiceFutureStub> {

        private CapitalServiceFutureStub(io.grpc.Channel channel) {
            super(channel);
        }

        private CapitalServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            super(channel, callOptions);
        }

        @java.lang.Override
        protected CapitalServiceFutureStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new CapitalServiceFutureStub(channel, callOptions);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply> getCapitalAccountByUserId(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest request) {
            return futureUnaryCall(getChannel().newCall(getGetCapitalAccountByUserIdMethod(), getCallOptions()), request);
        }

        /**
         */
        public com.google.common.util.concurrent.ListenableFuture<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply> record(org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto request) {
            return futureUnaryCall(getChannel().newCall(getRecordMethod(), getCallOptions()), request);
        }
    }

    private static final class MethodHandlers<Req, Resp> implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>, io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>, io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {

        private final CapitalServiceImplBase serviceImpl;

        private final int methodId;

        MethodHandlers(CapitalServiceImplBase serviceImpl, int methodId) {
            this.serviceImpl = serviceImpl;
            this.methodId = methodId;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                case METHODID_GET_CAPITAL_ACCOUNT_BY_USER_ID:
                    serviceImpl.getCapitalAccountByUserId((org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountRequest) request, (io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalAccountReply>) responseObserver);
                    break;
                case METHODID_RECORD:
                    serviceImpl.record((org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.CapitalTradeOrderDto) request, (io.grpc.stub.StreamObserver<org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.RecordReply>) responseObserver);
                    break;
                default:
                    throw new AssertionError();
            }
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("unchecked")
        public io.grpc.stub.StreamObserver<Req> invoke(io.grpc.stub.StreamObserver<Resp> responseObserver) {
            switch(methodId) {
                default:
                    throw new AssertionError();
            }
        }
    }

    private static abstract class CapitalServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {

        CapitalServiceBaseDescriptorSupplier() {
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
            return org.mengyun.tcctransaction.sample.grpc.capital.api.CapitalServiceOuterClass.getDescriptor();
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
            return getFileDescriptor().findServiceByName("CapitalService");
        }
    }

    private static final class CapitalServiceFileDescriptorSupplier extends CapitalServiceBaseDescriptorSupplier {

        CapitalServiceFileDescriptorSupplier() {
        }
    }

    private static final class CapitalServiceMethodDescriptorSupplier extends CapitalServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {

        private final String methodName;

        CapitalServiceMethodDescriptorSupplier(String methodName) {
            this.methodName = methodName;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
            return getServiceDescriptor().findMethodByName(methodName);
        }
    }
}
