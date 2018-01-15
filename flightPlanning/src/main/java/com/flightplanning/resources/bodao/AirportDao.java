package com.flightplanning.resources.bodao;

import java.util.List;

import com.flightplanning.resources.bo.Airport;

public interface AirportDao{
    /**
     * @return this list of Flight
     */
    List<Airport> getAirports();
    
    void addAirports(List<Airport> airports);
    
    List<Airport> importAirports(String pathFileBase);
}
