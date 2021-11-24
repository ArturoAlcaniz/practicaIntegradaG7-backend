package com.practicaintegradag7.exceptions;

public class UsuarioVacunadoException  extends Exception{
	
	private static final long serialVersionUID = 4L;
	private final String mssg;

	public UsuarioVacunadoException () {
		this.mssg = "El usuario que intentas eliminar ya se encuentra vacunado";
	}
	
	@Override
	public String getMessage() {
		return mssg;
	}
	
}
