package com.practicaintegradag7.repos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Cita;

@Repository
@CrossOrigin(value = {})
public interface CitaRepository extends MongoRepository<Cita, String> {
	
	public List<Cita> findByDni(String cita);
	
	public void deleteByDni(String cita);
	
	public List<Cita> findByFechaAndCentroNombre(LocalDateTime fecha, String centroNombre);
}