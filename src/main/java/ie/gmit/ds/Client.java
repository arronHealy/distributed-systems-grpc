package ie.gmit.ds;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private final ManagedChannel channel;
	private final PasswordServiceGrpc.PasswordServiceBlockingStub passwordClientStub;
	
	private static Scanner console;

	/**
	 * Construct client for accessing HelloWorld server using the existing channel.
	 */
	public Client(String host, int port) {
		this.channel = ManagedChannelBuilder.forAddress(host, port)
				// Channels are secure by default (via SSL/TLS). For the example we disable TLS
				// to avoid
				// needing certificates.
				.usePlaintext().build();
		passwordClientStub = PasswordServiceGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	/** Say hello to server. */
	/*
	 * public void greet(String name) { logger.info("Will try to greet " + name +
	 * " ..."); HelloRequest request =
	 * HelloRequest.newBuilder().setName(name).build(); HelloReply response; try {
	 * response = greeterClientStub.sayHello(request); } catch
	 * (StatusRuntimeException e) { logger.log(Level.WARNING, "RPC failed: {0}",
	 * e.getStatus()); return; } logger.info("Greeting: " + response.getMessage());
	 * }
	 */

	public void hash(int id, String password) {

		if (id < 0 || password == null) {
			System.out.println("Invalid values entered!");
			return;
		}

		HashRequest req = HashRequest.newBuilder().setUserId(id).setPassword(password).build();

		HashResponse res;

		try {
			res = passwordClientStub.hash(req);
			
			int userId = res.getUserId();
			String pwd = res.getHashedPassword();
			String salt = res.getSalt();
			
			System.out.println("\nUser ID: " + res.getUserId() + "\nHashed password: " + res.getHashedPassword()
			+ "\nGenerated Salt: " + res.getSalt());
			
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
		

	}

	public void validate(String password, String salt, String hashedPassword) {
		
		if((password == null || password.length() <= 0) || (hashedPassword == null || hashedPassword.length() <= 0)) {
			System.out.println("Invalid values entered!");
			return;
		}
		
		ValidatePasswordRequest req = ValidatePasswordRequest.newBuilder().setPassword(password).setSalt(salt).setHashedPassword(hashedPassword).build();
		
		ValidatePasswordResponse res;
		
		try {
			res = passwordClientStub.validate(req);
			
			boolean matches = res.getPasswordsMatch();
			
			System.out.println("\nPasswords match: " + matches);
			
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
	}

	public static void main(String[] args) throws Exception {

		console = new Scanner(System.in);

		Client client = new Client("localhost", 5000);
		
		try {

			System.out.print("Please enter a user ID: ");
			int userId = console.nextInt();

			System.out.print("\nPlease enter your password for hashing: ");
			String password = console.next();

			if (userId < 0 || (password == null || password.length() <= 0)) {

				do {
					System.out.print("Please enter a user ID: ");
					userId = console.nextInt();

					System.out.print("\nPlease enter your password for hashing: ");
					password = console.next();

				} while (userId < 0 || (password == null || password.length() <= 0));
			}
			
			client.hash(userId, password);

			System.out.print("\nPlease enter your password: ");
			String pwd = console.next();

			System.out.print("\nPlease enter the hashed password: ");
			String hashed = console.next();

			System.out.print("\nPlease enter your salt value: ");
			String salt = console.next();
			
			client.validate(pwd, salt, hashed);

		} finally {
			console.close();
			client.shutdown();
		}
	}

}
