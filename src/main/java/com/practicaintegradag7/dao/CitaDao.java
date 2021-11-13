package com.practicaintegradag7.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CitaNotModifiedException;
import com.practicaintegradag7.exceptions.CitasCupoNotAvailable;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.model.Centro;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Cupo;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.CitaRepository;

@Service
public class CitaDao {
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private UsuarioDao usuarioDao;
	
	@Autowired
	private CupoDao cupoDao;
	
	@Autowired
	private CentroDao centroDao;
	
	public List<Cita> createCitas() throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CentroNotFoundException, CupoNotFoundException, CupoExistException, CitaNotFoundException {
		Usuario usuario = findUsuarioAvailable();
		List<Cita> citasUsuario = getCitasByEmail(usuario.getEmail());
		List<Cita> citas = new ArrayList<>();

		Centro centro = centroDao.buscarCentroByNombre(usuario.getCentro().getNombre());
		
		if(citasUsuario.size()>1) {
			throw new CitasUsuarioNotAvailable();
		}
		
		if(citasUsuario.size() == 1) {
			LocalDateTime fecha = findFechaAvailableAfter(centro, citasUsuario.get(0).getFecha());
			Cita cita = new Cita(usuario.getEmail(), fecha, usuario.getCentro().getNombre(), (short) 2);
			citas.add(citaRepository.save(cita));
			
			Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(fecha, centro);
			restarCitaCupo(cupo);
			
			return citas;
		}

		LocalDateTime fecha1 = findFechaAvailable(centro);
		LocalDateTime fecha2 = findFechaAvailableAfter(centro, fecha1.plusDays(21));
		Cupo cupo1 = cupoDao.getCupoByInicialDateAndCentro(fecha1, centro);
		Cupo cupo2 = cupoDao.getCupoByInicialDateAndCentro(fecha2, centro);
		String centroNombre = usuario.getCentro().getNombre();
		Cita cita1 = new Cita(usuario.getEmail(), fecha1, centroNombre, (short) 1);
		Cita cita2 = new Cita(usuario.getEmail(), fecha2, centroNombre, (short) 2);
		citaRepository.save(cita1);
		citaRepository.save(cita2);
		citas.add(cita1);
		citas.add(cita2);
		restarCitaCupo(cupo1);
		restarCitaCupo(cupo2);
		return citas;
	}
	
