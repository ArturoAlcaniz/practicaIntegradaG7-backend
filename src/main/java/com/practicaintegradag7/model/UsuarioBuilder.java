package com.practicaintegradag7.model;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.codec.digest.DigestUtils;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;

public class UsuarioBuilder {
	@Id
	@Column(name = "dni")
	private String dni;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "apellidos")
	private String apellidos;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "primeraDosis")
	private boolean primeraDosis;
	
	@Column(name = "segundaDosis")
	private boolean segundaDosis;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "centro")
	private Centro centro;
	
	@Column(name = "rol")
	private String rol;
	
	public void hashPassword() {
		this.password = DigestUtils.sha256Hex(password);
	}
	
    private boolean validateEmail(String email) {
    	Pattern regexEmail = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    	Matcher compareEmail = regexEmail.matcher(email); 
    	return compareEmail.matches();
	}
	
	
	private boolean validateRol(String rol) {
		boolean validez = false;
		
		if (rol.equalsIgnoreCase("administrador") || rol.equalsIgnoreCase("sanitario") || rol.equalsIgnoreCase("paciente")) 
			validez = true;
		
		return validez;
	}

	public String getDni() {
		return dni;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getEmail() {
		return email;
	}

	public boolean isPrimeraDosis() {
		return primeraDosis;
	}

	public boolean isSegundaDosis() {
		return segundaDosis;
	}

	public UsuarioBuilder  setPrimeraDosis(boolean primeraDosis) {
		this.primeraDosis = primeraDosis;
		return this;
	}
	
	public UsuarioBuilder setSegundaDosis(boolean segundaDosis) {
		this.segundaDosis = segundaDosis;
		return this;
	}
	
	public String getPassword() {
		return password;
	}

	public Centro getCentro() {
		return centro;
	}

	public String getRol() {
		return rol;
	}

	public UsuarioBuilder  setPassword(String password) {
		this.password = password;
		return this;
	}
	
	/**
	 * Encrypts own password via Cesar Cypher(3), then B64 encoding.
	 * Appends a 'd' to the beggining after the procedure, to indiciate that this string is encoded. 
	 */
	public void encryptDNI() throws CifradoContrasenaException {
		String cyph = cypher(this.dni, 3);
		byte[] aux;
		
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(this.nombre);
			if(builder.toString().length() < 16)
				while(builder.toString().length() < 16) builder.append('a');
			String flag = "a";
			String keyS = builder.toString();
	        Key aesKey = new SecretKeySpec(keyS.getBytes(), "AES");
	        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, flag.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, aesKey, parameterSpec);

	        byte[] cipherText = cipher.doFinal(cyph.getBytes(StandardCharsets.UTF_8));

	        ByteBuffer byteBuffer = ByteBuffer.allocate(flag.getBytes().length + cipherText.length);
	        byteBuffer.put(flag.getBytes());
	        byteBuffer.put(cipherText);
	        
	        aux = byteBuffer.array();
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			throw new CifradoContrasenaException(e.getMessage());
		}
		
		this.dni = "a" + Base64.getEncoder().encodeToString(aux);
	}
	
	/**
	 * Moves only upper/lowercase letters
	 */
	private static String cypher(String og, int n) {
		for(int i = 0; i < og.length(); i++) {
			char aux = og.charAt(i);
			if(aux > 64 && aux < 91) {
				og = divideAndCypher(og, n, aux, i, false);
			} else if(aux > 96 && aux < 123) {
				og = divideAndCypher(og, n, aux, i, true);
			}
		}
		return og;
	}
	
	/**
	 * mode = False: Uppercase letter (for looping back ex. Z -> A)
	 * mode = True: Lowercase letter
	 */
	private static String divideAndCypher(String og, int n, char c, int pos, boolean lowercase) {
		String m1 = og.substring(0, pos);
		String m2 = og.substring(pos + 1, og.length());
		c += n;
		if((!lowercase && c > 90) || (lowercase && c > 122)) c -= 26;
		return m1 + c + m2;
	}
	
	public void decryptDNI() throws CifradoContrasenaException {
		String pwd = this.dni.substring(1);
		String decyph;
		
		byte[] bytes = Base64.getDecoder().decode(pwd.getBytes());
		
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(this.nombre);
			if(builder.toString().length() < 16)
				while(builder.toString().length() < 16) builder.append('a');
			String keyS = builder.toString();
			Key aesKey = new SecretKeySpec(keyS.getBytes(), "AES");
			AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, bytes, 0, 1);
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmIv);
	        byte[] plainText = cipher.doFinal(bytes, 1, bytes.length - 1);

	        decyph = new String(plainText, StandardCharsets.UTF_8);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
			throw new CifradoContrasenaException(e.getMessage());
		}
		
		decyph = decypher(decyph, 3);
		
		this.dni = decyph;
	}
	
	private static String decypher(String og, int n) {
		for(int i = 0; i < og.length(); i++) {
			char aux = og.charAt(i);
			if(aux > 64 && aux < 91) {
				og = divideAndDecypher(og, n, aux, i, false);
			} else if(aux > 96 && aux < 123) {
				og = divideAndDecypher(og, n, aux, i, true);
			}
		}
		return og;
	}
	
	private static String divideAndDecypher(String og, int n, char c, int pos, boolean lowercase) {
		String m1 = og.substring(0, pos);
		String m2 = og.substring(pos + 1, og.length());
		c -= n;
		if((!lowercase && c > 90) || (lowercase && c > 122)) c += 26;
		return m1 + c + m2;
	}
}
