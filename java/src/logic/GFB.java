package logic;

import java.util.ArrayList;

import multicore_exp.Param;
import data.SporadicTask;
import data.Task;
import data.TaskSet;

public class GFB {
	public static int getNumSchedulableTasks(ArrayList<SporadicTask> taskSet) {
		
		ArrayList<SporadicTask> newSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			newSet.add(taskSet.get(i));
			if (!isSchedulable(newSet))
				return i;
		}
		
		return taskSet.size();
	}

	public static double getMaxLambda(ArrayList<SporadicTask> taskSet)
	{
		double maxLambda = 0;

		for(int i=0; i<taskSet.size(); i++)
		{
			SporadicTask t = taskSet.get(i);
			double lambda = t.getExecutionOverDeadline();
			
			maxLambda = Math.max(maxLambda, lambda);
		}

		return maxLambda;
	}
	
	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{
		double maxLambda = getMaxLambda(taskSet);
		
		double sum = 0;
		for(int i=0; i<taskSet.size(); i++)
		{
			sum += taskSet.get(i).getExecutionOverDeadline();
		}
		
		return sum <= Param.NumProcessors * (1 - maxLambda) + maxLambda;
	}
}
