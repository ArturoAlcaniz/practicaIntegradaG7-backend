package com.practicaintegradag7.repos;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Cita;

@Repository
@CrossOrigin(value = {})
public interface CitaRepository extends MongoRepository<Cita, Serializable> {
}