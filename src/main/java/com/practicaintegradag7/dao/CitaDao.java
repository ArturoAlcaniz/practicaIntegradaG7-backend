package com.practicaintegradag7.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.repos.CitaRepository;

@Service
public class CitaDao {
	
	@Autowired
	public CitaRepository citaRepository;
	
	public void createCita(Cita cita) {
		citaRepository.insert(cita);
	}
	
	public List<Cita> getCitasByDni(String dni) {
		return citaRepository.findByDni(dni);
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}

}
