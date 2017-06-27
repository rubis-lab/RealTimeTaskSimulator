package generator;

import java.util.ArrayList;

import multicore_exp.Util;
import data.SporadicTask;

public class TaskGeneratorSporadic2 {
	
	public ArrayList<SporadicTask> GenerateTaskSet(int taskSetID, int seed, int nTasks, int maxValue)
	{
		ArrayList<SporadicTask> taskSet = new ArrayList<SporadicTask>();
		Util util = new Util(seed);
		
		for (int i = 0; i < nTasks; i++)
		{
			int period = util.randomInt(1, maxValue);
			int deadline = util.randomInt(1, period);
			int executionTime = util.randomInt(1, deadline);
			
			SporadicTask task = new SporadicTask(period, executionTime, deadline);
			task.taskID = i + 1;
			task.taskSetID = taskSetID + 1;
			taskSet.add(task);
		}
		
		return taskSet;
	}

}
