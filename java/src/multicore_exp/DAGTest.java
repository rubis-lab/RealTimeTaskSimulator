package multicore_exp;

import java.util.ArrayList;

import data.Task;
import data.TaskSet;
import generator.TaskGenerator;
import generator.TaskGenerator2;
import period_harmonizer.PeriodModifier;

public class DAGTest {

	public DAGTest(int numVertex)
	{		
		this.numVertex = numVertex;
	}
	
	public double getDensity(int seed, ArrayList<ArrayList<Integer>> nodeinfo)
	{
		System.out.println(nodeinfo);
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 1000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = numVertex;
		Param.NumSegments_MAX = numVertex;

	//	PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator generator = new TaskGenerator();

		generator.setRandomBeta(0.3, 2);
	//generator.setFixedBeta(2.0);
		/***********************************/
		generator.setFixedGamma(1.0);
		/***********************************/
		generator.setRandomAlpha(0, 0.5);/**/
		generator.setFixedTaskNum(1);
		
		
		TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
		TaskSet mergedTaskSet = new TaskSet();

		Task task = taskSet.get(0);
		
		Task mergedTask = new Task(nodeinfo.size(), task.getPeriod(), task.getDeadline());
		
		for (int i = 0; i < nodeinfo.size(); i++)
		{
			for (int k = 0; k < task.getNumOptions(); k++)
			{
				int maxExecutionTime = 0;
				for (int l = 0; l < k + 1; l++)
				{
					int executionSum = 0;
					for (int j = 0; j < nodeinfo.get(i).size(); j++)
					{
						int executionTime = task.getExecutionTimeOfThread(nodeinfo.get(i).get(j), k, l);
						if (executionTime > maxExecutionTime) maxExecutionTime = executionTime;

						executionSum += executionTime;
						//executionSum += task.getExecutionTimeOfThread(nodeinfo.get(i).get(j), k, l)* (1 + (j / 10.0));
					}
					mergedTask.setExecutionTime(i, k, l, executionSum);
				}
				mergedTask.segments[i].get(k).maxExecutionTime = maxExecutionTime;
			}
		}
		
		mergedTaskSet.add(mergedTask);
		
//		System.out.println(task);
//		System.out.println(mergedTask);
			
		System.out.println(logic.RTAS.getPeakDensity(mergedTaskSet));		
		System.out.println(logic.RTAS.getPeakDensity(taskSet));		
		
		
	
		return logic.RTAS.getPeakDensity(mergedTaskSet);		
	}
	private int numVertex;
	

	public static void main(String args[])
	{
		ArrayList<ArrayList<Integer>> nodeinfo = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> grp1 = new ArrayList<Integer>();
		ArrayList<Integer> grp2 = new ArrayList<Integer>();
		ArrayList<Integer> grp3 = new ArrayList<Integer>();
		ArrayList<Integer> grp4 = new ArrayList<Integer>();
		ArrayList<Integer> grp5 = new ArrayList<Integer>();
		ArrayList<Integer> grp6 = new ArrayList<Integer>();
		ArrayList<Integer> grp7 = new ArrayList<Integer>();
		ArrayList<Integer> grp8 = new ArrayList<Integer>();
		ArrayList<Integer> grp9 = new ArrayList<Integer>();
		ArrayList<Integer> grp10 = new ArrayList<Integer>();
		
		grp1.add(0);
		grp3.add(1);
		grp5.add(2);
		grp6.add(3);
		grp6.add(4);
		grp2.add(5);
		grp4.add(6);
		grp3.add(7);
		grp2.add(8);
		grp7.add(9);
		

/*
		grp1.add(0);
		grp2.add(1);
		grp3.add(2);
		grp4.add(3);
		grp5.add(4);
		grp6.add(5);
		grp7.add(6);
		grp8.add(7);
		grp9.add(8);
		grp10.add(9);
*/
		
		
		if (grp1.size() > 0) nodeinfo.add(grp1);
		if (grp2.size() > 0) nodeinfo.add(grp2);
		if (grp3.size() > 0) nodeinfo.add(grp3);
		if (grp4.size() > 0) nodeinfo.add(grp4);
		if (grp5.size() > 0) nodeinfo.add(grp5);
		if (grp6.size() > 0) nodeinfo.add(grp6);
		if (grp7.size() > 0) nodeinfo.add(grp7);
		if (grp8.size() > 0) nodeinfo.add(grp8);
		if (grp9.size() > 0) nodeinfo.add(grp9);
		if (grp10.size() > 0) nodeinfo.add(grp10);
		
		DAGTest dagtest = new DAGTest(10);
		double d = dagtest.getDensity(11,nodeinfo);
		System.out.println(d);
	}

}