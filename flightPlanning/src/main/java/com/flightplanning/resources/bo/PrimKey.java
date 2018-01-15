package com.flightplanning.resources.bo;

public class PrimKey{
	private String date;
	private String oaciDeparture;
	private String commercialNumber;

	public PrimKey(String d, String oaci, String com){
		date = d;
		oaciDeparture = oaci;
		commercialNumber = com;
	}

	public String getDate(){
		return date;
	}

	public String getOaciDeparture(){
		return oaciDeparture;
	}

	public String getCommercialNumber(){
		return commercialNumber;
	}
}
