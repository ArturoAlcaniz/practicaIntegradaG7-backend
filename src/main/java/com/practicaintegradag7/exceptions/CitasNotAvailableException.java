package com.practicaintegradag7.exceptions;

public class CitasNotAvailableException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasNotAvailableException() {
		this.mssg = "No hay dosis disponibles";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}