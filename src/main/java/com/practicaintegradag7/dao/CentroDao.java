package com.practicaintegradag7.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroExistException;
import com.practicaintegradag7.exceptions.CentroNotEmptyException;
import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.ConfigurationEmptyException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.VacunasNoValidasException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Configuration;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CentroRepository;

/**
 * 
 * Se encarga de la lógica de los centros en la aplicación
 *
 */
@Service
public class CentroDao {

	@Autowired
	private CentroRepository centroRepository;
	
	@Autowired
	private CupoDao cupoDao;
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private ConfigurationDao configurationDao;
	
	
	/**
	 * Metodo que se encarga de insertar un centro con la configurion existente
	 * @param centro a insertar
	 * @return centro insertado
	 * @throws CentroExistException excepción que se lanzará si el centro ya existe
	 */
	public Centro createCentro(Centro centro) throws CentroExistException {
		existeCentro(centro.getNombre());
		Configuration configuracion;
		try {
			configuracion = configurationDao.obtenerConfiguration();
			cupoDao.autogenerarFranjaParaCentro(configuracion, centro);
			return centroRepository.insert(centro);
		} catch (ConfigurationEmptyException e) {
			return centroRepository.insert(centro);
		}
	}
	
	/**
	 * Metodo encargado de buscar un centro dado a partir de su id.
	 * @param id del centro
	 * @return el centro
	 * @throws CentroNotFoundException excepción que se lanza cuando no se encuentra el centro
	 */
	public Centro buscarCentro(String id) throws CentroNotFoundException{
		Optional<Centro> opt = centroRepository.findById(id);
		if(opt.isPresent()) return opt.get();
		else throw new CentroNotFoundException("El centro con id "+id+" no existe");
	}
	
	/**
	 * Metodo encargado de buscar un centro dado a partir de su nombre.
	 * @param nombre del centro
	 * @return el centro encontrado
	 * @throws CentroNotFoundException excepción que se lanza si no se encuentra el centro
	 */
	public Centro buscarCentroByNombre(String centro) throws CentroNotFoundException {
		Optional<Centro> opt = centroRepository.findByNombre(centro);
		if(opt.isPresent()) return opt.get();
		else throw new CentroNotFoundException("El centro "+centro+" no existe");
	}
	
	/**
	 * Metodo para obtener todos los centros
	 * @return una lista con los centros existentes
	 */
	public List<Centro> getAllCitas() {
		return centroRepository.findAll();
	}
	
	/**
	 * Metodo para comprobar si un centro existe
	 * @param nombre del centro
	 * @throws CentroExistException Excepcion que se lanzará si el centro existe
	 */
	public void existeCentro(String nombre) throws CentroExistException {
		Optional<Centro> opt = centroRepository.findByNombre(nombre);
		if(opt.isPresent()) throw new CentroExistException("El centro que desea guardar ya existe");
	}
	
	/**
	 * Metodo para añadir vacunas a un centro. Se sumaran a las existentes
	 * @param centro el nombre del centro
	 * @param amount la cantidad de vacunas a añadir
	 * @throws CentroNotFoundException Excepción que se lanzará si no se encuentra el centro
	 * @throws VacunasNoValidasException Excepción que se lanzará si las vacunas introducidas es un numero negativo
	 */
	public void addVacunas(String centro, int amount) throws CentroNotFoundException, VacunasNoValidasException{
		if (amount < 0) throw new VacunasNoValidasException("El numero de vacunas a anadir debe ser mayor que 0");
		Optional<Centro> og = centroRepository.findById(centro);
		if(og.isPresent()) {
			Centro c = og.get();
			c.setVacunas(c.getVacunas() + amount);
			centroRepository.save(c);
		} else throw new CentroNotFoundException("Centro "+centro+" no encontrado");
	}
	
	/**
	 * Metodo para eliminar un centro
	 * @param c el nombre del centro
	 * @throws CentroNotFoundException Excepción que se lanzará si no se encuentra un centro
	 */
	public void deleteCentro(Centro c) throws CentroNotFoundException {
		Optional<Centro> opt = centroRepository.findByNombre(c.getNombre());
		if(opt.isPresent()) centroRepository.deleteByNombre(c.getNombre());
		else throw new CentroNotFoundException("Centro no encontrado");
	}

	/**
	 * Metodo para eliminar todos los centros de la base de datos
	 */
	public void deleteAllCentros() {
		centroRepository.deleteAll();
	}

	/**
	 * Metodo para eliminar un centro que no contenga usuarios (forma que utilizarán los usuarios de la aplicación)
	 * @param nombreCentro el nombre del centro a eliminar
	 * @throws CentroNotEmptyException Excepción que se lanzará si el centro contiene usuarios
	 * @throws CupoNotFoundException Excepción que se lanzará si algún cupo del centro no se encuentra
	 * @throws CentroNotFoundException Excepción que se lanzará si el centro no se encuentra
	 */
	public void deleteCentroWithNoUsers(String nombreCentro) throws CentroNotEmptyException, CupoNotFoundException, CentroNotFoundException {

		List <Usuario> usuarios = usuarioDao.getAllUsuariosByCentro(nombreCentro);
		
		if (usuarios.isEmpty()) {
			cupoDao.deleteAllCuposByCentro(nombreCentro);
			centroRepository.deleteByNombre(nombreCentro);

		}else throw new CentroNotEmptyException("El centro "+nombreCentro+" no puede ser eliminado porque contiene "+
			usuarios.size()+" usuario(s).");
		
	}
	
	/**
	 * Metodo para modificar los datos de un centro
	 * @param nombre El nombre del centro
	 * @param direccion La nueva direccion del centro
	 * @param vacunas Las nuevas vacunas del centro
	 * @return el centro modificado
	 * @throws CentroExistException Excepción que se lanzará cuando el centro a insertar ya exista
	 * @throws CentroNotFoundException Excepción que se lanzará si no se encuentra el centro a modificar
	 */
	public Centro modificarCentro(String nombre, String direccion, int vacunas) throws CentroExistException, CentroNotFoundException {
		if(direccion.equals("")) throw new CentroNotFoundException("La direccion no puede estar en blanco");
		Centro centroOld;
		centroOld = buscarCentroByNombre(nombre);
		Centro centroNew = new Centro(nombre, direccion, vacunas);
		deleteCentro(centroOld);
		return createCentro(centroNew);
	}
	
	/**
	 * Metodo para crear un centro sin generar sus cupos
	 * @param centro el centro a crear (objeto)
	 * @return el centro creado
	 */
	public Centro save(Centro centro) {
		return centroRepository.save(centro);
	}
	
	/**
	 * Metodo para guardar una lista de centros
	 * @param centros los centros a guardar
	 */
	public void saveAll(List<Centro> centros) {
		centroRepository.saveAll(centros);
	}
	
}