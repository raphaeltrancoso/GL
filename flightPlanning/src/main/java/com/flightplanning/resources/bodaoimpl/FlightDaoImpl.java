package com.flightplanning.resources.bodaoimpl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import org.apache.log4j.Logger;

import com.flightplanning.resources.bo.Airplane;
import com.flightplanning.resources.bo.Airport;
import com.flightplanning.resources.bo.Crew;
import com.flightplanning.resources.bo.Flight;
import com.flightplanning.resources.bo.PrimKey;
import com.flightplanning.resources.bodao.FlightDao;

import javax.jdo.Query;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class FlightDaoImpl implements FlightDao{
	private PersistenceManagerFactory pmf;
	private static List<Flight> s_flights = null;
    private final String BASE_PREFIX = "database/";
    private static final Logger logger = Logger.getLogger(FlightDaoImpl.class);

	public FlightDaoImpl(){
		pmf = SingletonDao.getPersistenceManagerFactory();

		if(s_flights == null){
		    s_flights = importFlights(BASE_PREFIX + "flightsBase.csv");
		    addFlights(s_flights);
		}
	}

	private PrimKey splitFunction(String id){
		String [] keys = id.replace('+',' ').split("_");
		return new PrimKey(keys[0], keys[1], keys[2]);
	}

	@SuppressWarnings("unchecked")
	private <T> T instanceByKey(String id, PersistenceManager pm, boolean unique){
		PrimKey pk = splitFunction(id);
		long paramDeparture = Long.parseLong(pk.getDate());
		Query q = pm.newQuery("SELECT FROM "+ Flight.class.getName()
				+ " WHERE departure == :departure && oaciDeparture == '"
				+ pk.getOaciDeparture() + "' && commercialNumber == '"
				+ pk.getCommercialNumber() + "'");
		q.setUnique(unique);
		return (T)q.execute(paramDeparture);
	}
	
	
	

	// Permits to process to a certain action according to the tag action name
	@SuppressWarnings("unchecked")
	private <T> T processActionByTag(String tagAction, String id, T element,
			T newElement, boolean unique, int numPage){
		
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.getFetchPlan().setGroup(org.datanucleus.FetchGroup.ALL);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			if (unique){
				element = (id != null) ? (T)instanceByKey(id, pm, true) : null;
				
				if (tagAction.equals("get")){
					Flight c = (Flight)pm.detachCopy((Flight)element);
					element = (T)c;
				} else if (tagAction.equals("set")){
					if(element!=null)
						((Flight)element).setFlight((Flight)newElement);
				} else if (tagAction.equals("remove")){
					pm.deletePersistent((Flight)element);
				} else { // addFlight
					pm.makePersistent((Flight)newElement);
					exportFlight((Flight)newElement);
				}	
			}else{
				if (tagAction.equals("get")){
					// 'RANGE' exemple : "RANGE 0,10" renvoie les résultats compris dans [0...9]
					Query q = pm.newQuery(Flight.class);
					if (numPage > 0){
						q.setRange((numPage-1)*10, numPage*10);
					}
					q.setOrdering("departure ascending");
					element = (T)q.execute();
					List<Flight> c = (List<Flight>)pm.detachCopyAll((List<Flight>)element);
					element =(T)c;	
				}else { // addFlights
					pm.makePersistentAll((List<Flight>)element);
				}
			}
			tx.commit();
		} catch(Exception e){
			logger.error("Database connection Error",e);
		} finally{
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return element;
		
	}

	private String buildQuery(String [] stringCriterias, long [] longCriterias, String [] airplanes, String [] airportsDep, String [] airportsArvl){
	    String [] fields = new String []{"commercialNumber",
				"atc"};
		String [] fieldsLong = new String[]{" departure >= ", " departure <= "};
		String q = " ";
		boolean bl = false;
		boolean b2 = false;
		boolean b3 = false;
		boolean b4 = false;
		for (int i = 0; i < 2; i++){
			if (stringCriterias[i] != null){
				q += bl ? " && " : "";
				// string researched in uppercase form 
				String UCResearched = stringCriterias[i].toUpperCase();
				q += fields[i] + ".toUpperCase().indexOf('" + UCResearched + "') >= 0";
				bl = true;
			}
		}
		
		for (int i = 0; i < longCriterias.length ; i++){
			if (longCriterias[i]!=0){
				q += bl ? " && " : "";
				q+=fieldsLong[i]+Long.toString(longCriterias[i]);
				bl = true;
			}
		}
		
		if (airplanes != null){
			for (int i=0;i<airplanes.length;i++){
				if(bl){
					if (b2){
						q+=" || ";
					}else{
						q+=" && ( ";
					}
				}else{
					q+=" ( ";
				}
				q += "idAirplane.indexOf('" + airplanes[i] + "') >= 0";
				bl=true;
				b2=true;
			}
			q += " )";
		}
		if (airportsDep != null){
			for (int i=0;i<airportsDep.length;i++){
				if(bl){
					if (b3){
						q+=" || ";
					}else{
						q+=" && ( ";
					}
				}else{
					q+=" ( ";
				}
				q += "oaciDeparture" + ".indexOf('"+airportsDep[i]+"') >= 0";
				bl=true;
				b3=true;
			}
			q += " )";
		}
		if (airportsArvl != null){
			for (int i=0;i<airportsArvl.length;i++){
				if(bl){
					if (b4){
						q+=" || ";
					}else{
						q+=" && ( ";
					}
				}else{
					q+=" ( ";
				}
				q += "oaciDestination" + ".indexOf('"+airportsArvl[i]+"') >= 0";
				bl=true;
				b4=true;
			}
			q += " )";
		}
		return q;
	}

	public List<Flight> searchFlights(String [] stringCriterias, long [] longCriterias){
		List<Flight> flights = null;
		List<Airplane> airplane = null;
		List<Airport> airport = null;
		String [] airplnes = null;
		String [] airportsDep = null;
		String [] airportsArvl = null;
		String UCResearched;
		List<Flight> detached = new ArrayList<Flight>();
		List<Airplane> airplanes = new ArrayList<Airplane>();
		List<Airport> airports = new ArrayList<Airport>();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.getFetchPlan().setGroup(org.datanucleus.FetchGroup.ALL);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			if (stringCriterias[4]!=null) {
				UCResearched = stringCriterias[4].toUpperCase();
				Query q1 = pm.newQuery("SELECT FROM "+ Airplane.class.getName() + " WHERE "+"type"+".toUpperCase().indexOf('"+UCResearched+"') >= 0");
				airplane = (List<Airplane>) q1.execute();
				airplanes = (List<Airplane>) pm.detachCopyAll(airplane);
				if (! airplanes.isEmpty()){
					airplnes = new String[airplanes.size()];
					for (int i=0;i<airplanes.size();i++){
						airplnes[i]=airplanes.get(i).getId();
					}
				}
			}
			if (stringCriterias[2]!=null) {
				UCResearched = stringCriterias[2].toUpperCase();
				Query q1 = pm.newQuery("SELECT FROM "+ Airport.class.getName());// + " WHERE "+"name"+".toUpperCase().indexOf('"+UCResearched+"') >= 0");
				airport = (List<Airport>) q1.execute();
				airports = (List<Airport>) pm.detachCopyAll(airport);
				if (!airports.isEmpty()){
					airportsDep = new String[airports.size()];
					for (int i=0;i<airports.size();i++){
						airportsDep[i]=airports.get(i).getOACI();
					}
				}
			}
			if (stringCriterias[3]!=null) {
				UCResearched = stringCriterias[3].toUpperCase();
				Query q1 = pm.newQuery("SELECT FROM "+ Airport.class.getName() + " WHERE "+"name"+".toUpperCase().indexOf('"+UCResearched+"') >= 0");
				airport = (List<Airport>) q1.execute();
				airports = (List<Airport>) pm.detachCopyAll(airport);
				if (! airports.isEmpty()){
					airportsArvl = new String[airports.size()];
					for (int i=0;i<airports.size();i++){
						airportsArvl[i]=airports.get(i).getOACI();
					}
				}
			}
			
			boolean invalidData = true;
			for (String sc : stringCriterias){
				invalidData &= sc == null;
			}
				
			Query q;
			if (invalidData){
				tx.rollback();
				pm.close();
				return getFlights(1);
			}else{
				q = pm.newQuery("SELECT FROM "+ Flight.class.getName() 
						+ " WHERE "+buildQuery(stringCriterias, longCriterias, 
								airplnes, airportsDep, airportsArvl));
			}
			q.setOrdering("departure ascending");
			flights = (List<Flight>) q.execute();
			detached = (List<Flight>) pm.detachCopyAll(flights);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return detached;
	}

	public List<Flight> getFlights(){
		List<Flight> flights = null;
		return processActionByTag("get", null, flights, null, false, 0);
	}
	
	public List<Flight> getCrewFlights(final String login){
		List<Flight> flights = getFlights();
		Predicate<Flight> filter = new Predicate<Flight>() {
			@Override public boolean test(Flight f){
				return (!f.getCrew().concerned(login) ? true : false);
			}
		};
		flights.removeIf(filter);
		return flights;
	}
	
	public List<Flight> getCrewFlights(int page, String login){
		List<Flight> flights = getCrewFlights(login);
		int min = (page - 1) * 10, max = page * 10, length = flights.size();
		if(max > length)
			max = length;
		return flights.subList(min, max);
	}
	
	public List<Flight> getFlights(int page){
		List<Flight> flights = null;
		return processActionByTag("get", null, flights, null, false, page);
	}

	/* The following function returns the total number of flights in our database  
	 * But it is inefficient cause it loads all flights from database before getting
	 * the total number of entries in our list.
	 */
	public int getFlightsNumber(){
		int max = 0;
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.getFetchPlan().setGroup(org.datanucleus.FetchGroup.ALL);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query query = pm.newQuery(Flight.class);
			List<Flight> f = (List<Flight>)query.execute();
			max = ((List<Flight>)pm.detachCopyAll(f)).size();
			tx.commit();
		}catch(Exception e){
			logger.error("Database connection Error",e);
		} finally{
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return max;
	}
	
	public boolean isConcernedByFlight(String id, String login){
		System.out.println("Reçu : " + id);
		Flight f = getFlight(id);
		return (f.getCrew().concerned(login) ? true : false);
	}
	
	public int getCrewFlightsNumber(String login){
		return (getCrewFlights(login).size());
	}

	public Flight getFlight(String id){
		Flight flight = null;
		return processActionByTag("get", id, flight, null, true, 0);
	}

	public void setFlight(String id, Flight flight){
		Flight flightObj = null;
		processActionByTag("set", id, flightObj, flight, true, 0);
	}

	public void addFlight(Flight flight){
		processActionByTag("add", null, null, flight, true, 0);
	}

	public void removeFlight(String id){
		Flight flight = null;
		processActionByTag("remove", id, flight, null, true, 0);
	}
	public Crew getCrew(String id){
		return getFlight(id).getCrew();
	}

	public String getOFP(String id){
		return getFlight(id).getOFP();
	}

	public String getNOTAM(String id){
		return getFlight(id).getNOTAM();
	}

	public String getTradeNotice(String id){
		return getFlight(id).getTradeNotice();
	}

	public void setTradeNotice(String id){
		// TODO Auto-generated method stub
	}

	private void addFlights(List<Flight> flights) {
		processActionByTag("add", null, flights, null, false, 0);
	}

	private List<Flight> importFlights(String pathFileBase) {
		List<Flight> flights = new ArrayList<Flight>();
		List<String[]> data = null;
		try {
			Reader file = new FileReader(pathFileBase);
			CSVReader reader = new CSVReader(file);
			data = reader.readAll();
			reader.close();
			file.close();
		}catch(Exception e){
			logger.error("Le fichier de base externe est introuvable" +
					" ou n'existe pas ou les données contenues n'ont pas le bon format.",e);
		}

		for (String[] s : data){
			Flight a = new Flight(s);
			flights.add(a);
		}
		return flights;
	}

	private void exportFlight(Flight newFlight){
		FileWriter fw;
		ArrayList<String> dataList = new ArrayList<String>();
		dataList.addAll(newFlight.serializeAttributes());

		try {
			fw = new FileWriter(BASE_PREFIX + "flightsBase.csv", true);
			CSVWriter writer = new CSVWriter(fw, ',');
			String[] data = new String[dataList.size()];
			writer.writeNext(dataList.toArray(data));
			writer.close();
			fw.close();
		} catch (Exception e) {
			logger.error("Erreur lors de l'écriture du vol dans le fichier.",e);
		}
	}
}
