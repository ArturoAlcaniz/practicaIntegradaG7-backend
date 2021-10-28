package com.practicaintegradag7.exceptions;

public class CifradoContrasenaException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CifradoContrasenaException(String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}
