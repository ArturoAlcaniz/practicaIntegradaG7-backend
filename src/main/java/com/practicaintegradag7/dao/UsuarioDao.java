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
import com.practicaintegradag7.exceptions.CupoNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioNotFoundException;
import com.practicaintegradag7.exceptions.UsuarioVacunadoException;
import com.practicaintegradag7.model.Cita;
import com.practicaintegradag7.model.Usuario;
import com.practicaintegradag7.repos.UsuarioRepository;


/**
 * 
 * Se encarga de la lógica del usuario
 * Su funcionalidad será acceder, crear, actualizar y borrar a los usuarios
 *
 */

@Service
public class UsuarioDao {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CitaDao citaDao;
	
	private String wrongContraMssg = "Contrasena no valida, use como minimo 9 caracteres, mayusculas y minusculas, y al menos un numero y un simbolo";
	
	/**
	 * Método que se encarga de insertar un usuario
	 * @param usuario que se quiere almacenar
	 * @return usuario que se ha creado, null en el caso de que ya exista
	 * @throws CifradoContrasenaException excepción que se lanza si al cifrar la contraseña falla
	 * @throws IllegalArgumentException se lanza cuando el dni o la contraseña dados no son correctos,
	 * especifica cual no es correcta
	 */
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
	
	/**
	 * Método que borra todos los usuarios guardados
	 */
	public void deleteAllUsuarios() {
		usuarioRepository.deleteAll();
	}
	
	/**
	 * Método que devuelve el usuario con el email dado
	 * @param email que identifica al usuario
	 * @return usuario con el email que se especificó
	 * @throws UsuarioNotFoundException excepción que salta cuando no existe ningún usuario
	 * con dicho email
	 */
	public Usuario getUsuarioByEmail(String email) throws UsuarioNotFoundException {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		if (usuario.isPresent()) {
			return usuario.get();
		}else throw new UsuarioNotFoundException("El usuario con email "+email+" no existe");
		
	}

	/**
	 * Método que devuelve todos los usuarios
	 * @return lista de usuarios que tiene todos los usuarios
	 * @throws CifradoContrasenaException excepción que salta cuando se descifran los dni
	 * para que se puedan interpretar
	 */
	public List<Usuario> getAllUsuarios() throws CifradoContrasenaException {
		List<Usuario> aux = usuarioRepository.findAll();
		for(Usuario u : aux) u.decryptDNI();
		return aux;
	}
	
	/**
	 * Método que devuelve todos los usuarios que pertenecen a un centro
	 * @param nombreCentro que identifica al centro del que se quieren saber los usuarios
	 * @return lista de usuarios que pertenecen a dicho centro
	 */
	public List <Usuario> getAllUsuariosByCentro(String nombreCentro){
		List <Usuario> usuarios = usuarioRepository.findAll();
		List <Usuario> usuariosCentro = new ArrayList<>();
		
		for (Usuario usuario : usuarios) {
			if (usuario.getCentro().equals(nombreCentro))
				usuariosCentro.add(usuario);
		}
		
		return usuariosCentro;
		
	}
	
	/**
	 * Método que borra al usuario con dicho email
	 * @param email que identifica al usuario que se desea borrar
	 */
	public void deleteUsuarioByEmail(String email) {
		usuarioRepository.deleteByEmail(email);
	}
	
	/**
	 * Método que borra todas a un usuario y a todas sus citas si no está vacunado
	 * @param email que identifica al usuario
	 * @throws CitaNotFoundException excepción que se lanza cuando no existen citas
	 * @throws CentroNotFoundException excepción que se lanza cuando no existen centros
	 * @throws CupoNotFoundException excepción que se lanza cuando no existe el cupo de las citas
	 * @throws UsuarioNotFoundException excepción que se lanza cuando no existe un usuario con dicho email
	 * @throws UsuarioVacunadoException excepción que se lanza cuando un usuario está vacunado y
	 * no se puede borrar.
	 */
	public void deleteUsuarioAndCitasByEmail(String email) throws CitaNotFoundException, CentroNotFoundException, CupoNotFoundException, UsuarioNotFoundException, UsuarioVacunadoException {
		
		Usuario usuario = getUsuarioByEmail(email);
		if(usuario.isPrimeraDosis()) {
			throw new UsuarioVacunadoException();
		}

		List <Cita> citas = citaDao.getCitasByEmail(email);
		for (Cita cita : citas) {
			citaDao.deleteCitaModificar(cita);
		}
		if (citaDao.getCitasByEmail(email).isEmpty())
			usuarioRepository.deleteByEmail(email);
	}
	
	/**
	 * Método que comprueba que la contraseña cumple la política de contraseñas
	 * @param password que se tiene que comprobar que cumple la política de contraseñas
	 * @return boolean que te dice si la contraseña es válida o no
	 */
	private boolean validatePasswordPolicy(String password) {
		Pattern regexPassword = Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.-])(?=\\S+$).{8,}");
		Matcher comparePassword = regexPassword.matcher(password);
		return comparePassword.matches();
	}

	/**
	 * Método que comprueba que el dni sea válido
	 * @param dni que pertenece al usuario y se va a validar
	 * @return boolean que te dice si el dni es válido o no
	 */
    private boolean validateDNI(String dni) {
    	Pattern regexDni = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
    	Matcher compareDni = regexDni.matcher(dni); 
    	return compareDni.matches();
    }
    
    /**
     * Método que permite modificar los datos que se tienen de un usuario
     * @param newUser usuario que tiene todos los datos actualizados del usuario
     * @return usuario que se ha almacenado para que se tenga confirmación de los datos actualizados
     * @throws UserModificationException excepción que se lanza si el nombre, los apellidos
     * o el nombre son nulos
     * @throws CifradoContrasenaException excepción que se lanza si falla al cifrar el dni
     * @throws UsuarioNotFoundException excepción que se lanza si no existe el usuario que se
     * quiere modificar
     */
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
	
	/**
	 * Método que se encarga de insertar un usuario
	 * @param usuario que se quiere almacenar
	 * @return usuario que se ha creado, null en el caso de que ya exista
	 */
	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}
	
	/**
	 * Método que guarda un grupo de usuarios
	 * @param usuarios lista de usuarios que se guarda
	 */
	public void saveAll(List<Usuario> usuarios) {
		usuarioRepository.saveAll(usuarios);
	}
	
	/**
	 * Método que devuelve un grupo de usuarios que son identificados por un grupo de emails
	 * @param emails lista de los usuarios que se quieren obtener
	 * @return lista de usuarios que se han buscado
	 * @throws CifradoContrasenaException excepción que se lanza al descifrar el dni de un usuario
	 */
	public List<Usuario> getAllByEmail(List<String> emails) throws CifradoContrasenaException {
		List<Usuario> usuarios = usuarioRepository.findByEmailIn(emails);
		for(Usuario u : usuarios) u.decryptDNI();
		return usuarios;
	}
	
}
