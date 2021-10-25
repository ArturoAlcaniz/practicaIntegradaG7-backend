package com.practicaintegradag7.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Centro")
public class Centro implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
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
	
	public Centro(String nombre, String direccion, int vacunasDisponibles) {
		
		if(vacunasDisponibles < 0) {
			throw new IllegalArgumentException("vacunas is not valid");
		}

		this.nombre = nombre;
		this.direccion = direccion;
		this.vacunasDisponibles = vacunasDisponibles;
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
