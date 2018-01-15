package com.flightplanning.resources.ws;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.flightplanning.resources.bo.Crew;
import com.flightplanning.resources.bo.Flight;
import com.flightplanning.resources.bodao.FlightDao;
import com.flightplanning.resources.bodaoimpl.FlightDaoImpl;
import com.flightplanning.resources.exceptions.UnauthorizedException;

@Path("/")
public class FlightServices {
	private FlightDao fdi;
	
	@Context
    private HttpServletRequest req;
	
	public FlightServices() {
		fdi = new FlightDaoImpl();
	}

    private boolean isCcoMember(){
    	String ptype = (String) req.getSession().getAttribute("ptype");
    	return (ptype != null) && (ptype.equals("cco"));
    }
    
    private String getUsername(){
    	return (String) req.getSession().getAttribute("username");
    }

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights")
	public List<Flight> getFlights() {
		List<Flight> flights = null;

		if (ResourceManager.isConnectedUser(req))
			flights = isCcoMember() ? fdi.getFlights() : fdi.getCrewFlights(getUsername());
		return flights;
	}

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/flights")
    public List<Flight> getFlights(@QueryParam("page") int page){
		List<Flight> flights = null;

		if (ResourceManager.isConnectedUser(req))
			flights = isCcoMember() ? fdi.getFlights(page) : fdi.getCrewFlights(page, getUsername());
		return flights;
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights/number")
	public int getFlightsNumber() {
		int pages = 0;
		if (ResourceManager.isConnectedUser(req))
			pages = isCcoMember() ? fdi.getFlightsNumber() : fdi.getCrewFlightsNumber(getUsername());
		return pages;
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}")
	public Flight getFlight(@PathParam("id") String id) {
		if (!isCcoMember() && !fdi.isConcernedByFlight(id, getUsername()))
			throw new UnauthorizedException();
		return ResourceManager.isConnectedUser(req) ? fdi.getFlight(id) : null;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}")
	public void setFlight(@PathParam("id") String id, Flight flight) {
		ResourceManager.isConnectedUser(req);
		if (isCcoMember())
			fdi.setFlight(id, flight);
		else
			throw new UnauthorizedException();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/flights")
	public void addFlight(Flight flight) {
		ResourceManager.isConnectedUser(req);
		if (isCcoMember())
			fdi.addFlight(flight);
		else
			throw new UnauthorizedException();
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}")
	public void removeFlight(@PathParam("id") String id) {
		ResourceManager.isConnectedUser(req);
		if (isCcoMember())
			fdi.removeFlight(id);
		else
			throw new UnauthorizedException();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}/fcrew")
	public Crew getCrew(@PathParam("id") String id) {
		return ResourceManager.isConnectedUser(req) ? fdi.getCrew(id) : null;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}/ofp")
	public String getOFP(@PathParam("id") String id) {
		return ResourceManager.isConnectedUser(req) ? fdi.getOFP(id) : null;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/flights/{id}/notam")
	public String getNOTAM(@PathParam("id") String id) {
		return ResourceManager.isConnectedUser(req) ? fdi.getNOTAM(id) : null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/trade")
	public String getTradeNotice(@PathParam("id") String id) {
		return ResourceManager.isConnectedUser(req) ? fdi.getTradeNotice(id) : null;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/trade")
	public void setTradeNotice(String tradeNotice) {
		ResourceManager.isConnectedUser(req);
		System.out.println("setTradeNotice");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/flights/search")
	public List<Flight> getCriterias(@QueryParam("comNb") String commercialNumber,
			@QueryParam("atcNb") String atcNumber, @QueryParam("plane") String airplane,
			@QueryParam("oaciD") String oaciDeparture, @QueryParam("oaciA") String oaciDestination,
			@QueryParam("timeD") long departureTime, @QueryParam("timeA") long arrivalTime) {

		String[] stringCriterias = new String[5];
		long[] longCriterias = new long[2];

		stringCriterias[0] = commercialNumber;
		stringCriterias[1] = atcNumber;
		stringCriterias[2] = oaciDeparture;
		stringCriterias[3] = oaciDestination;
		stringCriterias[4] = airplane;

		longCriterias[0] = departureTime;
		longCriterias[1] = arrivalTime;

		// We retrieve a flight list which correspond to the criterias entered
		// here we call the DaoImpl function to get a List of flights
		return (ResourceManager.isConnectedUser(req) ? 
				fdi.searchFlights(stringCriterias, longCriterias) : null);
	}
}