package com.practicaintegradag7.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.validator.EmailValidator;
import org.springframework.data.mongodb.core.mapping.Document;

import com.practicaintegradag7.exceptions.CifradoContrasenaException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

@Document(collection = "Usuario")
public class Usuario {

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
	
	public Usuario(String dni, String nombre, String apellidos, String email, String password, Centro centro,
			String rol) {
    	if(!validateDNI(dni)) {
    		throw new IllegalArgumentException("Dni is not valid!");
    	}

		if (!validateEmail(email)) {
			throw new IllegalArgumentException("Email is not valid!");
		}
		
		if (!validateRol(rol.toLowerCase())) {
			throw new IllegalArgumentException("Rol is not valid!");
		}
		
		if (rol.equalsIgnoreCase("paciente")) {
			this.primeraDosis = false;
			this.segundaDosis = false;
		} else {
			this.primeraDosis = true;
			this.segundaDosis = true;
		}
		
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.password = password;
		this.centro = centro;
		this.rol = rol.toLowerCase();
		
	}

    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
	
    private boolean validateEmail(String email) {
		EmailValidator validator = EmailValidator.getInstance();
		return validator.isValid(email);
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

	public String getPassword() {
		return password;
	}

	public Centro getCentro() {
		return centro;
	}

	public String getRol() {
		return rol;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Encrypts own password via Cesar Cypher(3), then B64 encoding.
	 * Appends a 'd' to the beggining after the procedure, to indiciate that this string is encoded.
	 */
	public void encryptPassword() throws CifradoContrasenaException {
		String cyph = cypher(this.dni, 3);
		byte[] encrypted = new byte[0];
		
		try {
			Key aesKey = new SecretKeySpec(this.nombre.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			encrypted = cipher.doFinal(cyph.getBytes());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			throw new CifradoContrasenaException(e.getMessage());
		}
		
		StringBuilder sb = new StringBuilder();
		for (byte b: encrypted) {
		    sb.append((char)b);
		}
		
		this.dni = sb.toString();
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
	
	public void decryptPassword() throws CifradoContrasenaException {
		String pwd = this.dni;
		Cipher cipher;
		Key aesKey;
		String decyph;
		
		byte[] bytes = new byte[pwd.length()];
		for (int i=0; i<pwd.length(); i++) {
			bytes[i] = (byte) pwd.charAt(i);
		}
		
		try {
			aesKey = new SecretKeySpec(this.nombre.getBytes(), "AES");
			cipher = Cipher.getInstance("AES/GCM/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			decyph = new String(cipher.doFinal(bytes));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
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