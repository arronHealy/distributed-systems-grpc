package ie.gmit.ds;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;


import com.fasterxml.jackson.annotation.JsonProperty;




@XmlRootElement(name="UserModel")
public class UserModel {

	@NotNull
    private int userId;
    
	@NotNull
	@Length(min=6, max=20)
    private String userPassword;
    
	@NotNull
	@Pattern(regexp=".+@.+\\.[a-z]+")
    private String email;
    
	@NotNull
	@Length(min=4, max=25)
    private String username;
	
	private String salt;
	
	private String hashedPassword;

    public UserModel() {
        // Needed for Jackson deserialisation
    }

    public UserModel(int userId, String password, String email, String username) {
        this.userId = userId;
        this.userPassword = password;
        this.email = email;
        this.username = username;
    }
    
    public UserModel(int userId, String password, String email) {
        this.userId = userId;
        this.userPassword = password;
        this.email = email;
    }
    
    public void setSalt(String salt) {
    	this.salt = salt;
    }
    
    
    // Setters needed for xml mapping
    public void setHashedPassword(String password) {
    	this.hashedPassword = password;
    	this.userPassword = "";
    }
    
    public void setUserId(int id) {
    	this.userId = id;
    }
    
    public void setUserPassword(String password) {
    	this.userPassword = password;
    }
    
    public void setEmail(String email) {
    	this.email = email;
    }
    
    public void setUsername(String name) {
    	this.username = name;
    }
    

    @JsonProperty
    @XmlElement(name="userId")
    public int getUserId(){
        return userId;
    }
    
    @JsonProperty
    @XmlElement(name="userPassword")
    public String getUserPassword() {
    	return userPassword;
    }
    
    @JsonProperty
    @XmlElement(name="email")
    public String getEmail() {
    	return email;
    }
    
    @JsonProperty
    @XmlElement(name="username")
    public String getUsername() {
    	return username;
    }
    
    @JsonProperty
    public String getSalt() {
    	return salt;
    }
    
    @JsonProperty
    public String getHashedPassword() {
    	return hashedPassword;
    }
   
}
