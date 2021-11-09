package com.practicaintegradag7.repos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cupo;


@Repository
public interface CupoRepository extends MongoRepository<Cupo, Serializable> {
	@Query("{ 'id': ?0}")
	Optional<Cupo> findById(String cupo);
	
	@Query("{'fechaInicio': ?0, 'centro': ?1}")
	Optional<Cupo> findByFechaInicioAndCentro(LocalDateTime fechaInicio, Centro centro);
	
	@Query("{'numeroCitas':{'$gt':?0}, 'centro': ?1}")
	List<Cupo> findCuposWithCitasMoreThan(int numeroCitas, Centro centro);

	@Query("{'numeroCitas':{'$gt':?0}, 'centro': ?1, 'fechaInicio':{'$gt':?2}}")
	List<Cupo> findCuposWithCitasMoreThan(int numeroCitas, Centro centro, LocalDateTime fechaMinima);
}
