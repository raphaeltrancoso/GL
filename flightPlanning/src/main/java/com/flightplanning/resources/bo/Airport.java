package com.flightplanning.resources.bo;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Airport{
    private String oaci;
    private String name;

    // Empty Constructor
    public Airport(){}

    public Airport(String[] attributes){
	oaci = attributes[0];
	name = attributes[1];
    }

    public String getOACI(){
	return oaci;
    }

    public String getName(){
	return name;
    }
}
