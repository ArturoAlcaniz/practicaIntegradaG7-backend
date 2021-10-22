package com.practicaintegradag7.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.repos.CentroRepository;

@Service
public class CentroDao {

	@Autowired
	public CentroRepository centroRepository;
	
	public void createCita(Centro centro) {
		centroRepository.insert(centro);
	}
	
	public Optional<Centro> getCitasByDni(String id) {
		return centroRepository.findById(id);
	}
	
	public List<Centro> getAllCitas() {
		return centroRepository.findAll();
	}

}
