package com.flightplanning.resources.ws;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.flightplanning.resources.bo.Airport;
import com.flightplanning.resources.bodao.AirportDao;
import com.flightplanning.resources.bodaoimpl.AirportDaoImpl;

@Path("/")
public class AirportServices {
    private AirportDao adi;
    @Context
    private HttpServletRequest req;

    public AirportServices(){
	   adi = new AirportDaoImpl();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/airports")
    public List<Airport> getAirports(){
    	HttpSession session = req.getSession();
    	if (session.getAttribute("ptype") != null){
    		if (!session.getAttribute("ptype").equals("cco"))
    			return null;
    	}
    	return (ResourceManager.isConnectedUser(req) ? adi.getAirports() : null);
    }
}