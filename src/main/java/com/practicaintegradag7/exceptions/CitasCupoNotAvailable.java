package com.practicaintegradag7.exceptions;

public class CitasCupoNotAvailable extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasCupoNotAvailable() {
		this.mssg = "No hay cupos disponibles";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}