package ie.gmit.ds;

import io.dropwizard.Configuration;

public class UserApiConfig extends Configuration {
	
	private String host = "localhost";
	
	private int port = 5000;
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
}