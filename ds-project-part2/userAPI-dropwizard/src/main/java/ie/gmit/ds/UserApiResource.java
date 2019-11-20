package ie.gmit.ds;

import java.util.Collection;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Path("/user")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })
public class UserApiResource {

	private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

	private PasswordClient grpcClient;

	private Validator validatior = factory.getValidator();

	private Set<ConstraintViolation<UserModel>> violations;

	private UserStorage storage = UserStorage.getInstance();

	public UserApiResource(String host, int port) {

		grpcClient = new PasswordClient(host, port);
	}

	@GET
	public Response getUsers() {
		Collection<UserModel> users = storage.getAllUsers();
		
		return users != null ? Response.status(Status.OK).entity(users).build() : Response.status(Status.NOT_FOUND).entity("Could not retrieve Users! Please try again...").build();
	}

	@GET
	@Path("{id}")
	public Response getUserById(@PathParam("id") int id) {
		
		if(!storage.containsUserId(id)) {
			return Response.status(Status.NOT_FOUND).entity("No User with this ID Exists! Could not update...")
					.build();
		}
		
		UserModel user = storage.getUserById(id);
		
		return user != null ? Response.status(Status.OK).entity(user).build() : Response.status(Status.NOT_FOUND).entity("No User with this ID found!").build();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response createUser(UserModel userRequest) {

		violations = validatior.validate(userRequest);

		if (violations.size() > 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid User Data! Violations present..." + violations.toString()).build();
		}

		if (storage.containsUserId(userRequest.getUserId())) {
			return Response.status(Status.BAD_REQUEST).entity("User with this ID already Exists!").build();
		}

		storage.addUser(userRequest);

		boolean created = grpcClient.hash(userRequest.getUserId(), userRequest.getUserPassword());
		
		if(!created) {
			storage.deleteUser(userRequest.getUserId());
		}

		return created
				? Response.status(Status.CREATED).type(MediaType.TEXT_PLAIN)
						.entity("User Details Successfully Added! You can now login...").build()
				: Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN)
						.entity("Error Adding User Details! Please Try again...").build();
	}

	@POST
	@Path("update")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response updateUser(UserModel userRequest) {

		violations = validatior.validate(userRequest);

		if (violations.size() > 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid User Data! Violations present..." + violations.toString()).build();
		}

		if (!storage.containsUserId(userRequest.getUserId())) {
			return Response.status(Status.NOT_FOUND).entity("No User with this ID Exists! Could not update...")
					.build();
		}

		storage.addUser(userRequest);

		boolean updated = grpcClient.hash(userRequest.getUserId(), userRequest.getUserPassword());

		return updated
				? Response.status(Status.CREATED).type(MediaType.TEXT_PLAIN)
						.entity("User Details Successfully updated!").build()
				: Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN)
						.entity("Error Updating User Details! Please Try again...").build();
	}

	@POST
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response deleteUser(UserModel userRequest) {

		violations = validatior.validate(userRequest);

		if (violations.size() > 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid User Data! Violations present..." + violations.toString()).build();
		}

		if (!storage.containsUserId(userRequest.getUserId())) {
			return Response.status(Status.NOT_FOUND).entity("No User with this ID Exists! Could not delete...")
					.build();
		}

		return storage.deleteUser(userRequest.getUserId())
				? Response.status(Status.ACCEPTED).entity("User Deleted Successfully!").build()
				: Response.status(Status.BAD_REQUEST).entity("Error occurred! Could not Delete user...").build();
	}

	@POST
	@Path("login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response loginUser(UserModel userRequest) {

		violations = validatior.validate(userRequest);

		if (violations.size() > 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Invalid User Data! Violations present..." + violations.toString()).build();
		}

		if (!storage.containsUserId(userRequest.getUserId())) {
			return Response.status(Status.NOT_FOUND).entity("No User with this ID Exists! Could not delete...")
					.build();
		}

		UserModel loginUser = storage.getUserById(userRequest.getUserId());

		if (loginUser == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("An error has occured! Could not load user. Please Try Again...").build();
		}

		boolean userLoggedIn = grpcClient.validate(userRequest.getUserPassword(), loginUser.getSalt(),
				loginUser.getHashedPassword());

		return userLoggedIn
				? Response.status(Status.ACCEPTED).entity("Log in Accepted! Welcome back " + loginUser.getUsername())
						.build()
				: Response.status(Status.BAD_REQUEST).entity("Login Failed! Not Valid Credentials...").build();
	}

}