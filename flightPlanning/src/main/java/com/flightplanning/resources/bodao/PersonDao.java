package com.flightplanning.resources.bodao;

import java.util.List;
import com.flightplanning.resources.bo.Person;


public interface PersonDao{
	/**
     * @return the list of persons
     **/
	List<Person> getPersons();
	/**
     * @return the list of pilots
     **/
    List<Person> getPilots();

    /**
     * @return the list of co-pilots
     **/
    List<Person> getCopilots();

    /**
     * @return the list of staff
     **/
    List<Person> getHostStaff();
    
    /**
     * @param person the user who wants to connect himself
     * @return salt of the person
     **/
    String getSalt(Person person);

    /**
     * @param person the user who wants to connect himself
     * @return person detached
     */
    Person checkUser(Person person);
}
