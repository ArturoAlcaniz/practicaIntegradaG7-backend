package com.practicaintegradag7.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.practicaintegradag7.model.Usuario;

@Repository
@CrossOrigin(value = {})

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
	
	public Optional<Usuario> findByEmail(String email);
	public void deleteByEmail(String email);
	public boolean existsByEmail(String email);
	
}