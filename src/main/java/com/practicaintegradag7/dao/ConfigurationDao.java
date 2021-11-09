package com.practicaintegradag7.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.ConfigurationLimitException;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.repos.ConfigurationRepository;

@Service
public class ConfigurationDao {
	
	@Autowired
	private ConfigurationRepository configurationRepository;
	
	public Configuration save(Configuration configuration) throws ConfigurationLimitException {
		if (!configurationRepository.findAll().isEmpty()) {
			throw new ConfigurationLimitException();
		}
		return configurationRepository.save(configuration);
	}
	
	public Configuration obtenerConfiguration() throws ConfigurationEmptyException {
		List<Configuration> configurations = configurationRepository.findAll();
		if(configurations.isEmpty()) {
			throw new ConfigurationEmptyException();
		}
		return configurationRepository.findAll().get(0);
	}
	
	public void eliminarConfiguration() {
		configurationRepository.deleteAll();
	}
}
