package com.practicaintegradag7.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@RestController
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST})
public class HelloController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Hola Mundo! La hora en el servidor es " + new Date() + "\n";
    }
}