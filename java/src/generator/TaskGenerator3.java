package generator;

import multicore_exp.*;
import period_harmonizer.PeriodModifier;
import data.*;

public class TaskGenerator3 extends TaskGenerator2{

	public TaskGenerator3(PeriodModifier periodModifier) {
		super(periodModifier);
		// TODO Auto-generated constructor stub
	}

	public Task GenerateTask(double alpha, double beta, double gamma, int taskID, int seed)
	{
		boolean allOptionsSameIntermediateDeadline = true;
		Util util = new Util(seed);
		int numSegments = util.randomInt(kmin, kmax); 	
		
		int sumOneThreads = 0;
		Task task = new Task(numSegments);
		
		int period = util.randomInt((int)Param.Period_MIN, (int)Param.Period_MAX);
		period = periodModifier.modifyPeriod(period);
		
		if (beta <= 0) beta = 0.1;
		if (beta > Param.NumProcessors) beta = Param.NumProcessors;
		int totalExecutionTime = (int) (period * beta);
		
		if (gamma <= 0) gamma = 0.1;
		int deadline = (int) (totalExecutionTime * gamma);
		if (deadline > period) deadline = period;
		
		task.setDeadline(deadline);
		task.setRealDeadline(deadline);
		task.setPeriod(period);
		task.setRealPeriod(period);
		
		int[] fractionalExecutionTime = new int[numSegments];
		int fractionalExecutionTimeSum = 0;
		for (int i = 0; i < numSegments; i++)
		{
			fractionalExecutionTime[i] = util.randomInt(scmin, scmax);
			fractionalExecutionTimeSum += fractionalExecutionTime[i];
		}
		
		for (int j = 0; j < numSegments; j++)
		{
			int wcetOneThread = (int)
					(fractionalExecutionTime[j]  / (double)fractionalExecutionTimeSum * totalExecutionTime);
			for(int k = 0; k<omax; k++)
			{
				int Oik = k + 1;
				int wcetEachThread = (int)Math.ceil((alpha * wcetOneThread) +( (1 - alpha) * wcetOneThread / Oik));
				if(wcetEachThread == 0) wcetEachThread = 1;
				
				for(int x = 0; x < Oik ; x++)
					task.setExecutionTime(j,  k,  x, wcetEachThread);
			}
		}
		sumOneThreads = totalExecutionTime;
		
		
		
		if(allOptionsSameIntermediateDeadline == true)
		{
			for(int i= 0 ; i < task.getNumSegments() ; i++)
			{
				task.setIntermediateDeadline(i, (int) (sumOneThreads / beta));
			}
		}
		
		
		task.setTaskID(taskID);
		task.setInformation(seed, scmin, scmax, omax, kmin, kmax);
		
		return task;
		
	}

}
