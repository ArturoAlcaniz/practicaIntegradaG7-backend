package com.practicaintegradag7.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Cita {
    
    @Id
    @Column(name = "dni")
    private String dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToOne
    private Centro centro;

    public Cita(String dni, String nombre, String apellidos, String direccion, String email, String password) {
    	this.dni = dni;
    	this.nombre = nombre;
    	this.apellidos = apellidos;
    	this.direccion = direccion;
    	this.email = email;
    	this.password = password;
    }
    
    public Centro getCentro() {
    	return centro;
    }
}