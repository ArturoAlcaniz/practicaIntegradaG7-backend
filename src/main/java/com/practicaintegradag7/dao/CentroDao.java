package com.practicaintegradag7.dao;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.repos.CentroRepository;

@Service
public class CentroDao {

	@Autowired
	public CentroRepository centroRepository;
	
	public Centro createCentro(Centro centro) {
		return centroRepository.insert(centro);
	}
	
	public Centro buscarCentro(String id) throws CentroNotFoundException, NoSuchElementException{
		return centroRepository.findById(id).get();
	}
	
	public Optional<Centro> getCitasByDni(String id) {
		return centroRepository.findById(id);
	}
	
	public List<Centro> getAllCitas() {
		return centroRepository.findAll();
	}
	
	public void addVacunas(String centro, int amount) throws CentroNotFoundException{
		Optional<Centro> og = centroRepository.findById(centro);
		if(og.isPresent()) {
			Centro c = og.get();
			c.setVacunas(c.getVacunasDisponibles() + amount);
			centroRepository.save(c);
		} else throw new CentroNotFoundException("Centro "+centro+" no encontrado");
	}
}