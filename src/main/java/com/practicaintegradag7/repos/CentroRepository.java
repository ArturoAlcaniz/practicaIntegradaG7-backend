package com.practicaintegradag7.repos;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.practicaintegradag7.model.Centro;

@Repository
public interface CentroRepository extends MongoRepository<Centro, Serializable>{
	@Query("{ 'nombre': ?0}")
	Optional<Centro> findByNombre(String centro);
	public void deleteByNombre(String nombre);
}