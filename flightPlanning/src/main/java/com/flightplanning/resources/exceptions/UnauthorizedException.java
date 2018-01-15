package com.flightplanning.resources.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The following exception class may be called in a method where
 * user access rights are required to access a certain HTML page.
 */
public class UnauthorizedException extends WebApplicationException {
	
	/**
	 * A class which implements the interface 'serializable' needs a serialID
	 * to identify, in an unique way, this class.  
	 */
	private static final long serialVersionUID = 4762859079423401234L;
	
	/**
	 * The message to display when the request fails in HTML way.
	 */
	private static final String ExceptionMessage = 
		"<!DOCTYPE html><body><div style=\"font-size:30px;"
		+ "text-align:center;margin-top:5%;\"><span>Access denied."
		+ " Please login</span><br/><a href=\"/\">"
		+ "Click here to login</a></div></body></html>";
	
	/**
	 * The default constructor which prints the exception message.
	 */
	public UnauthorizedException(){
		super(Response.status(Response.Status.UNAUTHORIZED)
				.entity(ExceptionMessage).type(MediaType.TEXT_HTML).build());
	}
	
	/**
	 * The specialized constructor which prints the message passed in param.
	 * @param message The message to print.
	 * @param typeMessage The type of the message passed in param. 
	 * Among the available types, we have 'text/plain', 'MediaType.TYPE'
	 */
	public UnauthorizedException(String message, String typeMessage){
		super(Response.status(Response.Status.UNAUTHORIZED)
				.entity(message).type(typeMessage).build());
	}
}
