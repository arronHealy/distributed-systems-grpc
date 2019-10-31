package ie.gmit.ds;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class PasswordManager {
	
	private static final Random RANDOM = new SecureRandom();
	
	private static final int ITERATIONS = 10000;
	
	private static final int KEY_LENGTH = 256;
	
	private String encryptionMethod = "PBKDF2WithHmacSHA1";
	
	private static PasswordManager pwd = new PasswordManager();
	
	private PasswordManager() {
		
	}
	
	public static PasswordManager getInstance() {
		return pwd;
	}
	
	public byte[] generateSalt() {
		byte[] salt = new byte[32];
		RANDOM.nextBytes(salt);
		return salt;
	}
	
	public String[] hashPassword(String password, byte[] salt) {
		
		PBEKeySpec spec = new PBEKeySpec(password.trim().toCharArray(), salt, ITERATIONS, KEY_LENGTH);
		
		try {
				SecretKeyFactory sfk = SecretKeyFactory.getInstance(encryptionMethod);
				byte[] hash = sfk.generateSecret(spec).getEncoded();
				
				return new String[] { Base64.getEncoder().encodeToString(hash), Base64.getEncoder().encodeToString(salt) };
				
		} catch (NoSuchAlgorithmException | InvalidKeySpecException sp) {
			// TODO: handle exception
			throw new AssertionError("Encryption Error! Try again...", sp);
		} finally {
			spec.clearPassword();
		}
	}
	
	public boolean passwordMatch(String password, String salt, String expectedHash) {
		
		String[] pwdHash = hashPassword(password, Base64.getDecoder().decode(salt));
		
		return Arrays.equals(pwdHash[0].toCharArray(), expectedHash.toCharArray());
	}

}
