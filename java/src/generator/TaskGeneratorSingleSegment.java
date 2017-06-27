package generator;


import multicore_exp.Util;
import data.TaskSet;

public class TaskGeneratorSingleSegment extends TaskGenerator {
	
	public TaskGeneratorSingleSegment()
	{
		kmin = kmax = 1;
	}
	
	public TaskSet GenerateTaskSetWithLimitedLCM(int taskSetID, int seed, long maxLCM)
	{
		Util util = new Util(seed);
		
		TaskSet taskSet;
		long iter = 0;
		boolean ret;
		
		do
		{
			
			int baseSeed = util.randomInt(0, Integer.MAX_VALUE / 2);
			taskSet = GenerateTaskSet(alpha, beta, gamma, taskNum, taskSetID, baseSeed);
			iter ++;
			
			if (iter % 100000 == 0)
			{
				System.out.println("GenerateTaskSetWithLimitedLCM iterates " + iter + " times for taskSetID " + 
								taskSetID + " with maxLCM=" + maxLCM + ". Consider greater maxLCM value.");
			}
			try
			{
				ret = (Util.getTaskSetLCM(taskSet) > maxLCM);
			}
			catch(Exception e)
			{
				ret = true;
			}
		} while (ret);
		
		return taskSet;
	}	
	
	public void SetMaxNumThreads(int numThreadMax)
	{
		omax = numThreadMax;
	}
	
	
	public static void main(String[] args)
	{
		/* test codes for TaskGeneratorSingleSegment Class*/
		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,100);
		generator.SetMaxNumThreads(2);
		
		
		for (double gamma = 0.1; gamma <= 1.0; gamma += 0.1)
		{
			generator.setFixedGamma(gamma);
			for (int iter = 0; iter < 100; iter ++)
			{
				// now we can generate task set whose LCM is smaller than specified value. 

				TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(iter, iter, 50);

				System.out.print(taskSet.size() + " / ");
				for (int i = 0; i < taskSet.size(); i++)
				{
					System.out.print("(" + taskSet.get(i).getDeadline()
							+ ", " + taskSet.get(i).getPeriod() + ") ");
				}
				System.out.println();
			}
		}
	}

}
