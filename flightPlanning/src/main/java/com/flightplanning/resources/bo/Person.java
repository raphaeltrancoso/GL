package com.flightplanning.resources.bo;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Person{
    @PrimaryKey
    private String login;
    private String password;
    private String ptype;
    private String firstName;
    private String lastName;
    private String email;
    public static final int MD5_SIZE = 32;

    public Person(){
        // No need to create an empty instance of Person
    }

    // Complete constructor (for the data import)
    public Person(String[] attributes){
    	login = attributes[0];
    	password = attributes[1];
    	ptype = attributes[2];
    	firstName = attributes[3];
    	lastName = attributes[4];
    	email = attributes[5];
    }

    public String getLogin(){
    	return login;
    }

    public String getPassword(){
    	return password;
    }

    public String getHash(){
    	return password.substring(0, MD5_SIZE);
    }

    public String getSalt(){
    	return password.substring(MD5_SIZE);
    }

    public String getPtype(){
    	return ptype;
    }

    public String getFirstName(){
    	return firstName;
    }

    public String getLastName(){
    	return lastName;
    }
    
    public String getEmail(){
    	return email;
    }
}