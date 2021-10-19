package com.practicaintegradag7.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Centro {
	
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;
	
	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "direccion")
	private String direccion;
	
	@Column(name = "vacunasDisponibles")
	private int vacunasDisponibles;
	
	@OneToMany(
			mappedBy = "centro",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			fetch = FetchType.EAGER
	)
	private Set<Cita> centroCitas = new HashSet<>();
	
	public Centro(String nombre, String direccion, int vacunas) {
		
		if(vacunas < 0) {
			throw new IllegalArgumentException("vacunas is not valid");
		}

		this.nombre = nombre;
		this.direccion = direccion;
		this.vacunasDisponibles = vacunas;
	}
	
	public String getNombre() {
		return nombre;
	}

	public String getDireccion() {
		return direccion;
	}
	
	public int getVacunasDisponibles() {
		return vacunasDisponibles;
	}
	
	public Set<Cita> getCentroCitas() {
		return centroCitas;
	}
}
