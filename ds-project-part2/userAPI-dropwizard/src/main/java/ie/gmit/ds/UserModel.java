package ie.gmit.ds;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Length;


import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;


//@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="UserModel")
//@JacksonXmlRootElement(localName="UserModel")
public class UserModel {

	@NotNull
    private int userId;
    
	@NotNull
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
    //@JacksonXmlProperty(localName="userId", isAttribute=false)
    public int getUserId(){
        return userId;
    }
    
    @JsonProperty
    @XmlElement(name="userPassword")
    //@JacksonXmlProperty(localName="userPassword", isAttribute=false)
    //@JacksonXmlText
    public String getUserPassword() {
    	return userPassword;
    }
    
    @JsonProperty
    @XmlElement(name="email")
    //@JacksonXmlProperty(localName="email", isAttribute=false)
    //@JacksonXmlText
    public String getEmail() {
    	return email;
    }
    
    @JsonProperty
    @XmlElement(name="username")
    //@JacksonXmlProperty(localName="username", isAttribute=false)
    //@JacksonXmlText
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
