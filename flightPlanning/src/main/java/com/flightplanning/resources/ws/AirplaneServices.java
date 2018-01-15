package com.flightplanning.resources.ws;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.flightplanning.resources.bo.Airplane;
import com.flightplanning.resources.bodao.AirplaneDao;
import com.flightplanning.resources.bodaoimpl.AirplaneDaoImpl;

@Path("/")
public class AirplaneServices {
    private AirplaneDao adi;

    @Context
    private HttpServletRequest req;

    public AirplaneServices() {
    	adi = new AirplaneDaoImpl();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/airplanes")
    public List<Airplane> getAirplanes() {
    	return ResourceManager.isConnectedUser(req) ? adi.getAirplanes() : null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/airplanes/{id}")
    public Airplane getAirplane(@PathParam("id") String id) {
    	return ResourceManager.isConnectedUser(req) ? adi.getAirplane(id) : null;
    }
}