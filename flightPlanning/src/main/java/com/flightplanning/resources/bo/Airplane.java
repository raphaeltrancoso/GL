package com.flightplanning.resources.bo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Airplane{
	@PrimaryKey
	private String id;
    private String type;
    private int weight;
    private int capacity;

    public Airplane(){
    	// No need to create an instance of Airplane
    }

    public Airplane(String[] attributes){
    	id = attributes[0];
    	type = attributes[1];
    	weight = Integer.parseInt(attributes[2]);
    	capacity = Integer.parseInt(attributes[3]);
    }

    public String getId(){
    	return id;
    }

    public String getType(){
    	return type;
    }

    public int getWeight(){
    	return weight;
    }

    public int getCapacity(){
    	return capacity;
    }

    @Override
    public String toString(){
    	return "airplane " + id + ", type " + type 
    			+ ", weight: " + weight + ", capacity: " + capacity;
    }
}