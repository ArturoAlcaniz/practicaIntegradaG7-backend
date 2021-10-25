package com.practicaintegradag7.exceptions;

import javax.ws.rs.ClientErrorException;

import org.springframework.stereotype.Service;

@Service
public class CitaExceptions {
	
	public final ClientErrorException dniAlreadyExist;
	public final ClientErrorException dniWrongFormat;
	
	public CitaExceptions() {
		this.dniAlreadyExist = new ClientErrorException("Ya existen citas con este dni", 400);
		this.dniWrongFormat = new ClientErrorException("Dni no es valido!", 400);
	}

}
