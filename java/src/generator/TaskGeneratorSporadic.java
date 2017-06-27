package generator;

import java.util.ArrayList;
import data.SporadicTask;
import multicore_exp.Param;
import multicore_exp.Util;

public class TaskGeneratorSporadic extends TaskGenerator {
	
	public ArrayList<SporadicTask> RandomGenerateSporadic(int taskSetID, int seed)
	{
		ArrayList<SporadicTask> taskSet = new ArrayList<>();
		Util util = new Util(seed);
		
		int NumTasks = util.randomInt(2, taskNum);
		for (int i=0; i<NumTasks; i++)
		{
			alpha = util.randomDouble(alpha_from, alpha_to);
			beta = util.randomDouble(beta_from, beta_to);

			int period = (int)util.randomInt((int)Param.Period_MIN, (int)Param.Period_MAX);
			int deadline = Math.max(1, Math.min(period, (int)Math.round(period * gamma)));
			int executionTime = Math.max(1, Math.min(deadline, (int)Math.round(deadline * beta)));
			
			//double deadline = Math.max(1, Math.min(period, period * gamma));
			//double executionTime = Math.max(1, Math.min(deadline, deadline * alpha));
			//double executionTime = Math.min(deadline, deadline * alpha);
				
			SporadicTask t = new SporadicTask(period, executionTime, deadline);
			t.taskSetID = taskSetID + 1;
			t.taskID = i + 1;
			taskSet.add(t);
		}
		
		return taskSet;
	}
}
