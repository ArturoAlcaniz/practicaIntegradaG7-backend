package com.practicaintegradag7.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
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
	
	@ManyToOne
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
		
		if (!validatePassword(password)) {
			throw new IllegalArgumentException("Password is not valid!");
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
		Pattern regexEmail = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher compareEmail = regexEmail.matcher(email);
		return compareEmail.matches();
	}
	
	private boolean validatePassword(String password) {
		return password.length()>4;
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
}
