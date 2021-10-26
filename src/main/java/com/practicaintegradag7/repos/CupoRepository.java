package com.practicaintegradag7.repos;

import java.io.Serializable;
import java.time.LocalDateTime;
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
	
	//public Optional<Cupo> findByInitialDateAndCentro(LocalDateTime fechaInicio, Centro centro);
	
	/*public Optional<Cupo> findById(String cupo);
	public void updateById(String cupo);
	public void deleteById(String cupo);*/
	
}
