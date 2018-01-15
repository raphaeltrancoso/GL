package com.flightplanning.alert;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.flightplanning.resources.bo.Flight;
import com.flightplanning.resources.bo.Person;
import com.flightplanning.resources.bodaoimpl.FlightDaoImpl;
import com.flightplanning.resources.bodaoimpl.PersonDaoImpl;

public class AlertsSystem extends TimerTask{
	
	private PersonDaoImpl pdi = null;
	private FlightDaoImpl fdi = null;

	private List<Person> listePersons = null;
	private List<Flight> listeFlights = null;
	private List<String> listeFlightsEmailAirplaneOrCrewDone = null;
	private List<String> listeFlightsEmailOFPDone = null;

	private static final Logger logger = Logger.getLogger(AlertsSystem.class);

	public AlertsSystem(){
		pdi = new PersonDaoImpl();
		fdi = new FlightDaoImpl();
		listeFlightsEmailOFPDone = new ArrayList<String>();
		listeFlightsEmailAirplaneOrCrewDone = new ArrayList<String>();
	}

	private void checkFiles(){
	
		Calendar toDay = Calendar.getInstance();
		Calendar dateVol = Calendar.getInstance();
		Calendar tmp = Calendar.getInstance();
		listeFlights = fdi.getFlights();
		
		for(int i=0;i<listeFlights.size();i++){
			//get depart du vol
			dateVol.setTimeInMillis(listeFlights.get(i).getDeparture());
			//
			logger.debug(dateVol.getTime().toString());
			
			//
			tmp=(Calendar) dateVol.clone();
			tmp.add(Calendar.DAY_OF_MONTH, -7);
			//
			
			logger.debug(tmp.getTime().toString());
			logger.debug(listeFlights.get(i).getIdAirplane());
			logger.debug(listeFlights.get(i).getCrew().getLoginPilot());
			if(!listeFlightsEmailAirplaneOrCrewDone.contains(listeFlights.get(i).getCommercialNumber()) &&
					(toDay.get(Calendar.YEAR) == tmp.get(Calendar.YEAR)
					&& toDay.get(Calendar.MONTH) == tmp.get(Calendar.MONTH)
					&& toDay.get(Calendar.DAY_OF_MONTH) == tmp.get(Calendar.DAY_OF_MONTH))
					&& (listeFlights.get(i).getIdAirplane().isEmpty()
							|| listeFlights.get(i).getCrew().getLoginPilot().isEmpty()
							|| listeFlights.get(i).getCrew().getLoginCopilot().isEmpty()
							|| listeFlights.get(i).getCrew().getLoginHostStaff().isEmpty()))
			{
				sendEmailAirplaneOrCrew(listeFlights.get(i).getCommercialNumber(),listeFlights.get(i).getATC(),dateVol.getTime().toString());
				listeFlightsEmailAirplaneOrCrewDone.add(listeFlights.get(i).getCommercialNumber());
			}
			
			int time;
			if(toDay.get(Calendar.DAY_OF_MONTH) +1 == dateVol.get(Calendar.DAY_OF_MONTH)){
				tmp=(Calendar) dateVol.clone();
				tmp.add(Calendar.DAY_OF_MONTH, -1);
				time=Math.abs(dateVol.get(Calendar.HOUR_OF_DAY)+12);
			}else{
				time=Math.abs(dateVol.get(Calendar.HOUR_OF_DAY)-12);
			}
			if(!listeFlightsEmailOFPDone.contains(listeFlights.get(i).getCommercialNumber()) &&
					time==toDay.get(Calendar.HOUR_OF_DAY)
					&&(toDay.get(Calendar.YEAR) == dateVol.get(Calendar.YEAR)
							&& toDay.get(Calendar.MONTH) == dateVol.get(Calendar.MONTH)
							&& (toDay.get(Calendar.DAY_OF_MONTH) == dateVol.get(Calendar.DAY_OF_MONTH)
							|| toDay.get(Calendar.DAY_OF_MONTH) == dateVol.get(Calendar.DAY_OF_MONTH)-1))
					&& listeFlights.get(i).getOFP().isEmpty())
			{
				sendEmailOFP(listeFlights.get(i).getCommercialNumber(),listeFlights.get(i).getATC(),dateVol.getTime().toString());
				listeFlightsEmailOFPDone.add(listeFlights.get(i).getCommercialNumber());
			}
		}
	}
	
	private void sendEmailAirplaneOrCrew(String commercialNumber,String atc, String date){
		listePersons = pdi.getPersons();
		for(int i=0;i<listePersons.size();i++){
			if(listePersons.get(i).getPtype().contains("cco") && listePersons.get(i).getEmail()!=null){
				Email.sendMessage("FlightPlanning AlertSystem Flight Airplane or Crew is Missing","The flight commercialNumber "+commercialNumber+" ATC "+atc+" Departure "+date,listePersons.get(i).getEmail());
			}
		}
	}
	
	private void sendEmailOFP(String commercialNumber,String atc, String date){
		listePersons = pdi.getPersons();
		for(int i=0;i<listePersons.size();i++){
			if(listePersons.get(i).getPtype().contains("cco") && listePersons.get(i).getEmail()!=null){
				Email.sendMessage("FlightPlanning AlertSystem OFP is Missing","The flight commercialNumber "+commercialNumber+" ATC "+atc+" Departure "+date,listePersons.get(i).getEmail());
			}
		}
	}

	@Override
	public void run(){
		try {
			logger.debug("DEBUT"); 
            checkFiles();
            logger.debug("FIN"); 
            Thread.sleep(10000); //heure 3,6e+6
        } catch (InterruptedException e) {
        	logger.error(e);
        }
	}
}