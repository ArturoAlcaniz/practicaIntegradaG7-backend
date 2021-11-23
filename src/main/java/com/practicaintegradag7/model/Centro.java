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
	
	@Column(name = "nombre", unique=true)
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
	private transient Set<Cita> centroCitas = new HashSet<>();
	
	public Centro(String nombre, String direccion, int vacunasDisponibles) {
		
		if(vacunasDisponibles < 0) {
			throw new IllegalArgumentException("El numero de vacunas debe ser mayor o igual a 0");
		}

		this.nombre = nombre;
		this.direccion = direccion;
		this.vacunasDisponibles = vacunasDisponibles;
	}

	public String getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public int getVacunas() {
		return vacunasDisponibles;
	}

	public Set<Cita> getCentroCitas() {
		return centroCitas;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setVacunas(int vacunasDisponibles) {
		this.vacunasDisponibles = vacunasDisponibles;
	}

	public void setCentroCitas(Set<Cita> centroCitas) {
		this.centroCitas = centroCitas;
	}
	
}
