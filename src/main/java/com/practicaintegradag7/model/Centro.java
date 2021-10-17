package com.practicaintegradag7.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Centro {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @OneToMany(mappedBy = "centro", cascade = CascadeType.PERSIST)
    private Set<Cita> citas = new HashSet<>();
    
	public Set<Cita> getCentroCitas() {
		return citas;
	}
}