package com.practicaintegradag7.repos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;

@Repository
@CrossOrigin(value = {})
public interface CitaRepository extends MongoRepository<Cita, String> {
	
	public List<Cita> findByDni(String cita);
	
	//@Query("{'dni': ?0, 'fecha': ?1}")
	public void deleteByDniAndFecha(String dni, LocalDateTime fecha);
	
	Cita findByDniAndFecha(String dni, LocalDateTime fecha);
	

	
	
}