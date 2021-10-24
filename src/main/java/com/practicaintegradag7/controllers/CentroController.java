package com.practicaintegradag7.controllers;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000", "https://iso-g7-frontend.herokuapp.com"})
@RestController
public class CentroController {
	//TO DO: Autowired DAO's
	
	@PostMapping("api/addVaccines")
	public void addVacunas(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		String centro = jso.getString("hospital");
		int amount = jso.getInt("amount");
		//Do whatever, update DB
		System.out.println("Centro: "+centro+", cantidad: "+amount);
	}
}
