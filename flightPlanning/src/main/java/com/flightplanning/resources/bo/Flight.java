package com.flightplanning.resources.bo;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Flight{
    @PrimaryKey
    private long departure;
    @PrimaryKey
    private String oaciDeparture;
    @PrimaryKey
    private String commercialNumber;
    private String oaciDestination;
    private long arrivalTime;
    private String atc;
    private String ofp;
    private String notam;
    private String meteo;
    private String tradeNotice;
    private Crew crew;
    private String idAirplane;

    public Flight(){
    	// No need to create an empty instance of flight
    }
    
    public Flight(String[] data){
    	departure =new Long(data[0]);
    	//departure = new Date(new Long(data[0]));
    	oaciDeparture = data[1];
    	commercialNumber = data[2];
    	oaciDestination = data[3];
    	arrivalTime =new Long(data[4]);
    	//arrivalTime = new Date (new Long (data[4]));
    	atc = data[5];
    	ofp = data[6];
    	notam = data[7];
    	meteo = data[8];
    	tradeNotice = data[9];
    	idAirplane = data[10];
    	ArrayList<String> hostStaff = new ArrayList<String>();
    	for (int i = 13; i < data.length; i++){
    		hostStaff.add(data[i]);
    	}
    	crew = new Crew(data[11], data[12], hostStaff);
    }

    public long getDeparture(){
    	return departure;
    }

    public String getATC(){
    	return atc;
    }

    public String getOFP(){
    	return ofp;
    }

    public String getNOTAM(){
    	return notam;
    }

    public String getMeteo(){
    	return meteo;
    }

    public String getTradeNotice(){
    	return tradeNotice;
    }

    public String getIdAirplane(){
		return idAirplane;
	}

    public String getCommercialNumber(){
    	return commercialNumber;
    }

    public long getArrivalTime(){
		return arrivalTime;
    }

    public String getOaciDeparture(){
    	return oaciDeparture;
    }

    public String getOaciDestination(){
		return oaciDestination;
    }

    public Crew getCrew(){
    	return crew;
    }

    public void setFlight(Flight f){
    	this.atc = f.getATC();
    	this.ofp = f.getOFP();
    	this.notam = f.getNOTAM();
    	this.meteo = f.getMeteo();
    	this.crew = f.getCrew();
    	this.idAirplane = f.getIdAirplane();
    	this.oaciDestination = f.getOaciDestination();
    	this.arrivalTime = f.getArrivalTime();
    }
    
    public ArrayList<String> serializeAttributes(){
    	ArrayList<String> attributes = new ArrayList<String>();
    	attributes.add(Long.toString(departure));
    	attributes.add(oaciDeparture);
    	attributes.add(commercialNumber);
    	attributes.add(oaciDestination);
    	attributes.add(Long.toString(arrivalTime));
    	attributes.add(atc);
    	attributes.add(ofp);
    	attributes.add(notam);
    	attributes.add(meteo);
    	attributes.add(tradeNotice);
    	attributes.add(idAirplane);
    	attributes.add(crew.getLoginPilot());
    	attributes.add(crew.getLoginCopilot());
    	attributes.addAll(crew.getLoginHostStaff());
    	return attributes;
    }
}

