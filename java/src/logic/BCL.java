package logic;

import java.util.ArrayList;

import multicore_exp.Param;
import data.SporadicTask;
import data.Task;
import data.TaskSet;

public class BCL {
	public static int getNumSchedulableTasks(ArrayList<SporadicTask>  taskSet) {
		
		ArrayList<SporadicTask>   newSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			newSet.add(taskSet.get(i));
			if (!isSchedulable(newSet))
				return i;
		}
		
		return taskSet.size();
	}
	
	
	public static boolean isSchedulable2(ArrayList<SporadicTask> taskSet)
	{
		for (int k=0; k<taskSet.size(); k++)
		{
			SporadicTask t_k = taskSet.get(k);
			
			double sum = 0;			
			for (int i=0; i<taskSet.size(); i++)
			{
				if (i==k)
					continue;

				SporadicTask t_i = taskSet.get(i);
				sum += Math.min( t_k.getDeadline() - t_k.getExecutionTime() , Math.floor(t_k.getDeadline()/t_i.getPeriod()) + Math.min(t_i.getExecutionTime(), t_k.getDeadline() % t_i.getPeriod()));
			}
			
			if (sum > Param.NumProcessors * (t_k.getDeadline() - t_k.getExecutionTime()))
				return false;
		}
		return true;
	}
	public static double getBeta(SporadicTask task_i, SporadicTask task_k)
	{
		double N_i = Math.floor( (task_k.getDeadline() - task_i.getDeadline())/ task_i.getPeriod() ) + 1;
		
		double NC = N_i * task_i.getExecutionTime();
		double min = Math.min(task_i.getExecutionTime(), Math.max(0, task_k.getDeadline() - N_i * task_i.getPeriod()) );
		
		return (NC + min) / task_k.getDeadline();
	}
	
	public static boolean isSchedulable(ArrayList<SporadicTask>  taskSet)
	{		
		for(int k=0; k<taskSet.size(); k++)
		{
			double sum = 0;

			SporadicTask task_k = taskSet.get(k);
			for(int i=0; i<taskSet.size(); i++)
			{
				if (k==i) continue;
				
				SporadicTask task_i = taskSet.get(i);

				sum += Math.min(getBeta(task_i, task_k), 1 - task_k.getExecutionOverDeadline());
			}
			
			if (sum > Param.NumProcessors * (1 - task_k.getExecutionOverDeadline()))
			{	
				return false; 
			}
			else if (sum == Param.NumProcessors * (1 - task_k.getExecutionOverDeadline()))
			{
				boolean bExist = false;
				for (int h=0; h<taskSet.size(); h++)
				{
					if (k==h) continue;
					double I_h_k = getBeta(taskSet.get(h), task_k);
					
					if (I_h_k > 0 && I_h_k <= task_k.getDeadline() - task_k.getExecutionTime())
						bExist = true;
				}
				
				if (bExist) continue;
				
				return false;
			}	
		}
		
		return true;
	}
}
