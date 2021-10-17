package com.practicaintegradag7.repos;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.practicaintegradag7.model.Cita;

@ApplicationScoped
public class CitaRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Cita create(Cita cita) {
		if (cita.getCentro() != null ) {
			cita.getCentro().getCentroCitas().add(cita);
		}

		entityManager.persist(cita);

		return cita;
	}
}
