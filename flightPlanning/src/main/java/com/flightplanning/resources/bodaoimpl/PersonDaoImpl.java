package com.flightplanning.resources.bodaoimpl;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import org.apache.log4j.Logger;
import com.flightplanning.resources.bo.Person;
import com.flightplanning.resources.bodao.PersonDao;

import au.com.bytecode.opencsv.CSVReader;

public class PersonDaoImpl implements PersonDao{
	private static final Logger logger = Logger.getLogger(PersonDaoImpl.class);
    private PersistenceManagerFactory pmf;
    private static List<Person> s_persons = null;
    private final String BASE_PREFIX = "database/";
 
    public PersonDaoImpl(){
		pmf = SingletonDao.getPersistenceManagerFactory();

    	if(s_persons == null){
    		s_persons = importPersons(BASE_PREFIX + "personsBase.csv");
    		addPersons(s_persons);
    	}
    }

    private static String toMD5(String md5){
    	try{
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] array = md.digest(md5.getBytes());
    		StringBuffer sb = new StringBuffer();
    		for(int i = 0; i < array.length; i++)
    			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
    		return sb.toString();
    	}
    	catch(NoSuchAlgorithmException e){
    	}
    	return null;
	}

    @SuppressWarnings("unchecked")
    public List<Person> getPersons(){
		List<Person> persons = null;
		List<Person> detached = new ArrayList<Person>();
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.getFetchPlan().setGroup(org.datanucleus.FetchGroup.ALL);
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			Query q = pm.newQuery(Person.class);
			persons = (List<Person>) q.execute();
			detached = (List<Person>) pm.detachCopyAll(persons);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		return detached;
	}

    @SuppressWarnings("unchecked")
	public List<Person> getCategoryOfFlightcrew(String ptype){
    	List<Person> persons = null;
		List<Person> detached = new ArrayList<Person>();
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
		    tx.begin();
		    Query q;

		    if(!ptype.equals("hoststaff")){
		    	q = pm.newQuery("SELECT FROM " + Person.class.getName() + 
		    			" WHERE ptype == '" + ptype +"'");
		    }
		    else{
		    	q = pm.newQuery("SELECT FROM " + Person.class.getName() + 
		    			" WHERE ptype == 'stewart' || ptype == 'hostess'");
		    }
			persons = (List<Person>) q.execute();
		    detached = (List<Person>) pm.detachCopyAll(persons);
		    tx.commit();
		} finally {
		    if (tx.isActive()) {
			tx.rollback();
		    }
		    pm.close();
		}
		return detached;
    }
    
    public List<Person> getPilots(){
		return getCategoryOfFlightcrew("pilot");
    }

    public List<Person> getCopilots(){
		return getCategoryOfFlightcrew("copilot");
    }

    public List<Person> getHostStaff(){
    	return getCategoryOfFlightcrew("hoststaff");
    }

    private Person checkUser(String login){
    	Person detached = null;
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
		    tx.begin();
		    Person user = pm.getObjectById(Person.class, login);
		    detached = (Person)pm.detachCopy(user);
		    tx.commit();
		} catch (javax.jdo.JDOObjectNotFoundException e){
			logger.error("Aucune ligne correspondante trouvée dans la base",e);
		}
		finally{
		    if (tx.isActive())
		    	tx.rollback();
		    pm.close();
		}
		return detached;
    }

    public String getSalt(Person person){
    	Person detached = checkUser(person.getLogin());
		if(detached == null)
			return null;

		return detached.getSalt();
    }

    public Person checkUser(Person person){
    	Person detached = checkUser(person.getLogin());
		if(detached == null)	
			return null;
    	String hashClient = person.getHash();
    	String hashServer = toMD5("" + detached.getHash() + detached.getSalt() + person.getSalt());
    	logger.debug(hashClient + "\n" + hashServer);

		return hashClient.equals(hashServer) ? detached : null;
    }

	private void addPersons(List<Person> persons){
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			pm.makePersistentAll(persons);	
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}

	private List<Person> importPersons(String pathFileBase){
		List<Person> persons = new ArrayList<Person>();
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
			Person a = new Person(s);
			persons.add(a);
		}
		return persons;
	}
}