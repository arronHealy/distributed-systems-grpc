package ie.gmit.ds;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
	
	private static final Logger logger = Logger.getLogger(Client.class.getName());
    private final ManagedChannel channel;
    private final PasswordServiceGrpc.PasswordServiceBlockingStub passwordClientStub;

    /** Construct client for accessing HelloWorld server using the existing channel. */
    public Client(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        passwordClientStub = PasswordServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    
    /** Say hello to server. */
    /*
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = greeterClientStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }
    */
    
    public void hash(int id, String password) {
    	
    	if (id < 0 || password == null) {
    		System.out.println("Non valid values entered!");
    		return;
    	}
    	
    	HashRequest req = HashRequest.newBuilder().setUserId(id).setPassword(password).build();
    	
    	HashResponse res;
    	
    	 try {
             res = passwordClientStub.hash(req);
         } catch (StatusRuntimeException e) {
             logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
             return;
         }
         logger.info("Hashed password: " + res.getHashedPassword() + " user ID: " + res.getUserId());
    	
    }
    
    public void validate() {
    	
    }
    
    
    
    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 5000);
        try {
            //client.greet("world");
        	client.hash(0, "this-password");
        } finally {
            client.shutdown();
        }
    }

}
