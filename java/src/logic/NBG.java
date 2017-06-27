package logic;

import multicore_exp.Param;
import multicore_exp.TaskSetPool;
import multicore_exp.Util;
import data.Task;
import data.TaskSet;

public class NBG {
	
	public enum Type {MAX, SINGLE, RANDOM, CUSTOM};
	



	public static boolean isSchedulable(TaskSet taskSet, Type type)
	{
		if (getNumSchedulableTasks(taskSet, type) == taskSet.size()) 
			return true;
		return false;	
	}
	
	public static boolean isSchedulable(TaskSetPool taskPool, Type type)
	{
		if (getNumSchedulableTaskSets(taskPool, type) == taskPool.size()) 
			return true;
		return false;
	}
	
	public static int getNumSchedulableTasks(TaskSet taskSet, Type type)
	{
		TaskSet subTaskSet = new TaskSet();
		
		for (int i = 0; i < taskSet.size(); i++)
		{			
			Task task = taskSet.get(i);
			subTaskSet.add(task);
			if (getPeakDensity(subTaskSet, type) > Param.NumProcessors) return i;
		}
		return taskSet.size();		
	}
	
	public static int getNumSchedulableTaskSets(TaskSetPool taskPool, Type type)
	{
		int success = 0;
		for (int i = 0; i < taskPool.size(); i++)
		{
			TaskSet taskSet = taskPool.get(i);
			
			if (isSchedulable(taskSet, type))
				success ++;				
		}
		
		return success;
	}
	
	public static int Check(TaskSetPool taskPool, Type type)
	{
		return getNumSchedulableTaskSets(taskPool, type);
	}
	
	
	public static boolean Check(TaskSet taskSet) {
		return false;
	}
	
	public static double getPeakDensity(TaskSet taskSet, Type type)
	{
		Util util = new Util(0);
		double worstCaseTotalDensity = 0;		
		
		for (int i = 0; i < taskSet.size(); i++)
		{			
			Task task = taskSet.get(i);
			for (int j = 0; j < task.getNumSegments(); j++)
			{
				if (type == Type.CUSTOM)
					break;
				
				int option = 1;
				switch(type)
				{
					case SINGLE: 	option = 1; break;
					case MAX:		option = Param.NumProcessors; break;
					case RANDOM:	option = util.randomInt(1,  Param.NumProcessors); break;
				}
				task.selectOption(j, option - 1);			
			}		
			
			double remainingExecutionTime = task.getTotalExecutionTime();
			double remainingDeadline = task.getDeadline();
			double peakDensity = 0;
			for (int j = 0; j < task.getNumSegments(); j++)
			{
				double remainingSegmentDensity = remainingExecutionTime / remainingDeadline;
				double totalExecutionTime = task.getTotalExecutionTime(j);
				double minDeadline = task.getMaxExecutionTimeOfSegment(j);
				double segmentMaxDensity = totalExecutionTime / minDeadline;
				double deadline;
				if (segmentMaxDensity < remainingSegmentDensity)
				{
					// intermediate deadline is shorter than C^max
					// set intermediate deadline as minimum. and then managing other segments
					
					deadline = minDeadline;					
				}
				else
				{
					deadline = totalExecutionTime / remainingSegmentDensity; 
				}
				
				task.setIntermediateDeadline(j, (int)deadline);
				
				remainingExecutionTime -= totalExecutionTime;
				remainingDeadline -= deadline;
				
				double density = totalExecutionTime / deadline;
				if (density > peakDensity)
					peakDensity = density;
			}
			
			worstCaseTotalDensity += peakDensity;
			if (task.getMaxExecutionTime() > task.getDeadline()) return Param.NumProcessors * 3;

		}
		
		return worstCaseTotalDensity;		

	}

}
