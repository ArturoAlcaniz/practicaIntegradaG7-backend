package com.practicaintegradag7.exceptions;

public class UsuarioNotFoundException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public UsuarioNotFoundException (String mssg) {
		this.mssg = mssg;
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
