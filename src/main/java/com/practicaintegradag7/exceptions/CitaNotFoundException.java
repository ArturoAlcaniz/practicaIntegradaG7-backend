package com.practicaintegradag7.exceptions;

public class CitaNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CitaNotFoundException(String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}
