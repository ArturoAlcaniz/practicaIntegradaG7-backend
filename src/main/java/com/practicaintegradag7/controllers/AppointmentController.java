package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.Map;

@CrossOrigin
@RestController
public class AppointmentController{
	
	@PostMapping(path="/api/makeAppointment", consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String giveAppointment(@RequestBody Map<String, Object> info) {
		JSONObject jso = new JSONObject(info);
		String user = jso.getString("user");
		//Consultar BD, etc
		System.out.println(user);
		return "Fecha para "+user+" : "+new Date();
	}
}
