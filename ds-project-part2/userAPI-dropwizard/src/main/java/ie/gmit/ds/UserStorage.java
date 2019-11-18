package ie.gmit.ds;

import java.util.Collection;
import java.util.HashMap;

public class UserStorage {
	
	private static UserStorage storage = new UserStorage();
	
	private static HashMap<Integer, UserModel> database = new HashMap<>();
	
	private UserStorage() {
		
	}
	
	public static UserStorage getInstance() {
		return storage;
	}
	
	public void addUser(UserModel user) {
		database.put(user.getUserId(), user);
	}
	
	public boolean updateUserCredentials(int id, String hashedPassword, String salt) {
		UserModel user = database.get(new Integer(id));
		
		if (user == null || hashedPassword == null || salt == null) {
			return false;
		}
		
		user.setHashedPassword(hashedPassword);
		
		user.setSalt(salt);
		
		return true;
	}
	
	public Collection<UserModel> getAllUsers() {
		return database.values();
	}
	
	/*
	public boolean editUser(int id, UserModel user) {
		
		if(id <= 0 || user == null) {
			return false;
		}
		
		if(database.get(new Integer(id)) == null) {
			return false;
		}
		
		UserModel updateUser = database.get(new Integer(id));
		
		
		
		return true;
	}
	*/
	public UserModel getUserById(int id) {
		
		return database.get(new Integer(id));
	}
	
	public boolean deleteUser(int id) {
		
		UserModel user = database.remove(new Integer(id));
		
		return user != null;
	}
	
	public boolean containsUserId(int id) {
		return database.containsKey(new Integer(id));
	}

}
