package com.practicaintegradag7.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.ConfigurationCitasFranjaException;
import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.repos.ConfigurationRepository;

@Service
public class ConfigurationDao {
	
	@Autowired
	private ConfigurationRepository configurationRepository;
	
	/**
	 * Comprueba que no haya ninguna configuracion guardada y que las citas y las franjas tengan un valor valido,
	 * y guarda la configuracion
	 * @param configuration
	 * La configuracion a guardar
	 * @return La configuracion de los cupos guardada, o una excepcion
	 * @throws ConfigurationLimitException
	 * Si ya hay una guardada
	 * @throws ConfigurationCitasFranjaException
	 * Si las citas por franja son no validas (0)
	 */
	public Configuration save(Configuration configuration) throws ConfigurationLimitException, ConfigurationCitasFranjaException {
		if (!configurationRepository.findAll().isEmpty()) {
			throw new ConfigurationLimitException();
		}
		if (configuration.getCitasPorFranja() == 0 || configuration.getFranjasPorDia() == 0) {
			throw new ConfigurationCitasFranjaException();
		}
		return configurationRepository.save(configuration);
	}
	
	/**
	 * Comprueba si hay una configuracion y la devuelve
	 * @return La configuracion de los cupos de la base de datos
	 * @throws ConfigurationEmptyException
	 * Si no hay ninguna configuracion guardada
	 */
	public Configuration obtenerConfiguration() throws ConfigurationEmptyException {
		List<Configuration> configurations = configurationRepository.findAll();
		if(configurations.isEmpty()) {
			throw new ConfigurationEmptyException();
		}
		return configurationRepository.findAll().get(0);
	}
	
	/**
	 * Elimina la configuracion de los cupos de la base de datos
	 */
	public void eliminarConfiguration() {
		configurationRepository.deleteAll();
	}
}