	private LocalDateTime findFechaAvailableAfter(Centro centro, LocalDateTime fecha) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailableAfter(centro, fecha);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
	}
	public List<Cita> getCitasByEmail(String email) throws CitaNotFoundException {
		Optional<List<Cita>> citas = citaRepository.findByEmail(email);
		if (citas.isPresent()) 
			return citas.get();
		else throw new CitaNotFoundException("Este usuario no tiene citas");
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
	}
	
	private Usuario findUsuarioAvailable() throws CitasUsuarioNotAvailable {
		List<Cita> citas = getAllCitas();
		Optional<Usuario> d = usuarioDao.getAllUsuarios().stream().filter(usuario -> 
			citas.stream().filter(cita -> usuario.getEmail().equals(cita.getEmail())).count()<2).findFirst();
		if(!d.isPresent()) {
			throw new CitasUsuarioNotAvailable();
		}
		return d.get();	
	}
	
	private LocalDateTime findFechaAvailable(Centro centro) throws CitasCupoNotAvailable {
		List<Cupo> cupos = cupoDao.getAllCuposAvailable(centro);
		if(cupos.isEmpty()) {
			throw new CitasCupoNotAvailable();
		}
		return cupos.get(0).getFechaInicio();
	}
	
	public void createCita (Cita cita) {
		citaRepository.save(cita);
	}
	
	public void deleteCita (Cita cita) throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro);
		
		if (Short.toUnsignedInt(cita.getNcita())==1) {
			Optional<Cita> opt = citaRepository.findByEmailAndNcita(cita.getEmail(),Short.valueOf("2"));
			if (opt.isPresent()) {
				Cita citaSegunda = opt.get();
				Cita citaSegundaPasaPrimera = new Cita(citaSegunda.getEmail(), citaSegunda.getFecha(), citaSegunda.getCentroNombre(), Short.valueOf("1"));
				deleteCita(citaSegunda);
				saveCita(citaSegundaPasaPrimera);
			}
		}
		
		sumarCitaCupo(cupo);
		citaRepository.deleteByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	public void saveCita(Cita cita) throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro);
		citaRepository.save(cita);
		restarCitaCupo(cupo);
	}

	public void deleteCitaModificar(Cita cita) throws CentroNotFoundException, CupoNotFoundException, CupoExistException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro);
		sumarCitaCupo(cupo);
		citaRepository.deleteByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	
	public Cita findCitaByEmailAndFecha(Cita cita) {
		return citaRepository.findByEmailAndFecha(cita.getEmail(), cita.getFecha());
	}
	
	public Cita findByEmailAndNcita(String email, Short ncita) throws CitaNotFoundException {
		Optional<Cita> opt = citaRepository.findByEmailAndNcita(email, ncita);
		if(opt.isPresent()) return opt.get();
		else throw new CitaNotFoundException("La cita con email "+email+" y ncita "+ncita+" no existe");
		
	}

	public boolean modifyCita(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		
		boolean modified = false;
		
		if (validarModificacion(citaAntigua, citaNueva)) {
		
			Centro centro = centroDao.buscarCentroByNombre(citaAntigua.getCentroNombre());
			Cupo cupoAntiguo = cupoDao.getCupoByInicialDateAndCentro(citaAntigua.getFecha(), centro);
			Cupo cupoNuevo = cupoDao.getCupoByInicialDateAndCentro(citaNueva.getFecha(), centro);
			
			sumarCitaCupo(cupoAntiguo);
			restarCitaCupo(cupoNuevo);
			
			deleteCitaModificar(citaAntigua);
			createCita(citaNueva);
			
			modified = true;
		
		}
		
		return modified;
	}
	
	public void deleteAllCitas() {
		citaRepository.deleteAll();
	}
	
	public void sumarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException, CentroNotFoundException, CupoExistException {
		
		Cupo cupoActualizado = new Cupo(cupoAntiguo.getFechaInicio(), cupoAntiguo.getFechaFin(), cupoAntiguo.getNumeroCitas()+1, cupoAntiguo.getCentro());
		cupoDao.deleteCupo(cupoAntiguo);
		cupoDao.saveCupo(cupoActualizado);
	}
	
	public void restarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException, CentroNotFoundException, CupoExistException {
		
		Cupo cupoActualizado = new Cupo(cupoAntiguo.getFechaInicio(), cupoAntiguo.getFechaFin(), cupoAntiguo.getNumeroCitas()-1, cupoAntiguo.getCentro());
		cupoDao.deleteCupo(cupoAntiguo);
		cupoDao.saveCupo(cupoActualizado);
	}
	
	public boolean validarModificacion(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException {
		
		boolean validado = false;
		
		if (citaAntigua.getFecha().equals(citaNueva.getFecha()))
			throw new CitaNotModifiedException("Debe insertar una fecha distinta a la antigua");
		else if(Short.toUnsignedInt(citaAntigua.getNcita())==1){
			
			Optional<Cita> opt = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("2"));
			
			if (opt.isPresent()) {
				Cita citaSegunda = opt.get();
				if (!citaNueva.getFecha().isBefore(citaSegunda.getFecha()))
					throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior a la segunda ("+citaSegunda.getFecha()+")");	
			}		
			
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 10, 23, 59)))
				throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior al 10-1-2022");
			
			validado = true;
		}
		else if(Short.toUnsignedInt(citaAntigua.getNcita())==2){

			Optional<Cita> opt = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("1"));
			
			if (opt.isPresent()) {
				Cita citaPrimera = opt.get();
				if (!citaNueva.getFecha().isAfter(citaPrimera.getFecha().plusDays(21)))
					throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser anterior a "
							+ "21 dias despues de la primera ("+citaPrimera.getFecha()+")");	
			}
			
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 31, 23, 59)))
				throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser posterior al 31-1-2022");
			
			validado = true;
		
		}
			
		
		return validado;
	}
	
	public List<Cita> findByFechaAndCentroNombre (LocalDateTime fechaMin, LocalDateTime fechaMax, String centro) {
		return citaRepository.findByFechaAndCentroNombre(fechaMin,fechaMax, centro);
	}

	
}
