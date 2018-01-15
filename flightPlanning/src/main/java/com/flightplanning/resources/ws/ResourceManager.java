package com.flightplanning.resources.ws;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Path;

import com.flightplanning.resources.bo.Crew;
import com.flightplanning.resources.bo.Flight;
import com.flightplanning.resources.bo.Person;
import com.flightplanning.resources.exceptions.UnauthorizedException;

/**
 * Class providing a server side mechanism to check
 * information consistency for business objects.
 */
@Path("/")
public class ResourceManager {
	/**
	 *  Don't let anyone instantiate this class.
	 */
	private ResourceManager() {
	}

    /**
     * The following method permits to
     * @return True if the user is connected, otherwise an exception is thrown.
     * @throws UnauthorizedException No attribute 'username' was found.
     */
    public static boolean isConnectedUser(HttpServletRequest req) 
    		throws UnauthorizedException {
    	HttpSession session = req.getSession(true);
    	if (session.getAttribute("username") == null){
    	    throw new UnauthorizedException();
    	}
    	return true;
    }

	private static boolean matchAlphabetic(String text){
		if(text == null)
			return true;
		return text.matches("[A-Za-z]{1,30}");
	}

	private static boolean matchAlphanumeric(String text){
		if(text == null)
			return true;
		return text.matches("[A-Za-z0-9]{1,30}");
	}

	private static boolean matchFilename(String filename){
		if(filename == null)
			return true;
		return filename.matches("[A-Za-z0-9]{1,30}\\.[A-Za-z0-9]{1,10}");
	}

	public static void checkPerson(Person person) throws AuthenticationException{
		// Minimum required fields
		boolean requiredFields = person.getLogin() == null || person.getHash() == null;
		boolean checkContent = matchAlphabetic(person.getFirstName())
			&& matchAlphabetic(person.getLastName())
			&& matchAlphanumeric(person.getLogin())
			&& matchAlphanumeric(person.getPtype());
		if(requiredFields || !checkContent)
			throw new AuthenticationException();
	}

	private static void checkCrew(Crew crew) throws AuthenticationException{
		boolean checkContent = matchAlphanumeric(crew.getLoginPilot())
				&& matchAlphanumeric(crew.getLoginCopilot());
	    for(String login: crew.getLoginHostStaff())
	    	checkContent = checkContent && matchAlphanumeric(login);
	    if(!checkContent)
	    	throw new AuthenticationException();
	}

	public static void checkFlight(Flight flight) throws AuthenticationException{
		// Minimum required fields
		boolean requiredFields = flight.getOaciDeparture() == null || flight.getCommercialNumber() == null;
		boolean checkContent = matchAlphabetic(flight.getOaciDeparture())
				&& matchAlphanumeric(flight.getCommercialNumber())
				&& matchAlphabetic(flight.getOaciDestination())
				&& matchAlphanumeric(flight.getATC())
				&& matchFilename(flight.getOFP())
				&& matchFilename(flight.getNOTAM())
				&& matchFilename(flight.getMeteo())
				&& matchAlphanumeric(flight.getTradeNotice())
				&& matchAlphanumeric(flight.getIdAirplane());
		checkCrew(flight.getCrew());
	    if(requiredFields || !checkContent)
	    	throw new AuthenticationException();
	}
}