package com.flightplanning.resources.bodaoimpl;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import au.com.bytecode.opencsv.CSVReader;
import org.apache.log4j.Logger;
import com.flightplanning.resources.bo.Airplane;
import com.flightplanning.resources.bodao.AirplaneDao;;


public class AirplaneDaoImpl implements AirplaneDao{
	private PersistenceManagerFactory pmf;
	private static List<Airplane> s_airplanes;
	private final String BASE_PREFIX = "database/";
	private static final Logger logger = Logger.getLogger(AirplaneDaoImpl.class);

	public AirplaneDaoImpl(){
		pmf = SingletonDao.getPersistenceManagerFactory();

		if (s_airplanes == null){
			s_airplanes = importAirplanes(BASE_PREFIX + "airplanesBase.csv");
			addAirplanes(s_airplanes);
		}
	}

	public List<Airplane> getAirplanes(){
		List<Airplane> airplanes = null;
		List<Airplane> detached = new ArrayList<Airplane>();
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query q = pm.newQuery(Airplane.class);
			airplanes = (List<Airplane>)q.execute();
			detached = (List<Airplane>)pm.detachCopyAll(airplanes);
			tx.commit();
		} finally {
			if (tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		return detached;
	}

	public Airplane getAirplane(String id){
		Airplane airplane = null, detached = null;
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			airplane = pm.getObjectById(Airplane.class, id);
			detached = (Airplane)pm.detachCopy(airplane);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return detached;
	}

	public void addAirplanes(List<Airplane> airplanes){
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistentAll(airplanes);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	public List<Airplane> importAirplanes(String pathFileBase){
		List<Airplane> airplanes = new ArrayList<Airplane>();
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
			Airplane a = new Airplane(s);
			airplanes.add(a);
		}
		return airplanes;
	}
}
