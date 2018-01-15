package com.flightplanning.resources.bodaoimpl;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import org.apache.log4j.Logger;
import com.flightplanning.resources.bo.Airport;
import com.flightplanning.resources.bodao.AirportDao;
import au.com.bytecode.opencsv.CSVReader;

public class AirportDaoImpl implements AirportDao{
	private PersistenceManagerFactory pmf;
	private static List<Airport> s_airports;
	private final String BASE_PREFIX = "database/";
	private static final Logger logger = Logger.getLogger(AirportDaoImpl.class);

	public AirportDaoImpl(){
		pmf = SingletonDao.getPersistenceManagerFactory();

	    if (s_airports == null){
		s_airports = importAirports(BASE_PREFIX + "airportsBase.csv");
		addAirports(s_airports);
	    }
	}

    @SuppressWarnings("unchecked")
    public List<Airport> getAirports(){
		List<Airport> airports = null;
		List<Airport> detached = new ArrayList<Airport>();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.getFetchPlan().setGroup(org.datanucleus.FetchGroup.ALL);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query q = pm.newQuery(Airport.class);
			q.setOrdering("oaci ascending");
			airports = (List<Airport>) q.execute();
			detached = (List<Airport>) pm.detachCopyAll(airports);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return detached;
	}

	public void addAirports(List<Airport> airports){
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistentAll(airports);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

    public List<Airport> importAirports(String pathFileBase){
		List<Airport> airports = new ArrayList<Airport>();
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
			Airport a = new Airport(s);
			airports.add(a);
		}
		return airports;
	}
}
