package com.flightplanning.resources.bodao;

import java.util.List;
import com.flightplanning.resources.bo.Airplane;

public interface AirplaneDao{
	/**
	 * @return the list of all available airplanes
	 */
	List<Airplane> getAirplanes();

	/**
	 * Permits to retrieve an airplane corresponding to its id
	 * @param id The identifier of the airplane to retrieved
	 * @return the corresponding airplane object
	 */
	Airplane getAirplane(String id);

	void addAirplanes(List<Airplane> airplanes);
	
	List<Airplane> importAirplanes(String pathFileBase);
}
