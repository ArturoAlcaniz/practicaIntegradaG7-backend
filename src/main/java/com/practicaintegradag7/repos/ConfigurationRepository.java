package com.practicaintegradag7.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Configuration;

@Repository
@CrossOrigin(value = {})

public interface ConfigurationRepository extends MongoRepository<Configuration, String> {
}
