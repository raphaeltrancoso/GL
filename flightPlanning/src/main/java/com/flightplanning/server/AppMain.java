package com.flightplanning.server;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import com.flightplanning.alert.AlertsSystem;

public class AppMain {

	private static final String PKG_PREFIX = "com.flightplanning.resources.";
	private static final String[] RSRC_LIST = 
		{ "ws", "bo", "bodao", "bodaoimpl", "exceptions" };
	
    private static String[] concatResourcesPath(){
    	String[] allResources = new String[RSRC_LIST.length];
    	int i = 0;
		for (String s : RSRC_LIST) { 
			allResources[i++] = PKG_PREFIX + s; 
		}
		return allResources;
    }
	
	public static void main(String[] args) {
		Logger logger = Logger.getLogger(AppMain.class);
		
		// Initialize the server
		Server server = new Server();
	
		// Add a connector
		ServerConnector connector = new ServerConnector(server);
		connector.setHost("0.0.0.0");
		connector.setPort(8081);
		connector.setIdleTimeout(30000);
		server.addConnector(connector);
	
		// Configure Jersey
		ResourceConfig rc = new ResourceConfig();
		rc.packages(true, concatResourcesPath());
		rc.register(JacksonFeature.class);
		rc.register(LoggingFilter.class);
	
		// Add a servlet handler for web services
		ServletHolder servletHolder = new ServletHolder(new ServletContainer(rc));
		ServletContextHandler handlerWebServices =
		    new ServletContextHandler(ServletContextHandler.SESSIONS);
		handlerWebServices.setContextPath("/ws");
		handlerWebServices.addServlet(servletHolder, "/*");
	
		// Add a handler for resources (/*)
		ResourceHandler handlerPortal = new ResourceHandler();
		handlerPortal.setResourceBase("webapp");
		handlerPortal.setDirectoriesListed(false);
		handlerPortal.setWelcomeFiles(new String[] { "index.html" });
		ContextHandler handlerPortalCtx = new ContextHandler();
		handlerPortalCtx.setContextPath("/");
		handlerPortalCtx.setHandler(handlerPortal);
	
		// Activate handlers
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { handlerWebServices, handlerPortalCtx });
		server.setHandler(contexts);
	
		// Start server
		try{
		    server.start();
		}
		catch(Exception e){
		    logger.error("Un serveur est déjà en route", e);
		}
		
		// Start alert
		Timer t = new Timer();
        GregorianCalendar gc = new GregorianCalendar(); 
        gc.add(Calendar.SECOND, 10); //start 10 secondes apres
        t.scheduleAtFixedRate(new AlertsSystem(), gc.getTime(),2000);
        
		/*Alertes alertes = new Alertes();
		alertes.start();*/
    }
}
