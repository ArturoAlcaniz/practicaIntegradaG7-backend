package com.practicaintegradag7.model;

public class UsuarioBuilder {

	private String dni;
	private String nombre;
	private String apellidos;
	private String email;
	private boolean primeraDosis;
	private boolean segundaDosis;
	private String password;
	private Centro centro;
	private String rol;

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

	public UsuarioBuilder dni(String dni) {
		this.dni = dni;
		return this;
	}
	
	public UsuarioBuilder nombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	
	public UsuarioBuilder apellidos(String apellidos) {
		this.apellidos = apellidos;
		return this;
	}
	
	public UsuarioBuilder email(String email) {
		this.email = email;
		return this;
	}
	
	public UsuarioBuilder password(String password) {
		this.password = password;
		return this;
	}
	
	public UsuarioBuilder centro(Centro centro) {
		this.centro = centro;
		return this;
	}
	
	public UsuarioBuilder rol(String rol) {
		this.rol = rol;
		return this;
	}
	
    public Usuario build() {
        return new Usuario(this);
    }	
}
