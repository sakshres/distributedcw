package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
{
	@Override
	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{

        	int A = request.getA();
 	        int B = request.getB();

                int mC = A+B;
        
		MatrixReply response = MatrixReply.newBuilder().setC(mC).build();
		reply.onNext(response);
		reply.onCompleted();

	}

	@Override
    	public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
    	{

		int mA = request.getA();
		int mB = request.getB(); 

		int mC = mA*mB;

		MatrixReply response = MatrixReply.newBuilder().setC(mC).build();
                reply.onNext(response);
                reply.onCompleted();

	}

    	 public void addBlocks(MatrixRequest request, StreamObserver<MatrixReply> reply)
	 {
		int C=request.getA()+request.getB();
		MatrixReply response = MatrixReply.newBuilder().setC(C).build();
		reply.onNext(response);
		reply.onCompleted();
	 }

}
