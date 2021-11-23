package com.practicaintegradag7.model;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;

import org.apache.commons.codec.digest.DigestUtils;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Document(collection = "Usuario")
public class Usuario {
	
	@Id
	@Column(name = "email")
	private String email;
	
	@Column(name = "dni")
	private String dni;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "apellidos")
	private String apellidos;
	
	@Column(name = "primeraDosis")
	private boolean primeraDosis;
	
	@Column(name = "segundaDosis")
	private boolean segundaDosis;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "centro")
	private String centro;
	
	@Column(name = "rol")
	private String rol;
	
	@Transient
	@JsonSerialize
	@JsonProperty("dniDenc")
	private String dniDenc;

	Usuario(UsuarioBuilder builder){
		if (!validateEmail(builder.getEmail())) {
			throw new IllegalArgumentException("Email is not valid!");
		}
		
		if (!validateRol(builder.getRol().toLowerCase())) {
			throw new IllegalArgumentException("Rol is not valid!");
		}
		
		if (builder.getRol().equalsIgnoreCase("paciente") || builder.getRol().equalsIgnoreCase("sanitario")) {
			this.primeraDosis = false;
			this.segundaDosis = false;
		} else {
			this.primeraDosis = true;
			this.segundaDosis = true;
		}
		
		this.dni = builder.getDni();
		
		this.nombre = builder.getNombre();
		this.apellidos = builder.getApellidos();
		this.email = builder.getEmail();
		this.password = builder.getPassword();
		this.centro = builder.getCentro();
		this.rol = builder.getRol().toLowerCase();
	}
	
	public Usuario() {}
	
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
		
		if (rol.equalsIgnoreCase("Administrador") || rol.equalsIgnoreCase("Sanitario") || rol.equalsIgnoreCase("Paciente")) 
			validez = true;
		
		return validez;
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
			builder.append(this.email.substring(0, this.email.indexOf("@")));
			if(builder.toString().length() < 16)
				while(builder.toString().length() < 16) builder.append('a');
			builder.setLength(16);
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
		String nonsense = this.dni.substring(1);
		String decyph;
		
		byte[] bytes = Base64.getDecoder().decode(nonsense.getBytes());
		
		try {
			StringBuilder builder = new StringBuilder();
			builder.append(this.email.substring(0, this.email.indexOf("@")));
			if(builder.toString().length() < 16)
				while(builder.toString().length() < 16) builder.append('a');
			builder.setLength(16);
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
		
		this.dniDenc = decyph;
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
	
	public boolean isPrimeraDosis() {
		return primeraDosis;
	}

	public boolean isSegundaDosis() {
		return segundaDosis;
	}

	public String getEmail() {
		return email;
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

	public String getPassword() {
		return password;
	}

	public String getCentro() {
		return centro;
	}

	public String getRol() {
		return rol;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPrimeraDosis(boolean primeraDosis) {
		this.primeraDosis = primeraDosis;
	}

	public void setSegundaDosis(boolean segundaDosis) {
		this.segundaDosis = segundaDosis;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getDniDenc() {
		return dniDenc;
	}



	public void setDni(String dni) {
		this.dni = dni;
	}
	
}