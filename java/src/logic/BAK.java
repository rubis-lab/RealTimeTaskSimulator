package logic;

import java.util.ArrayList;

import multicore_exp.Param;
import data.SporadicTask;
import data.Task;
import data.TaskSet;

public class BAK {
	public static int getNumSchedulableTasks(ArrayList<SporadicTask> taskSet) {	
		ArrayList<SporadicTask>  newSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			newSet.add(taskSet.get(i));
			if (!isSchedulable(newSet))
				return i;
		}
		
		return taskSet.size();
	}
	
	public static double getBeta(SporadicTask task_i, SporadicTask task_k)
	{
		double lambda_k = task_k.getExecutionOverDeadline();
		double U_i = task_i.getExecutionOverPeriod();
		
		double leftTerm = U_i * (1 + (task_i.getPeriod() - task_i.getDeadline()) / task_k.getDeadline() );
		if (lambda_k >= U_i)
		{
			return leftTerm;
		}

		return leftTerm + (task_i.getExecutionTime() - lambda_k * task_i.getPeriod()) / task_k.getDeadline();
	}
	
	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{		
		for(int k=0; k<taskSet.size(); k++)
		{
			SporadicTask task_k = taskSet.get(k);
			double lambda_k = task_k.getExecutionOverDeadline();

			double sum = 0;
			for(int i=0; i<taskSet.size(); i++)
			{
				SporadicTask task_i = taskSet.get(i);
				sum += Math.min(1, getBeta(task_i, task_k));
			}
			
			if (sum > Param.NumProcessors * (1 - lambda_k) + lambda_k)
				return false;
		}
		
		return true;
	}
}
