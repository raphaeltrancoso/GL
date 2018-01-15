package com.flightplanning.resources.bodao;

import java.util.List;

import com.flightplanning.resources.bo.Crew;
import com.flightplanning.resources.bo.Flight;

public interface FlightDao{
    /**
     * @return this list of Flight
     */
    List<Flight> getFlights();

    List<Flight> getFlights(int page);

    /** 
     * @return The total number of flights. 
     */
    int getFlightsNumber();
    
    /**
     * @param id of the flight we want to return
     * @param login of the user that is concerned by flight or not
     * @return true if the flight contains the user, false otherwise.
     */
    boolean isConcernedByFlight(String id, String login);
    
    /** 
     * @param The login of the connected user. 
     * @return the total number of flights which concern the user.
     */
    int getCrewFlightsNumber(String login);
    
    /**
     * @param departure
     * @return the list of Flights assigned to a specific departure date.
     */
    Flight getFlight(String id);

    /**
     * @modify an existing flight
     */
    void setFlight(String id, Flight flight);

    /**
     * @add a new flight
     **/
    void addFlight(Flight fly);

    /**
     * 
     * @remove an existing flight
     **/
    void removeFlight(String id);

    
    List<Flight> searchFlights(String[] stringCriterias, long[] longCriterias);    
    
    /**
     * @param departure
     */
    Crew getCrew(String id);

    /**
     * @remove a crew from a flight
     * @param departure
     */
    String getOFP(String id);

    /** 
     * @param departure
     * @return The URI of the NOTAM 
     */
    String getNOTAM(String id);

    /** 
     * @param departure
     * @return The URI of the trade notice 
     */
    String getTradeNotice(String id);

    /** 
     * @param departure
     * @return The URI of the trade notice 
     */
    void setTradeNotice(String id);
    
    /**
     * @param login
     * @param pageNumber
     * @return the person's flights by pages
     */
    List<Flight> getCrewFlights(int page, String login);
    
    /**
     * @param login
     * @return the person's flights
     */
    List<Flight> getCrewFlights(String login);
}