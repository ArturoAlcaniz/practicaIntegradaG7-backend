package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.repos.CentroRepository;
import com.practicaintegradag7.repos.CupoRepository;

@Component
public class CupoDao {

	@Autowired
	public CupoRepository cupoRepository;
	@Autowired
	public CentroRepository centroRepository;
	
	public Cupo saveCupo(Cupo cupo) throws CentroNotFoundException, CupoExistException {
		Optional<Centro> opt = centroRepository.findByNombre(cupo.getCentro().getNombre());
		if (opt.isPresent()) {
			Optional<Cupo> cupoExistente = cupoRepository.findByFechaInicioAndCentro(cupo.getFechaInicio(), cupo.getCentro());
			if (!cupoExistente.isPresent()) {
				return cupoRepository.save(cupo);
			} else throw new CupoExistException("El cupo que intentas crear ya existe");
		} else throw new CentroNotFoundException("El centro "+cupo.getCentro().getNombre()+"no existe.");
	}
	
	public Cupo getCupoById (String id) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo "+id+" no existe!");
	}
	
	public Cupo getCupoByInicialDateAndCentro (LocalDateTime fechaInicio, Centro centro) throws CupoNotFoundException {
		Optional<Cupo> opt = cupoRepository.findByFechaInicioAndCentro(fechaInicio, centro);
		if(opt.isPresent()) return opt.get();
		else throw new CupoNotFoundException("El cupo del centro "+centro.getNombre()+" con fecha "+fechaInicio+" no existe!!");
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
