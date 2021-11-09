package com.practicaintegradag7.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.repos.CentroRepository;

@Service
public class CentroDao {

	@Autowired
	private CentroRepository centroRepository;
	
	public Centro createCentro(Centro centro) throws CentroExistException{
		existeCentro(centro.getNombre());
		return centroRepository.insert(centro);
	}
	
	
	public Centro buscarCentro(String id) throws CentroNotFoundException{
		Optional<Centro> opt = centroRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CentroNotFoundException("El centro "+id+" no existe");
	}
	
	public Centro buscarCentroByNombre(String centro) throws CentroNotFoundException {
		Optional<Centro> opt = centroRepository.findByNombre(centro);
		if(opt.isPresent()) return opt.get();
		else throw new CentroNotFoundException("El centro "+centro+" no existe");
	}
	
	public List<Centro> getAllCitas() {
		return centroRepository.findAll();
	}
	
	public void existeCentro(String nombre) throws CentroExistException {
		Optional<Centro> opt = centroRepository.findByNombre(nombre);
		if(opt.isPresent()) throw new CentroExistException("El centro que desea guardar ya existe.");	
	}
	
	public void addVacunas(String centro, int amount) throws CentroNotFoundException, VacunasNoValidasException{
		if (amount < 0) throw new VacunasNoValidasException("El numero de vacunas a anadir debe ser mayor que 0");
		Optional<Centro> og = centroRepository.findById(centro);
		if(og.isPresent()) {
			Centro c = og.get();
			c.setVacunas(c.getVacunas() + amount);
			centroRepository.save(c);
		} else throw new CentroNotFoundException("Centro "+centro+" no encontrado");
	}
	
	public void deleteCentro(Centro c) throws CentroNotFoundException {
		Optional<Centro> opt = centroRepository.findByNombre(c.getNombre());
		if(opt.isPresent()) centroRepository.delete(c);
		else throw new CentroNotFoundException("Centro no encontrado");
	}
	
}