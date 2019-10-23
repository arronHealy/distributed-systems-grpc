package ie.gmit.ds;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;

public class HashServer {

    private Server server;
    private static final Logger logger = Logger.getLogger(HashServer.class.getName());

    
    public static void main(String[] args) throws IOException, InterruptedException {
        final HashServer server = new HashServer();
        server.start();
        server.blockUntilShutdown();
    }


    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 5000;
        server = ServerBuilder.forPort(port)
                .addService(new HashServer.PasswordServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
//                System.err.println("*** shutting down gRPC server since JVM is shutting down");
//                HelloWorldServer.this.stop();
//                System.err.println("*** server shut down");
//            }
//        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
    
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {
    	
    	@Override
    	public void hash(HashRequest req, StreamObserver<HashResponse> responseObserver) {
    		HashResponse reply = HashResponse.newBuilder().setHashedPassword(req.getPassword()).setUserId(req.getUserId()).build();
    		responseObserver.onNext(reply);
    		responseObserver.onCompleted();
    	}
    }
    
}
