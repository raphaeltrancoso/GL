package com.flightplanning.resources.bo;

import java.util.ArrayList;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class Crew{
    private String loginPilot;
    private String loginCopilot;
    private ArrayList<String> loginHostStaff;

    public Crew(){
        // No need to create an empty instance of Crew
    }
    
    public Crew(String lpilot, String lCopilot, ArrayList<String> lHostStaff){
    	loginPilot = lpilot;
    	loginCopilot = lCopilot;
    	loginHostStaff = lHostStaff;
    }

    // Getters
    public boolean concerned (String login){
    	if (loginPilot.equals(login)||loginCopilot.equals(login)){
    		return true;
    	}
    	else{
    		return loginHostStaff.contains(login);
    	}
    }
    
    public String getLoginPilot(){
    	return loginPilot;
	}

    public String getLoginCopilot(){
    	return loginCopilot;
    }

    public ArrayList<String> getLoginHostStaff(){
    	return loginHostStaff;
    }

    // Setters
    public void setLoginPilot(String p){
    	this.loginPilot = p;
    }

    public void setLoginCopilot(String p){
    	this.loginCopilot = p;
    }

    public void setLoginHostStaff(ArrayList<String> p){
    	this.loginHostStaff = new ArrayList<String>(p);
    }
}
