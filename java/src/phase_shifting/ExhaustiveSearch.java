package phase_shifting;

import generator.TaskGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import multicore_exp.Param;
import data.OptionDeadline;
import data.Task;
import data.TaskReader;
import data.TaskSet;

class Solution
{
	double minPeakDensity;
	int[] optimalOption;
	double[] optimalIntrDeadline;
	double optimalShift;
	double optimalT;	
	boolean schedulability;
}

class TaskOItdPh
{
	public double[] densities;
	public int option;
	public int intermediateDeadline;
	public int phase;
	
	public TaskOItdPh(int n)
	{
		double[] d = new double[n];
		this.densities = d;
	}
	
}

public class ExhaustiveSearch {
	public TaskSet taskset;
	
	public ExhaustiveSearch(TaskSet taskset)
	{
		this.taskset = taskset;
	}
	
	public int lcm(int a, int b)
	{
		int max,min,x;
		int lcm = 1;
		
		if(a>b)
		{
			max=a;
			min=b;
		}
		else
		{
			max=b;
			min=a;
		}
		for(int i=1;i<=min;i++)
		{
			x=max*i; //finding multiples of the maximum number
			if(x%min==0) //Finding the multiple of maximum number which is divisible by the minimum number.
			{
				lcm=x; //making the 1st multiple of maximum number as lcm, which is divisible by the minimum number
				break; //exiting from the loop, as we don’t need anymore checking after getting the LCM
			}
		}
		return lcm;
	}
	
	public int getLCMOfFirstNTasks(int n)
	{
		int num1 = 1;
		int lcmOfPeriods = 1;
				
		for(int i=0 ; i < n ; i++)
		{
			lcmOfPeriods = lcm(num1, (int)(this.taskset.get(i).getPeriod()));
			num1 = lcmOfPeriods;
		}
		return lcmOfPeriods;
	}
	
	public static ArrayList<OptionDeadline> getOptionDeadlineComb(int segmentNum, int threadNum, Task curTask, int a)
	{
		int total = (int) Math.pow(threadNum, segmentNum);
		ArrayList<OptionDeadline> optionDeadlineList = new ArrayList<OptionDeadline>();
		int[] jalisu = new int[segmentNum];
		
		for(int i=0 ; i<segmentNum ; i++)
			jalisu[i] = (int) Math.pow(threadNum, i);
		
		for(int i = 0; i<total;i++)
		{
			int[] x = new int[segmentNum];

			for(int j=0;j<segmentNum;j++)
			{
				if(j==0)
					x[j] = i % threadNum;
				else
				{
					x[j] = i / jalisu[j];
					if(x[j] >= threadNum)
						x[j] = x[j] % threadNum;
				}	
			}
			OptionDeadline optionDeadline = new OptionDeadline();
			optionDeadline.optionComb = x;
			//optionDeadlineList.add(optionDeadline);
			
			
			//ArrayList<double[]> intermediateDeadlineList = new ArrayList<double[]>();
			ArrayList<int[]> intermediateDeadlineList = new ArrayList<int[]>();
			//double changeAmount = 0.0;
			int changeAmount = 0;
			while(true)
			{
				//double[] intermediateDeadlines = new double[segmentNum];
				int[] intermediateDeadlines = new int[segmentNum];
				for(int j=0 ; i < segmentNum ; i++)
					intermediateDeadlines[j] = 0;
				
				double sum = 0.0;
				
				for(int j=0 ; j<segmentNum; j++)
				{
					intermediateDeadlines[j] = (int) curTask.getMaxExecutionTimeOfSegment(j , x[j]);
					intermediateDeadlines[j] += changeAmount;
					sum += intermediateDeadlines[j];
				}
				if(sum > curTask.getDeadline())
				{
					// when initial sum exceeds task's deadline, deadline assignment is impossible 
					if(changeAmount == 0)
						intermediateDeadlines = null;
					
					break;
				}
				
				intermediateDeadlineList.add(intermediateDeadlines);
				changeAmount++;	
			}
			
			optionDeadline.intrDeadlineComb = intermediateDeadlineList;
			
			optionDeadlineList.add(optionDeadline);

		}

		return optionDeadlineList;
	}
	
	
	
