package ie.gmit.ds;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.NotNull;

import ie.gmit.ds.HashRequest;
import ie.gmit.ds.HashResponse;
import ie.gmit.ds.PasswordServiceGrpc;
import ie.gmit.ds.ValidatePasswordRequest;
import ie.gmit.ds.ValidatePasswordResponse;

public class PasswordClient {

	private static final Logger logger = Logger.getLogger(PasswordClient.class.getName());
	private final ManagedChannel channel;
	private final PasswordServiceGrpc.PasswordServiceBlockingStub synchronousStub;
	private final PasswordServiceGrpc.PasswordServiceStub asynchronousStub;

	private UserStorage storage = UserStorage.getInstance();

	private UserModel userSession;

	public PasswordClient(String host, int port) {
		// TODO Auto-generated constructor stub
		this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

		this.synchronousStub = PasswordServiceGrpc.newBlockingStub(channel);
		this.asynchronousStub = PasswordServiceGrpc.newStub(channel);

	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public boolean hash(@NotNull int id, @NotNull String password) {

		StreamObserver<HashResponse> responseObserver = new StreamObserver<HashResponse>() {

			@Override
			public void onNext(HashResponse response) {
				// TODO Auto-generated method stub
				String hashedPassword = response.getHashedPassword();
				String salt = response.getSalt();

				System.out.println("hashed password: " + hashedPassword + " \nsalt: " + salt);
				// error from here down

				boolean updated = storage.updateUserCredentials(response.getUserId(), hashedPassword, salt);

				if (updated) {
					System.out.println("User updated:" + updated);
				} else {
					System.out.println("Error in user data, updated: " + updated);

				}

			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				System.out.println("Error storing user");
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				System.out.println("user has been stored");
			}
		};

		HashRequest req = HashRequest.newBuilder().setUserId(id).setPassword(password).build();

		try {
			asynchronousStub.hash(req, responseObserver);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Exception thrown!");
			return false;
		}

		return true;
	}

	public boolean validate(@NotNull String password, @NotNull String salt, @NotNull String hashedPassword) {
		
		boolean matches = false;

		if (password == null || salt == null || hashedPassword == null) {
			return false;
		}

		ValidatePasswordRequest req = ValidatePasswordRequest.newBuilder().setPassword(password).setSalt(salt)
				.setHashedPassword(hashedPassword).build();

		ValidatePasswordResponse res;

		try {
			res = synchronousStub.validate(req);

			matches = res.getPasswordsMatch();

			System.out.println("\nPasswords match: " + matches);

		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return false;
		}

		return matches;
	}

}
