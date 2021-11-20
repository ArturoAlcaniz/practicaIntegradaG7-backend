package com.practicaintegradag7.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practicaintegradag7.exceptions.CentroNotFoundException;
import com.practicaintegradag7.exceptions.CifradoContrasenaException;
import com.practicaintegradag7.exceptions.UserModificationException;
import com.practicaintegradag7.exceptions.CitaNotFoundException;
import com.practicaintegradag7.exceptions.CupoExistException;
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;

@Service
public class UsuarioDao {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CitaDao citaDao;
	
	private String wrongContraMssg = "Contrasena no valida, use como minimo 9 caracteres, mayusculas y minusculas, y al menos un numero y un simbolo";
	
	public Usuario saveUsuario(Usuario usuario) throws CifradoContrasenaException {
		
		if (!validatePasswordPolicy(usuario.getPassword())) {
			throw new IllegalArgumentException(wrongContraMssg);
		}
		if(!validateDNI(usuario.getDni())) {
			throw new IllegalArgumentException("Dni no valido, 8 numeros y letra mayuscula");
		}
		usuario.encryptDNI();
		if (usuarioRepository.existsByEmail(usuario.getEmail()))
			return null;
		else
		{
			usuario.hashPassword();
			return usuarioRepository.save(usuario);
		}
	}
	public void deleteAllUsuarios() {
		usuarioRepository.deleteAll();
	}
	
	public Usuario getUsuarioByEmail(String email) throws UsuarioNotFoundException {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		if (usuario.isPresent()) {
			return usuario.get();
		}else throw new UsuarioNotFoundException("El usuario con email "+email+" no existe");
		
	}

	
	public List<Usuario> getAllUsuarios() throws CifradoContrasenaException {
		List<Usuario> aux = usuarioRepository.findAll();
		for(Usuario u : aux) u.decryptDNI();
		return aux;
	}
	
	public List <Usuario> getAllUsuariosByCentro(String nombreCentro){
		List <Usuario> usuarios = usuarioRepository.findAll();
		List <Usuario> usuariosCentro = new ArrayList<>();
		
		for (Usuario usuario : usuarios) {
			if (usuario.getCentro().equals(nombreCentro))
				usuariosCentro.add(usuario);
		}
		
		return usuariosCentro;
		
	}
	
	public void deleteUsuarioByEmail(String email) {
		usuarioRepository.deleteByEmail(email);
	}
	
	public void deleteUsuarioAndCitasByEmail(String email) throws CitaNotFoundException, CentroNotFoundException, CupoNotFoundException, CupoExistException {
		
		List <Cita> citas = citaDao.getCitasByEmail(email);
		for (Cita cita : citas) {
			citaDao.deleteCitaModificar(cita);
		}
		if (citaDao.getCitasByEmail(email).isEmpty())
			usuarioRepository.deleteByEmail(email);
	}
	
	private boolean validatePasswordPolicy(String password) {
		Pattern regexPassword = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.-])(?=\\S+$).{8,}");
		Matcher comparePassword = regexPassword.matcher(password);
		return comparePassword.matches();
	}

    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
    
	public Usuario modifyUsuario(Usuario newUser) throws UserModificationException, CifradoContrasenaException, UsuarioNotFoundException {
		Optional<Usuario> oldOpt = usuarioRepository.findByEmail(newUser.getEmail());
		Usuario old;
		if(oldOpt.isPresent()) old = oldOpt.get();
		else throw new UsuarioNotFoundException("Usuario no encontrado");
		
		if(newUser.getNombre().equals("")) throw new UserModificationException("Nombre no puede ser nulo");
		if(newUser.getApellidos().equals("")) throw new UserModificationException("Apellidos no puede ser nulo");
		if(newUser.getDni().equals("")) throw new UserModificationException("DNI no puede ser nulo");
		
		if(newUser.getPassword().equals("")) newUser.setPassword(old.getPassword());
		else {
			if(validatePasswordPolicy(newUser.getPassword())) newUser.hashPassword();
			else throw new IllegalArgumentException(wrongContraMssg);
		}
		
		//Atributos no modificables en la interfaz, aparte del email
		newUser.setRol(old.getRol());
		newUser.setPrimeraDosis(old.isPrimeraDosis());
		newUser.setSegundaDosis(old.isSegundaDosis());
		
		if(!newUser.getCentro().equals(old.getCentro()) && newUser.isPrimeraDosis()) throw new UserModificationException("Un usuario ya vacunado no puede cambiar de centro");
		newUser.encryptDNI();
		
		usuarioRepository.save(newUser);
		
		return newUser;
	}
	
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	public List<Usuario> getAllByEmail(List<String> emails) throws CifradoContrasenaException {
		List<Usuario> usuarios = usuarioRepository.findByEmailIn(emails);
		for(Usuario u : usuarios) u.decryptDNI();
		return usuarios;
	}
	
}