	public double doExhaustiveSearch()
	{		
		// (1) For all tasks in taskset, find all cases of combinations of option & intermediate deadlines 
		
		for (int a=0; a<this.taskset.getTaskNum() ; a++)
		{
			//System.out.println("[Task " + a + "] ---get all combinations of options and intermediate deadlines");
			
			ArrayList<int[]> optionComb = new ArrayList<int[]>();
			Task curTask = this.taskset.get(a);
			double curDeadline = curTask.getDeadline();
			int segmentNum = curTask.getNumSegments();
			int threadNum = Param.NumProcessors;
			
			// task의 모든 가능한 option combination 조합 : [o1,o2,o3...] [o1,o2,o3...] [o1,o2,o3...] ...
			//curTask.optionCombList = getOptionDeadlineComb(segmentNum, threadNum);
			curTask.optionDeadline = getOptionDeadlineComb(segmentNum, threadNum, curTask, a);
		}
			
		
		// (2) Using (1) Do search : 0 ~ 0, 0 ~ 1, 0 ~ 2 ... 0 ~ N-1
		// Save the found optimal solution
		
		//for(int taskN = 1; taskN < this.taskset.getTaskNum()+1 ; taskN++)
		//{
			int taskN = this.taskset.getTaskNum();
			
			double minPeakDensity = Double.POSITIVE_INFINITY;
			int[] optimalOption = new int[this.taskset.get(0).getNumSegments()];
			double[] optimalIntrDeadline = new double[this.taskset.get(0).getNumSegments()];
			double optimalShift = Double.POSITIVE_INFINITY;
			double optimalT = Double.POSITIVE_INFINITY;	
			boolean schedulability = false;
				
				
			int lcmOfTaskPeriods = getLCMOfFirstNTasks(taskN);
			//System.out.println(lcmOfTaskPeriods);
			
			
			
			
			//int lcmOfTaskPeriods = getLCMOfFirstNTasks(this.taskset.getTaskNum());
			ArrayList<ArrayList<TaskOItdPh>> lists = new ArrayList<ArrayList<TaskOItdPh>>();	
			
	//		for(int taski = 0; taski < this.taskset.getTaskNum() ; taski++)
			for(int taski = 0; taski < taskN ; taski++)			
			{
				double shiftAmount = this.taskset.get(taski).getPeriod(); //this.taskset.get(taski).getDeadline()
				ArrayList<TaskOItdPh> datalist = new ArrayList<TaskOItdPh>();
				
				for(int o=0; o<this.taskset.get(taski).optionDeadline.size(); o++)
				{
					for(int itd = 0; itd < this.taskset.get(taski).optionDeadline.get(o).intrDeadlineComb.size() ; itd++)
					{
						// when intermediate deadline assigning is impossible, jump 
						if(this.taskset.get(taski).optionDeadline.get(o).intrDeadlineComb.get(itd) == null)
							continue;
						
						for(int ph = 0; ph < shiftAmount ; ph++)
						{
							TaskOItdPh data = new TaskOItdPh(lcmOfTaskPeriods);
							
							double[] densities = new double[lcmOfTaskPeriods];
																			
							for(int time = 0 ; time < lcmOfTaskPeriods ; time++)
							{
								densities[time] = this.taskset.get(taski).getDesityAtTAndOffset(o, itd, ph, time);
								data.densities[time] = densities[time];
								
								//if(data.densities[time] < 0.31 && data.densities[time] != 0.0)
								//	return;
								
								//System.out.print(data.densities[time] + " ");
								
								
							}
							//System.out.println();
							
							//System.out.println(lcmOfTaskPeriods);
							
							data.option = o;
							data.intermediateDeadline = itd;
							data.phase = ph;
							
							datalist.add(data);
						}
					}
				}
				lists.add(datalist);
			}
			
			/*for(int i = 0 ; i < lists.size() ; i++)
			{
				for(int j = 0; j < lists.get(i).size() ; j++)
				{
					System.out.println("[Task " + i + "] " + "[jth " + j + "]");
					for(int k = 0; k < lists.get(i).get(j).densities.length; k++)
					{
						System.out.print(lists.get(i).get(j).densities[k] + " ");
					}
					System.out.println();
				}
				System.out.println();
			}*/
			
			
			int result_length = lists.get(0).get(0).densities.length;
			double[] result = recursiveFunction(lists, 0);
			
			
			//System.out.println("check");
			
			//if(getMaxPeakDensity(result) > Param.NumProcessors)
			//	break;
			
			/*System.out.println("============================================================");
			System.out.println("# of schedulable tasks : " + taskN + "\tlcmOfTaskPeriods : " + lcmOfTaskPeriods);
			for(int n = 0 ; n<result.length ; n++)
			{
				System.out.print(result[n] + " ");
			}
			System.out.println();
			System.out.println("peak density : " + getMaxPeakDensity(result));
			System.out.println("============================================================");
			System.out.println();
			System.out.println();*/

			
			
		//}
			
		return getMaxPeakDensity(result);
	}
	
	

	
/*	public double[] recursiveFunction(ArrayList<ArrayList<TaskOItdPh>> lists, int taskid) 
	{
		ArrayList<TaskOItdPh> taskCases = lists.get(taskid);
				
		if(taskid == lists.size()-1)
		{
			double minPeakDensity = Double.POSITIVE_INFINITY;
			double[] minDensities = new double[taskCases.get(0).densities.length];
			double[] accumDensities = new double[taskCases.get(0).densities.length];
			int index = 0;
			
			for(int i = 0 ; i<taskCases.size() ; i++)
			{
				if(getMinPeakDensity(taskCases.get(i).densities) < minPeakDensity)
				{
					minPeakDensity = getMinPeakDensity(taskCases.get(i).densities);
					
					index = i;
				}
			}
			
			for(int i = 0; i<minDensities.length ; i++)
				minDensities[i] = taskCases.get(index).densities[i];
			
			for(int i = 0; i<minDensities.length ; i++)
			{
				accumDensities[i] = minDensities[i];
				//System.out.print("[" + taskCases.get(index).densities[i] +"]");
			}
			//System.out.println();
			
			
			return accumDensities;
		}
		else
		{
			double minPeakDensity = Double.POSITIVE_INFINITY;
			double[] minSumDensities = new double[taskCases.get(0).densities.length];
			double[] accumDensities = new double[taskCases.get(0).densities.length];
			
			for(int i = 0 ; i<taskCases.size() ; i++)
			{
				double[] first = taskCases.get(i).densities;
				double[] second = recursiveFunction(lists, taskid+1);
				
				double[] sum = new double[taskCases.get(i).densities.length];
				for(int j = 0 ; j < taskCases.get(i).densities.length ; j++)
					sum[j] = first[j] + second[j];				
					//sum[j] = taskCases.get(i).densities[j] + recursiveFunction(lists, taskid+1)[j];
				
				if(getMinPeakDensity(sum) < minPeakDensity)
				{
					System.out.print(" minPeakDensity " + minPeakDensity);
					
					minPeakDensity = getMinPeakDensity(sum);
					
					for(int j = 0; j<minSumDensities.length ; j++)
						minSumDensities[j] = sum[j];
					
					
					//System.out.println("minPeakDensity " + minPeakDensity);
					
				}
			}
			
			for(int i = 0; i<minSumDensities.length ; i++)
				accumDensities[i] = minSumDensities[i];
			
			return accumDensities;
		}	
	}*/
	
	
	public double[] recursiveFunction(ArrayList<ArrayList<TaskOItdPh>> lists, int taskid) 
	{
		ArrayList<TaskOItdPh> taskCases = lists.get(taskid);
				
		if(taskid == lists.size()-1)
		{
			double minSumOfDensity = Double.POSITIVE_INFINITY;
			double minPeakDensity = Double.POSITIVE_INFINITY;
			double[] minDensities = new double[taskCases.get(0).densities.length];
			double[] accumDensities = new double[taskCases.get(0).densities.length];
			int index = 0;
			
			for(int i = 0 ; i<taskCases.size() ; i++)
			{
				if(getMaxPeakDensity(taskCases.get(i).densities) < minPeakDensity)
				{
					//if(getSumOfDensity(taskCases.get(i).densities) < minSumOfDensity)
					//{
						//minSumOfDensity = getSumOfDensity(taskCases.get(i).densities) ;
						
						minPeakDensity = getMaxPeakDensity(taskCases.get(i).densities);
						index = i;
					//}
				}
			}
			//System.out.println(minPeakDensity + " " + index);
			
			for(int i = 0; i<minDensities.length ; i++)
				minDensities[i] = taskCases.get(index).densities[i];
			
			for(int i = 0; i<minDensities.length ; i++)
			{
				accumDensities[i] = minDensities[i];
				//System.out.print("[" + taskCases.get(index).densities[i] +"]");
			}
			//System.out.println();
			
			
			return accumDensities;
		}
		else
		{
			double minSumOfDensity = Double.POSITIVE_INFINITY;
			double minPeakDensity = Double.POSITIVE_INFINITY;
			double[] minSumDensities = new double[taskCases.get(0).densities.length];
			double[] accumDensities = new double[taskCases.get(0).densities.length];
			
			for(int i = 0 ; i<taskCases.size() ; i++)
			{
				double[] first = taskCases.get(i).densities;
				double[] second = recursiveFunction(lists, taskid+1);
				
				double[] sum = new double[taskCases.get(i).densities.length];
				for(int j = 0 ; j < taskCases.get(i).densities.length ; j++)
					sum[j] = first[j] + second[j];				
				
				if(getMaxPeakDensity(sum) < minPeakDensity)
				{
					//if(getSumOfDensity(sum) < minSumOfDensity)
					//{
						//minSumOfDensity = getSumOfDensity(sum) ;
						
						//System.out.print(" minPeakDensity " + minPeakDensity);
						
						minPeakDensity = getMaxPeakDensity(sum);
						
						for(int j = 0; j<minSumDensities.length ; j++)
							minSumDensities[j] = sum[j];
						
						//System.out.println("minPeakDensity " + minPeakDensity);
					//}
				}
			}
			
			for(int i = 0; i<minSumDensities.length ; i++)
				accumDensities[i] = minSumDensities[i];
			
			return accumDensities;
		}	
	}
	

