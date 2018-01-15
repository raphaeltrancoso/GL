package test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

public class RapportsVentesJob implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException{
		// TODO Auto-generated method stub
		try{
            Thread.sleep(1000);
            System.out.println("test");
        }catch (Exception e) {
            throw new JobExecutionException(e);
        }
	}
	
	/*public void execute1(JobExecutionContext context) throws JobExecutionException {
	    JobDataMap map = context.getJobDetail().getJobDataMap();
	    map.getString("userId");
	}*/

}
