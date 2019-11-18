package ie.gmit.ds;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement(name="UserModel")
public class UserModel {

	@NotNull
    private int userId;
    
	@NotEmpty
    private String userPassword;
    
	@NotEmpty
	@Pattern(regexp=".+@.+\\.[a-z]+")
    private String email;
    
	@NotEmpty
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
    
    public void setHashedPassword(String password) {
    	this.hashedPassword = password;
    	this.userPassword = "";
    }
    
    @JsonProperty
    public String getSalt() {
    	return salt;
    }
    
    @JsonProperty
    public String getHashedPassword() {
    	return hashedPassword;
    }

    @JsonProperty
    @XmlElement(name="userId")
    public int getUserId(){
        return userId;
    }
    
    @JsonIgnoreProperties
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
   
}
