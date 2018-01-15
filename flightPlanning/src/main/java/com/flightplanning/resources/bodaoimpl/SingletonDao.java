package com.flightplanning.resources.bodaoimpl;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class SingletonDao{
	private static volatile PersistenceManagerFactory pmf = null;

	private SingletonDao(){
	}

	public static synchronized
	PersistenceManagerFactory getPersistenceManagerFactory(){
		if (pmf == null){ 
			pmf = JDOHelper.getPersistenceManagerFactory("FlightPlanning");
		}
		return pmf;
	}
}