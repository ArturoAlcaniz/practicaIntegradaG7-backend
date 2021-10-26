package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.repos.CupoRepository;

@Component
public class CupoDao {

	@Autowired
	public CupoRepository cupoRepository;
	
	public Cupo saveCupo(Cupo cupo) {
		return cupoRepository.save(cupo);
	}
	
	public Cupo getCupoById (String id) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+id+" no existe");
	}
	
	//comprobar solo puede haber una fecha de inicio con un centro
	
	public Cupo getCupoByInicialDate (LocalDateTime fechaInicio) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(fechaInicio);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+fechaInicio+" no existe");
	}
	
	public List<Cupo> getAllCupos() {
		return cupoRepository.findAll();
	}
	
	public void updateCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) cupoRepository.save(cupo);
		else throw new CupoNotFoundException("Cupo para modificar no encontrado");
	}
	
	public void deleteCupo (Cupo cupo) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(cupo.id());
		if(opt.isPresent()) cupoRepository.delete(cupo);
		else throw new CupoNotFoundException("Cupo para borrar no encontrado");
	}
}
