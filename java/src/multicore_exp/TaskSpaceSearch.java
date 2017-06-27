package multicore_exp;

import java.util.ArrayList;

import logic.EDFEventSimulation;
import logic.Fluid;
import data.SporadicTask;
import generator.TaskGeneratorSporadic2;

public class TaskSpaceSearch {
	public static void main(String args[])
	{
		Param.NumProcessors = 2;
		TaskGeneratorSporadic2 generator = new TaskGeneratorSporadic2();
		int maxValue = 100;
		
		int iter = 10000;
		int cntTotal = 0;
		int cntFluid = 0;
		int cntGEDF = 0;
		
		int start = 0;
		int end = iter;
		if (args.length > 1)
		{
			start = Integer.parseInt(args[0]);
			end = Integer.parseInt(args[1]);
		}
		
		for (int i = start; i < end; i++)
		{
			
			int nTasks = 2;
			boolean retFluid, retGEDF;
			ArrayList<SporadicTask> taskSet;
			while(true)
			{
				taskSet = generator.GenerateTaskSet(i, i, nTasks, maxValue);
				retFluid = Fluid.isSchedulable(taskSet);
				retGEDF = EDFEventSimulation.isSchedulable(taskSet);
				
				if (!retFluid || !retGEDF) break;
				nTasks ++;
			}
//			System.out.println(i + ":" + retFluid + " " + retGEDF);
			if (retFluid != retGEDF)
			{
				if (retFluid) cntFluid ++;
				else cntGEDF++;
				cntTotal++;
				System.out.println("Seed : " + i + "\tnTasks : " + nTasks + "\tFluid : " + retFluid + "\tGEDF : " + retGEDF);
				printTaskSet(taskSet, retFluid, retGEDF);
			}

		}
		System.out.println(cntFluid + "\t" + cntGEDF + "\t" + cntTotal + "//");
	}
	
	public static void printTaskSet(ArrayList<SporadicTask> taskSet, boolean retFluid, boolean retGEDF)
	{
		double avgDP = 0;
		double avgEP = 0;
		double avgED = 0;
		for (int i = 0; i < taskSet.size(); i++)
		{
			int p, e, d;
			p = (int)taskSet.get(i).getPeriod();
			e = (int)taskSet.get(i).getExecutionTime();
			d = (int)taskSet.get(i).getDeadline();
			
			double DP = d / (double) p;
			double EP = e / (double) p;
			double ED = e / (double) d;
			
			avgDP += DP;
			avgEP += EP; 
			avgED += ED;

			if (retFluid) System.out.print("+ ");
			else System.out.print("- ");
			System.out.println(p + "\t" + e + "\t" + d+ "\t" +
					DP + "\t" + EP + "\t" + ED);
		}

		avgDP /= taskSet.size();
		avgEP /= taskSet.size();
		avgED /= taskSet.size();

		if (retFluid) System.out.print("++ ");
		else System.out.print("-- ");
		
		System.out.println(avgDP + "\t" + avgEP + "\t" + avgED);
	}

}
