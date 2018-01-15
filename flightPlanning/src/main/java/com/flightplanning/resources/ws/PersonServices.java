package com.flightplanning.resources.ws;

import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.flightplanning.resources.bo.Person;
import com.flightplanning.resources.bodao.PersonDao;
import com.flightplanning.resources.bodaoimpl.PersonDaoImpl;

@Path("/")
public class PersonServices {
    private PersonDao pdi;

    @Context
    private HttpServletRequest req;

    public PersonServices(){
	   pdi = new PersonDaoImpl();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(Person person){
    	// Send the salt number to the client
    	return pdi.getSalt(person);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(Person person) throws WebApplicationException{
		HttpSession session = req.getSession(true);
		// Set timeout value in second
		session.setMaxInactiveInterval(20*60);
		Person retrieved = pdi.checkUser(person);
		if (retrieved == null)
		    throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE).build());
	    session.setAttribute("username", retrieved.getLogin());
	    session.setAttribute("ptype", retrieved.getPtype());
	    
	    Response r;
	    if (retrieved.getPtype().equals("cco")){
	    	r = Response.status(210).build();
	    }else{
	    	r = Response.status(211).build();
	    }
	    return r;
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public void logout() throws ServletException{
    	HttpSession session = req.getSession(true);
    	session.invalidate();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/pilot")
    public List<Person> getPilots(){
	   return (ResourceManager.isConnectedUser(req) ? pdi.getPilots() : null);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/copilot")
    public List<Person> getCopilots(){
	   return (ResourceManager.isConnectedUser(req) ? pdi.getCopilots(): null);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/hoststaff")
    public List<Person> getHostStaff(){
	   return (ResourceManager.isConnectedUser(req) ? pdi.getHostStaff() : null);
    }
}