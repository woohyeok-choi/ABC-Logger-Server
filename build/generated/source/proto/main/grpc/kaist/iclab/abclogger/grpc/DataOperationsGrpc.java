package kaist.iclab.abclogger.grpc;

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
    value = "by gRPC proto compiler (version 1.26.0)",
    comments = "Source: data.proto")
public final class DataOperationsGrpc {

  private DataOperationsGrpc() {}

  public static final String SERVICE_NAME = "kaist.iclab.abclogger.DataOperations";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum,
      kaist.iclab.abclogger.grpc.DatumProto.Empty> getCreateDatumMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateDatum",
      requestType = kaist.iclab.abclogger.grpc.DatumProto.Datum.class,
      responseType = kaist.iclab.abclogger.grpc.DatumProto.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum,
      kaist.iclab.abclogger.grpc.DatumProto.Empty> getCreateDatumMethod() {
    io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum, kaist.iclab.abclogger.grpc.DatumProto.Empty> getCreateDatumMethod;
    if ((getCreateDatumMethod = DataOperationsGrpc.getCreateDatumMethod) == null) {
      synchronized (DataOperationsGrpc.class) {
        if ((getCreateDatumMethod = DataOperationsGrpc.getCreateDatumMethod) == null) {
          DataOperationsGrpc.getCreateDatumMethod = getCreateDatumMethod =
              io.grpc.MethodDescriptor.<kaist.iclab.abclogger.grpc.DatumProto.Datum, kaist.iclab.abclogger.grpc.DatumProto.Empty>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateDatum"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Datum.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Empty.getDefaultInstance()))
              .setSchemaDescriptor(new DataOperationsMethodDescriptorSupplier("CreateDatum"))
              .build();
        }
      }
    }
    return getCreateDatumMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data,
      kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> getReadDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReadData",
      requestType = kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data.class,
      responseType = kaist.iclab.abclogger.grpc.DatumProto.Datum.Data.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data,
      kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> getReadDataMethod() {
    io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data, kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> getReadDataMethod;
    if ((getReadDataMethod = DataOperationsGrpc.getReadDataMethod) == null) {
      synchronized (DataOperationsGrpc.class) {
        if ((getReadDataMethod = DataOperationsGrpc.getReadDataMethod) == null) {
          DataOperationsGrpc.getReadDataMethod = getReadDataMethod =
              io.grpc.MethodDescriptor.<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data, kaist.iclab.abclogger.grpc.DatumProto.Datum.Data>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReadData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Datum.Data.getDefaultInstance()))
              .setSchemaDescriptor(new DataOperationsMethodDescriptorSupplier("ReadData"))
              .build();
        }
      }
    }
    return getReadDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects,
      kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> getReadSubjectsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReadSubjects",
      requestType = kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects.class,
      responseType = kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects,
      kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> getReadSubjectsMethod() {
    io.grpc.MethodDescriptor<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects, kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> getReadSubjectsMethod;
    if ((getReadSubjectsMethod = DataOperationsGrpc.getReadSubjectsMethod) == null) {
      synchronized (DataOperationsGrpc.class) {
        if ((getReadSubjectsMethod = DataOperationsGrpc.getReadSubjectsMethod) == null) {
          DataOperationsGrpc.getReadSubjectsMethod = getReadSubjectsMethod =
              io.grpc.MethodDescriptor.<kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects, kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReadSubjects"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects.getDefaultInstance()))
              .setSchemaDescriptor(new DataOperationsMethodDescriptorSupplier("ReadSubjects"))
              .build();
        }
      }
    }
    return getReadSubjectsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataOperationsStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataOperationsStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataOperationsStub>() {
        @java.lang.Override
        public DataOperationsStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataOperationsStub(channel, callOptions);
        }
      };
    return DataOperationsStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataOperationsBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataOperationsBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataOperationsBlockingStub>() {
        @java.lang.Override
        public DataOperationsBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataOperationsBlockingStub(channel, callOptions);
        }
      };
    return DataOperationsBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataOperationsFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataOperationsFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DataOperationsFutureStub>() {
        @java.lang.Override
        public DataOperationsFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DataOperationsFutureStub(channel, callOptions);
        }
      };
    return DataOperationsFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class DataOperationsImplBase implements io.grpc.BindableService {

    /**
     */
    public void createDatum(kaist.iclab.abclogger.grpc.DatumProto.Datum request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateDatumMethod(), responseObserver);
    }

    /**
     */
    public void readData(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> responseObserver) {
      asyncUnimplementedUnaryCall(getReadDataMethod(), responseObserver);
    }

    /**
     */
    public void readSubjects(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> responseObserver) {
      asyncUnimplementedUnaryCall(getReadSubjectsMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateDatumMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kaist.iclab.abclogger.grpc.DatumProto.Datum,
                kaist.iclab.abclogger.grpc.DatumProto.Empty>(
                  this, METHODID_CREATE_DATUM)))
          .addMethod(
            getReadDataMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data,
                kaist.iclab.abclogger.grpc.DatumProto.Datum.Data>(
                  this, METHODID_READ_DATA)))
          .addMethod(
            getReadSubjectsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects,
                kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects>(
                  this, METHODID_READ_SUBJECTS)))
          .build();
    }
  }

  /**
   */
  public static final class DataOperationsStub extends io.grpc.stub.AbstractAsyncStub<DataOperationsStub> {
    private DataOperationsStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataOperationsStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataOperationsStub(channel, callOptions);
    }

    /**
     */
    public void createDatum(kaist.iclab.abclogger.grpc.DatumProto.Datum request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateDatumMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void readData(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReadDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void readSubjects(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects request,
        io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReadSubjectsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DataOperationsBlockingStub extends io.grpc.stub.AbstractBlockingStub<DataOperationsBlockingStub> {
    private DataOperationsBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataOperationsBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataOperationsBlockingStub(channel, callOptions);
    }

    /**
     */
    public kaist.iclab.abclogger.grpc.DatumProto.Empty createDatum(kaist.iclab.abclogger.grpc.DatumProto.Datum request) {
      return blockingUnaryCall(
          getChannel(), getCreateDatumMethod(), getCallOptions(), request);
    }

    /**
     */
    public kaist.iclab.abclogger.grpc.DatumProto.Datum.Data readData(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data request) {
      return blockingUnaryCall(
          getChannel(), getReadDataMethod(), getCallOptions(), request);
    }

    /**
     */
    public kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects readSubjects(kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects request) {
      return blockingUnaryCall(
          getChannel(), getReadSubjectsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DataOperationsFutureStub extends io.grpc.stub.AbstractFutureStub<DataOperationsFutureStub> {
    private DataOperationsFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataOperationsFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataOperationsFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kaist.iclab.abclogger.grpc.DatumProto.Empty> createDatum(
        kaist.iclab.abclogger.grpc.DatumProto.Datum request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateDatumMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kaist.iclab.abclogger.grpc.DatumProto.Datum.Data> readData(
        kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data request) {
      return futureUnaryCall(
          getChannel().newCall(getReadDataMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects> readSubjects(
        kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects request) {
      return futureUnaryCall(
          getChannel().newCall(getReadSubjectsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_DATUM = 0;
  private static final int METHODID_READ_DATA = 1;
  private static final int METHODID_READ_SUBJECTS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DataOperationsImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DataOperationsImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_DATUM:
          serviceImpl.createDatum((kaist.iclab.abclogger.grpc.DatumProto.Datum) request,
              (io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Empty>) responseObserver);
          break;
        case METHODID_READ_DATA:
          serviceImpl.readData((kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Data) request,
              (io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Data>) responseObserver);
          break;
        case METHODID_READ_SUBJECTS:
          serviceImpl.readSubjects((kaist.iclab.abclogger.grpc.DatumProto.Datum.Query.Subjects) request,
              (io.grpc.stub.StreamObserver<kaist.iclab.abclogger.grpc.DatumProto.Datum.Subjects>) responseObserver);
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

  private static abstract class DataOperationsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataOperationsBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return kaist.iclab.abclogger.grpc.DatumProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataOperations");
    }
  }

  private static final class DataOperationsFileDescriptorSupplier
      extends DataOperationsBaseDescriptorSupplier {
    DataOperationsFileDescriptorSupplier() {}
  }

  private static final class DataOperationsMethodDescriptorSupplier
      extends DataOperationsBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DataOperationsMethodDescriptorSupplier(String methodName) {
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
      synchronized (DataOperationsGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataOperationsFileDescriptorSupplier())
              .addMethod(getCreateDatumMethod())
              .addMethod(getReadDataMethod())
              .addMethod(getReadSubjectsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
