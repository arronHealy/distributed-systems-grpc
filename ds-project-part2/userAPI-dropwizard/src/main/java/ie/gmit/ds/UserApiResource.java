package ie.gmit.ds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.grpc.Context.Storage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;



@Path("/user")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class UserApiResource {

	private final Logger logger = Logger.getLogger(UserApiResource.class.getName());
	
    private PasswordClient grpcClient;
    
    private UserModel userSession;
    
    private Validator validatior;
    
    private UserStorage storage = UserStorage.getInstance();

    public UserApiResource(String host, int port){
    	
        grpcClient = new PasswordClient(host, port);

    }
    
    @GET
    public Collection<UserModel> getUsers(){
    	return storage.getAllUsers();
    }
    
    @GET
    @Path("{id}")
    public UserModel getUserById(@PathParam("id") int id) {
    	return storage.getUserById(id);
    }
    
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createUser(UserModel userRequest) {
    	
    	System.out.println("User values: " + userRequest.getUserId() + ", " + userRequest.getUserPassword());
    	
    	if(userRequest.getUserId() <= 0 || userRequest.getEmail() == null || userRequest.getUsername() == null || userRequest.getUserPassword() == null) {
    		return Response.status(Status.BAD_REQUEST).entity("Invalid User Data!").build();
    	} 
    	
    	
    	if(storage.containsUserId(userRequest.getUserId())) {
    		return Response.status(Status.BAD_REQUEST).entity("User with this ID already Exists!").build();
    	}
    	
    	storage.addUser(userRequest);
    	
    	boolean created = grpcClient.hash(userRequest.getUserId(), userRequest.getUserPassword());
    	
    	String entity = "User created: " + created;
    	
    	return Response.status(Status.CREATED).type(MediaType.TEXT_PLAIN).entity(entity).build();
    }
    
    @POST
    @Path("update")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUser(UserModel userRequest) {
    	
    	if(userRequest.getUserId() <= 0 || userRequest.getEmail() == null || userRequest.getUsername() == null || userRequest.getUserPassword() == null) {
    		return Response.status(Status.BAD_REQUEST).entity("Invalid User Data!").build();
    	} 
    	
    	
    	if(!storage.containsUserId(userRequest.getUserId())) {
    		return Response.status(Status.BAD_REQUEST).entity("No User with this ID Exists! Could not update...").build();
    	}
    	
    	storage.addUser(userRequest);
    	
    	boolean updated = grpcClient.hash(userRequest.getUserId(), userRequest.getUserPassword());
    	
    	String entity = "User updated: " + updated;
    	
    	return Response.status(Status.CREATED).type(MediaType.TEXT_PLAIN).entity(entity).build();
    	
    }
    
    @POST
    @Path("delete")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteUser(UserModel userRequest) {
    	
    	if(!storage.containsUserId(userRequest.getUserId())) {
    		return Response.status(Status.BAD_REQUEST).entity("No User with this ID Exists! Could not delete...").build();
    	}
    	
    	return storage.deleteUser(userRequest.getUserId()) ? Response.status(Status.ACCEPTED).entity("User Deleted Successfully!").build() : Response.status(Status.BAD_REQUEST).entity("Error occurred! Could not Delete user...").build();
    }
    
    @POST
    @Path("login")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response loginUser(UserModel userRequest) {
    
    	if(userRequest.getUserId() <= 0 || userRequest.getEmail() == null || userRequest.getUsername() == null || userRequest.getUserPassword() == null) {
    		return Response.status(Status.BAD_REQUEST).entity("Invalid User Data!").build();
    	} 
    	
    	if(!storage.containsUserId(userRequest.getUserId())) {
    		return Response.status(Status.BAD_REQUEST).entity("No User with this ID Exists! Could not delete...").build();
    	}
    	
    	UserModel loginUser = storage.getUserById(userRequest.getUserId());
    	
    	if(loginUser == null) {
    		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("An error has occured! Could not load user. Please Try Again...").build();
    	}
    	
    	boolean userLoggedIn = grpcClient.validate(userRequest.getUserPassword(), loginUser.getSalt(), loginUser.getHashedPassword());
    	
    	
    	return userLoggedIn ? Response.status(Status.ACCEPTED).entity("Log in Accepted! Welcome back " + loginUser.getUsername()).build() : Response.status(Status.BAD_REQUEST).entity("Login Failed! Not Valid Credentials...").build();
    }
    

}