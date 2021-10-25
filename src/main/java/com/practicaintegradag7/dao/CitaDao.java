package com.practicaintegradag7.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CitaExceptions;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.repos.CitaRepository;

@Service
public class CitaDao {
	
	@Autowired
	public CitaRepository citaRepository;
	
	public Cita createCita(Cita cita) {
		checkCitasLimit(cita);
		return citaRepository.insert(cita);
	}
	
	public List<Cita> getCitasByDni(String dni) {
		return citaRepository.findByDni(dni);
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	
	private void checkCitasLimit(Cita cita) {
		if(getCitasByDni(cita.getDni()).size() > 1) {
			throw new CitaExceptions().dniAlreadyExist;
		}
	}
}
