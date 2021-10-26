package com.practicaintegradag7.exceptions;

public class CupoNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;
	private final String mssg;

	public CupoNotFoundException(String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
}
