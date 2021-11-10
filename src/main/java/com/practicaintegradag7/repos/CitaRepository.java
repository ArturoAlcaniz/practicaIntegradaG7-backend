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
	
	public List<Cita> findByEmail(String email);
	
	//@Query("{'dni': ?0, 'fecha': ?1}")
	public void deleteByEmailAndFecha(String email, LocalDateTime fecha);
	
	Cita findByEmailAndFecha(String email, LocalDateTime fecha);
	
	public void deleteByEmail(String email);
	
	public List<Cita> findByFechaAndCentroNombre(LocalDateTime fecha, String centroNombre);

	public Optional<Cita> findByEmailAndNcita(String email, Short ncita);
}