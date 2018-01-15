package test;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob {
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException{
		// TODO Auto-generated method stub
		try{
            Thread.sleep(1000);
            System.out.println("test");
        }catch (Exception e) {
            throw new JobExecutionException(e);
        }
	}
}
