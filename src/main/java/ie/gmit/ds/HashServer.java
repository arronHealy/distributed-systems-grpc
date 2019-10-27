package ie.gmit.ds;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
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
    	
    	private PasswordManager passwordManager = PasswordManager.getInstance();
    	
    	private String hashedPassword;
    	
    	private byte[] salt;
    	
    	private boolean passwordsMatch;
    	
    	@Override
    	public void hash(HashRequest req, StreamObserver<HashResponse> responseObserver) {
    		
    		salt = passwordManager.generateSalt();
    		
    		String[] hashes = passwordManager.hashPassword(req.getPassword(), salt);
    		
    		HashResponse reply = HashResponse.newBuilder().setHashedPassword(hashes[0]).setSalt(hashes[1]).setUserId(req.getUserId()).build();
    		responseObserver.onNext(reply);
    		responseObserver.onCompleted();
    	}
    	
    	@Override
    	public void validate(ValidatePasswordRequest req, StreamObserver<ValidatePasswordResponse> responseObserver) {
    		
    		//byte[] hash = Base64.getDecoder().decode(req.getHashedPassword());
    		
    		passwordsMatch = passwordManager.passwordMatch(req.getPassword(), req.getSalt(), req.getHashedPassword());
    		
    		ValidatePasswordResponse reply = ValidatePasswordResponse.newBuilder().setPasswordsMatch(passwordsMatch).build();
    		responseObserver.onNext(reply);
    		responseObserver.onCompleted();
    	}
    }
    
}
