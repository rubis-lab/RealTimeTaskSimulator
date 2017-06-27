package logic;

import java.util.ArrayList;

import data.SporadicTask;
import multicore_exp.Param;

public class Fluid {

	public static int getNumSchedulableTasks(ArrayList<SporadicTask>  taskSet) {
		
		ArrayList<SporadicTask>   newSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			newSet.add(taskSet.get(i));
			if (!isSchedulable(newSet))
				return i;
			
			taskSet.get(i).schedulableFluid = true;
		}
		
		return taskSet.size();
	}
	

	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{
		if (taskSet == null) return false;
		double totalPeakDensity = 0;
		for(int i=0; i<taskSet.size(); i++)
		{
			SporadicTask t = taskSet.get(i);
			double density = t.getExecutionOverDeadline();
			if (density > 1) return false;
			totalPeakDensity += density;
		}
				
		return totalPeakDensity <= Param.NumProcessors;
	}
}
