package com.practicaintegradag7.validate;

import javax.ws.rs.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.dao.CitaDao;
import com.practicaintegradag7.model.Cita;

@Service
public class CitaValidator {
	
	@Autowired
	private CitaDao citaDao;
	
	public void createCitaValidation(Cita cita) throws BadRequestException {
		
		if(citaDao.getCitasByDni(cita.getDni()).size() > 1) {
			throw new BadRequestException("Ya existen citas con este dni");
		}
				
	}
	
}
