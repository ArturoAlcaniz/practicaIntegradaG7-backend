package com.practicaintegradag7.exceptions;

public class CitasDniNotValid extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasDniNotValid() {
		this.mssg = "Dni no es valido!";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}