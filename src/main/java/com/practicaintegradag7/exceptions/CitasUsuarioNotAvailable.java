package com.practicaintegradag7.exceptions;

public class CitasUsuarioNotAvailable extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitasUsuarioNotAvailable() {
		this.mssg = "Todos los usuarios tienen el maximo de citas";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}