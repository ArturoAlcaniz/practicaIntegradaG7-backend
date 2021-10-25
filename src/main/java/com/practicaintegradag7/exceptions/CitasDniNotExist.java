package com.practicaintegradag7.exceptions;

public class CitasDniNotExist extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasDniNotExist() {
		this.mssg = "El dni introducido no existe";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}