	public double getMinPeakDensity(double[] densities) {
		
		int index = 0;
		double min = Double.POSITIVE_INFINITY;
		for(int i =0; i<densities.length ; i++)
		{
			if(min > densities[i])
			{
				min = densities[i];
				index = i;
			}
		}
		return min;
	}
	
	public double getMaxPeakDensity(double[] densities) 
	{
		int index = 0;
		double max = Double.NEGATIVE_INFINITY;
		for(int i =0; i<densities.length ; i++)
		{
			if(max < densities[i])
			{
				max = densities[i];
				index = i;
			}
		}
		return max;
	}
	
	public double getSumOfDensity(double[] densities) 
	{
		double sum = 0.0;
		for(int i =0; i<densities.length ; i++)
			sum += densities[i];
		
		return sum;
	}

	
	public static void main(String[] args) {
		System.out.println("Start");

		TaskGenerator taskGenerator = new TaskGenerator();
		
		taskGenerator.setFixedAlpha(0.0);
		taskGenerator.setRandomBeta(1.0, 3.0);
		taskGenerator.setFixedGamma(2.0);
		taskGenerator.setFixedTaskNum(2);
		
		
/*		 taskGenerator.setFixedAlpha(0.0);
	      taskGenerator.setRandomBeta(1.0, 15);
	      taskGenerator.setFixedGamma(2.0);
	      taskGenerator.setFixedTaskNum(2);*/
		
		
		//System.out.println("alpha experiment : phase-shifting exhaustive search");
		//taskGenerator.setFixedAlpha(0.5);
		
		
		
	
		
		double temp = 0.0;
		
		for(int seed = 0; seed < 50; seed++)
		{
			if(seed == 1 || seed ==13 ||seed ==18 ||seed ==36 ||seed ==38 ||seed ==39 ||seed ==47)
			//if(seed == 1)	
			{
				
				TaskSet taskSet = taskGenerator.GenerateTaskSet(seed,seed);
				
				ExhaustiveSearch exhaustiveSearch = new ExhaustiveSearch(taskSet);
				temp = exhaustiveSearch.doExhaustiveSearch();
				
		
				System.out.print(temp + " ");
			}
		}
		System.out.println();
		
		
		
		System.out.println("End");
	
		
		
	}
	
	
}
