package com.practicaintegradag7.exceptions;

public class VacunacionDateException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public VacunacionDateException() {
		this.mssg = "La fecha actual no coincide con la fecha de la cita";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}