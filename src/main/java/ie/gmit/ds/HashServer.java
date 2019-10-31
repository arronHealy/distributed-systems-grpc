package ie.gmit.ds;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.lang3.math.NumberUtils;

public class HashServer {

	private Server server;
	private static final Logger logger = Logger.getLogger(HashServer.class.getName());

	public static void main(String[] args) throws IOException, InterruptedException {
    	
    	int port = 5000;
    	
    	if(args.length == 1) {
    		
    		if(NumberUtils.isNumber(args[0])) {
    			
    			if(Integer.parseInt(args[0]) > 2000) {
    				port = Integer.parseInt(args[0]);
    			}	
    		}
    	}
    	
        final HashServer server = new HashServer();
        server.start(port);
        server.blockUntilShutdown();
    }

	private void start(int port) throws IOException {
		/* The port on which the server should run */
		server = ServerBuilder.forPort(port).addService(new HashServer.PasswordServiceImpl()).build().start();
		logger.info("Server Defaults to port 5000 if no port specified! \nServer started, listening on " + port);
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

		private byte[] salt;

		private boolean passwordsMatch;

		@Override
		public void hash(HashRequest req, StreamObserver<HashResponse> responseObserver) {

			salt = passwordManager.generateSalt();

			String[] hashes = passwordManager.hashPassword(req.getPassword(), salt);

			HashResponse reply = HashResponse.newBuilder().setHashedPassword(hashes[0]).setSalt(hashes[1])
					.setUserId(req.getUserId()).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public void validate(ValidatePasswordRequest req, StreamObserver<ValidatePasswordResponse> responseObserver) {

			passwordsMatch = passwordManager.passwordMatch(req.getPassword(), req.getSalt(), req.getHashedPassword());

			ValidatePasswordResponse reply = ValidatePasswordResponse.newBuilder().setPasswordsMatch(passwordsMatch)
					.build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}

}
