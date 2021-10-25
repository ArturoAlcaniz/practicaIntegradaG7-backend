package com.practicaintegradag7.repos;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Centro;

@Repository
@CrossOrigin(value = {})
public interface CentroRepository extends MongoRepository<Centro, Serializable>{
	Optional<Centro> findByNombre(String centro);
}