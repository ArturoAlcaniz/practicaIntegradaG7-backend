package com.practicaintegradag7.model;

public class CupoFormatted {
	private String id;
	
	private String fechaInicio;
	
	private String fechaFin;
	
	private int numeroCitas;
	
	private Centro centro;

	public CupoFormatted(String id, String fechaInicio, String fechaFin, int numeroCitas, Centro centro) {
		this.id = id;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.numeroCitas = numeroCitas;
		this.centro = centro;
	}

	public String getId() {
		return id;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public int getNumeroCitas() {
		return numeroCitas;
	}

	public Centro getCentro() {
		return centro;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public void setNumeroCitas(int numeroCitas) {
		this.numeroCitas = numeroCitas;
	}

	public void setCentro(Centro centro) {
		this.centro = centro;
	}

}
