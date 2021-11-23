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
import com.practicaintegradag7.exceptions.CitasNotAvailableException;
import com.practicaintegradag7.exceptions.CitasUsuarioNotAvailable;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.VacunacionDateException;
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
	
	public List<Cita> createCitas(Usuario u) throws CitasUsuarioNotAvailable, CitasCupoNotAvailable, CentroNotFoundException, CupoNotFoundException, CitaNotFoundException {
		List<Cita> citasUsuario = getCitasByEmail(u.getEmail());
		List<Cita> citas = new ArrayList<>();

		Centro centro = centroDao.buscarCentroByNombre(u.getCentro());
		
		if(citasUsuario.size()>1) throw new CitasUsuarioNotAvailable();
		
		if(citasUsuario.size() == 1) {
			LocalDateTime fecha = findFechaAvailableAfter(centro, citasUsuario.get(0).getFecha().plusDays(21));
			Cita cita = new Cita(u.getEmail(), fecha, u.getCentro(), (short) 2);
			citas.add(citaRepository.save(cita));
			
			Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(fecha, centro.getNombre());
			restarCitaCupo(cupo);
		} else {
			LocalDateTime fecha1 = findFechaAvailable(centro);
			LocalDateTime fecha2 = findFechaAvailableAfter(centro, fecha1.plusDays(21));
			Cupo cupo1 = cupoDao.getCupoByInicialDateAndCentro(fecha1, centro.getNombre());
			Cupo cupo2 = cupoDao.getCupoByInicialDateAndCentro(fecha2, centro.getNombre());
			String centroNombre = u.getCentro();
			Cita cita1 = new Cita(u.getEmail(), fecha1, centroNombre, (short) 1);
			Cita cita2 = new Cita(u.getEmail(), fecha2, centroNombre, (short) 2);
			citaRepository.save(cita1);
			citaRepository.save(cita2);
			citas.add(cita1);
			citas.add(cita2);
			restarCitaCupo(cupo1);
			restarCitaCupo(cupo2);
		}
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
		if (citas.isPresent()) return citas.get();
		else throw new CitaNotFoundException("Este usuario no tiene citas");
	}
	
	public List<Cita> getAllCitas() {
		return citaRepository.findAll();
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
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
		
		if (cita.getNcita() == 1) {
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
	
	public void deleteCitaUsada (Cita cita) {
		citaRepository.deleteByEmailAndFechaAndNcita(cita.getEmail(), cita.getFecha(), cita.getNcita());
	}
	
	public void saveCita(Cita cita) throws CentroNotFoundException, CupoNotFoundException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
		citaRepository.save(cita);
		restarCitaCupo(cupo);
	}

	public void deleteCitaModificar(Cita cita) throws CentroNotFoundException, CupoNotFoundException {
		Centro centro = centroDao.buscarCentroByNombre(cita.getCentroNombre());
		Cupo cupo = cupoDao.getCupoByInicialDateAndCentro(cita.getFecha(), centro.getNombre());
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

	public boolean modifyCita(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException, CentroNotFoundException, CupoNotFoundException {
		
		boolean modified = false;
		
		if (validarModificacion(citaAntigua, citaNueva)) {
		
			Centro centro = centroDao.buscarCentroByNombre(citaAntigua.getCentroNombre());
			Cupo cupoAntiguo = cupoDao.getCupoByInicialDateAndCentro(citaAntigua.getFecha(), centro.getNombre());
			Cupo cupoNuevo = cupoDao.getCupoByInicialDateAndCentro(citaNueva.getFecha(), centro.getNombre());
			
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
	
	public void sumarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException {
		cupoAntiguo.setCitas(cupoAntiguo.getNumeroCitas()+1);
		cupoDao.updateCupo(cupoAntiguo);
	}
	
	public void restarCitaCupo(Cupo cupoAntiguo) throws CupoNotFoundException {
		cupoAntiguo.setCitas(cupoAntiguo.getNumeroCitas()-1);
		cupoDao.updateCupo(cupoAntiguo);
	}
	
	public boolean validarModificacion(Cita citaAntigua, Cita citaNueva) throws CitaNotModifiedException {
		
		if (citaAntigua.getFecha().equals(citaNueva.getFecha())) {
			throw new CitaNotModifiedException("Debe insertar una fecha distinta a la antigua");
		}
		
		Optional<Cita> opt = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("2"));
		
		if (citaAntigua.getNcita() == 1) {
			if(opt.isPresent() && !citaNueva.getFecha().isBefore(opt.get().getFecha())) {
				throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior a la segunda ("+opt.get().getFecha()+")");
			}
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 10, 23, 59))) {
				throw new CitaNotModifiedException("La fecha de la primera cita no puede ser posterior al 10-1-2022");
			}
			return true;
		}
		
		Optional<Cita> opt2 = citaRepository.findByEmailAndNcita(citaAntigua.getEmail(), Short.parseShort("1"));

		if(citaAntigua.getNcita()==2){
			if(opt2.isPresent() && !citaNueva.getFecha().isAfter(opt2.get().getFecha().plusDays(21))) {
				throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser anterior a "
						+ "21 dias despues de la primera ("+opt2.get().getFecha()+")");
			}
			if (citaNueva.getFecha().isAfter(LocalDateTime.of(2022, 1, 31, 23, 59))) {
				throw new CitaNotModifiedException("La fecha de la segunda cita no puede ser posterior al 31-1-2022");
			}
		}
			
		return true;
	}

	public void vacunar(Cita cita) throws VacunacionDateException, UsuarioNotFoundException, CentroNotFoundException, CitasNotAvailableException {
		LocalDateTime fechaActual = LocalDateTime.now();
		if(fechaActual.getYear() != cita.getFecha().getYear() || fechaActual.getMonth() != cita.getFecha().getMonth()
			|| fechaActual.getDayOfMonth() != cita.getFecha().getDayOfMonth()) {
			throw new VacunacionDateException();
		}
		Usuario usuarioVacunar = usuarioDao.getUsuarioByEmail(cita.getEmail());
		Centro centro = centroDao.buscarCentroByNombre(usuarioVacunar.getCentro());
		if(centro.getVacunas() == 0) {
			throw new CitasNotAvailableException();
		}
		
		if(cita.getNcita() == 1) {
			usuarioVacunar.setPrimeraDosis(true);
		} else {
			usuarioVacunar.setSegundaDosis(true);
		}
		usuarioDao.save(usuarioVacunar);
		this.deleteCitaUsada(cita);
	}
	
	public List<Cita> findByFechaAndCentroNombre (LocalDateTime fechaMin, LocalDateTime fechaMax, String centro) {
		return citaRepository.findByFechaAndCentroNombre(fechaMin,fechaMax, centro);
	}
}
