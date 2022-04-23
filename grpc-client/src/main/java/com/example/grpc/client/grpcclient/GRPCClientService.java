
package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.util.Random;



@Service
public class GRPCClientService {
        ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub> stubs = new ArrayList<MatrixServiceGrpc.MatrixServiceBlockingStub>();

    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build(); 
                       
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }
    public int[][] add(int[][] mA,int[][] mB){
		int[][] resp =grpcClient(mA, mB, 10, false);           
                return resp;//result;
    }

    public int[][] multiply(int[][] mA, int[][] mB, int deadline){
                int[][] resp =grpcClient(mA, mB, deadline, true);           
                return resp;
        }

        public long footprint(int random, int[][] matrixA, int[][] matrixB, int n){
                long startTime = System.nanoTime();
                MatrixReply temp=stubs.get(random).multiplyBlock(MatrixRequest.newBuilder().setA(matrixA[0][0]).setB(matrixB[n-1][n-1]).build());
                long endTime = System.nanoTime();

                return (endTime-startTime)/1000000000;
        }

	public int[][] grpcClient(int[][] matrixA, int[][] matrixB, int deadline, boolean mult){
                ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel3 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel4 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel5 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel6 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel7 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  
                ManagedChannel channel8 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();  

                MatrixServiceGrpc.MatrixServiceBlockingStub stub1 = MatrixServiceGrpc.newBlockingStub(channel1);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub2 = MatrixServiceGrpc.newBlockingStub(channel2);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub3 = MatrixServiceGrpc.newBlockingStub(channel3);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub4 = MatrixServiceGrpc.newBlockingStub(channel4);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub5 = MatrixServiceGrpc.newBlockingStub(channel5);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub6 = MatrixServiceGrpc.newBlockingStub(channel6);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub7 = MatrixServiceGrpc.newBlockingStub(channel7);
                MatrixServiceGrpc.MatrixServiceBlockingStub stub8 = MatrixServiceGrpc.newBlockingStub(channel8);

                stubs.add(stub1);
                stubs.add(stub2);
                stubs.add(stub3);
                stubs.add(stub4);
                stubs.add(stub5);
                stubs.add(stub6);
                stubs.add(stub7);
                stubs.add(stub8);
                
                int stub = 0;
                int n = matrixA.length;
  
                Random r = new Random();
                int random = r.nextInt(8);
		
                long footprint= footprint(random, matrixA, matrixB, n);

                int calls = (int) Math.pow(n, 2);

                double servers = (calls*footprint)/deadline;

                if (servers < 1.00 ){
		        servers = 1.00;
		}

                if(servers <2.00 && servers > 1.00){
			servers = 2.00;
                }
                
                if(servers > 8){
                        System.out.println("Deadline too low");
		}
            
                System.out.println((int) Math.round(servers) + "server used.");
               
		servers = (int) Math.round(servers);

		int[][] c = new int[n][n]; 

                if (mult){
                        for (int i = 0; i < n; i++) { 
                                for (int j = 0; j < n; j++) { 
                                        for (int k = 0; k < n; k++) {                
                                                MatrixReply temp1=stubs.get(stub).multiplyBlock(MatrixRequest.newBuilder().setA(matrixA[i][k]).setB(matrixB[k][j]).build());
                                                if(stub == servers-1){
                                                        stub = 0;
                                                }
                                                else{
                                                        stub++;
                                                }
                                                MatrixReply temp2=stubs.get(stub).addBlock(MatrixRequest.newBuilder().setA(c[i][j]).setB(temp1.getC()).build());
                                                
                                                c[i][j] = temp2.getC();
                                        
                                                if(stub == servers-1){
                                                        stub= 0;
                                                }
                                                else{
                                                        stub++;
                                                }
                                        }
                                }
                        }
                } else{
                        for(int i=0;i<n;i++){
                                for(int j=0;j<n;j++){ 
                                        MatrixReply temp1=stubs.get(stub).addBlock(MatrixRequest.newBuilder().setA(matrixA[i][j]).setB(matrixB[i][j]).build());
                                        c[i][j] = temp1.getC();
                                        if(stub == servers-1){
                                                stub= 0;
                                        }
                                        else{
                                                stub++;
                                        }
                                }
		        }
                }

                // channel1.shutdown();
                // channel2.shutdown();
                // channel3.shutdown();
                // channel4.shutdown();
                // channel5.shutdown();
                // channel6.shutdown();
                // channel7.shutdown();
                // channel8.shutdown();
	    
		return c;
	}
}