package logic;

import java.util.ArrayList;
import java.util.Collections;

import multicore_exp.Param;
import multicore_exp.Util;
import data.PhasedTask;

public class PhaseShiftingHarmonic {
	
	public enum option
	{
		L_GAMMA_FIRST,
		L_DENSITY_FIRST,
		L_DEADLINE_FIRST,
		S_PERIOD_FIRST
	}
	protected static ArrayList<ArrayList<PhasedTask>> makeHarmonicGroups(ArrayList<PhasedTask> taskSet)
	{
		ArrayList<ArrayList<PhasedTask>> groups = new ArrayList<ArrayList<PhasedTask>>();
		
		// sort tasks by increase order of period
		for (int i = 0; i < taskSet.size(); i++)
		{

			for (int j = i + 1; j < taskSet.size(); j++)
			{
				if (taskSet.get(i).getPeriod() > taskSet.get(j).getPeriod())
					Collections.swap(taskSet, i, j);
			}
		}
		
/*
		System.out.println("\nSort Info");
		for (int i = 0; i < taskSet.size(); i++)
		{
			System.out.println(taskSet.get(i).getPeriod() + "\t" + 
					taskSet.get(i).getExecutionTime() + ", " + taskSet.get(i).getDeadline() + ", "+ taskSet.get(i).getPeriod());
		}
		System.out.println("----------");
*/
		

		for (int i = 0; i < taskSet.size(); i++)
		{
			int groupIndex = -1;
			PhasedTask task = taskSet.get(i);
			
			for (int j = groups.size() - 1; j >= 0; j--)
			{
				int lastIndex = groups.get(j).size() - 1;
				if (task.getPeriod() % groups.get(j).get(lastIndex).getPeriod() == 0)
				{
					groupIndex = j;
					break;
				}
			}
			
			if (groupIndex < 0)
			{
				groups.add(new ArrayList<PhasedTask>());
				groupIndex = groups.size() - 1;
			}
			
			groups.get(groupIndex).add(task);
		}
		return groups;
	}
	
	protected static long getLCM(ArrayList<PhasedTask> taskSet)
	{
		long LCM = 1;
		for (int i = 0; i < taskSet.size(); i++)
		{
			LCM = Util.getLCM(LCM, (int)taskSet.get(i).getPeriod());
		}
		return LCM;
	}
	protected static long getLCM(ArrayList<PhasedTask> taskSet, PhasedTask task)
	{
		return Util.getLCM(getLCM(taskSet), (int)task.getPeriod());
	}
	
	protected static long getGCD(ArrayList<PhasedTask> taskSet, PhasedTask task)
	{
		long GCD = Util.getGCD((long)task.getDeadline(), (long)task.getPeriod());
		
		for (int i = 0; i < taskSet.size(); i++)
		{
			GCD = Util.getGCD(GCD, (long)taskSet.get(i).getPeriod());
			GCD = Util.getGCD(GCD, (long)taskSet.get(i).getDeadline());
		}
		return GCD;
	}

	
		
	
	// this function moves the given task one by one on top of given base Tasks
	// it returns phase that minimizes total peak density
	// baseTasks not includes task
	protected static int shiftOneTask(ArrayList<PhasedTask> baseTasks, PhasedTask task)
	{
		if (baseTasks.size() == 0) return 0;
		long LCM = getLCM(baseTasks, task);
		long GCD = getGCD(baseTasks, task);

		int period = (int)task.getPeriod();
		int deadline = (int)task.getDeadline();
		double taskDensity = task.getExecutionOverDeadline();
		double minPeakDensity = Double.MAX_VALUE;
		int optimalPhase = 0;
		
		
//		int expectedCount = (int) (period / GCD) * (int) (LCM / period);
		
		for (int phase = 0; phase < period; phase += GCD)
		{
			task.setPhase(phase);
			double peakDensity = 0;
			for (int jobIdx = 0; jobIdx < LCM / task.getPeriod(); jobIdx++)
			{
				long releasetime = phase + jobIdx * period;
				for (long t = releasetime; t < releasetime + deadline; t++)
				{
					double density = 0;
					for (int i = 0; i < baseTasks.size(); i++)
					{
						density += baseTasks.get(i).getInstanceDensity(t);
					}
					if (density > peakDensity)
						peakDensity = density;
				}
			}
			if (peakDensity < minPeakDensity)
			{
				minPeakDensity = peakDensity;
				optimalPhase = phase;
			}
		}
		
		return optimalPhase;
	}
	
	protected static double getPeakDensity(ArrayList<PhasedTask> taskSet)
	{
		long LCM = getLCM(taskSet);
		double peakDensity = 0;
		
		for (long t = 0; t < LCM; t++)
		{
			double density = 0;
			for (int i = 0; i < taskSet.size(); i++)
			{
				density += taskSet.get(i).getInstanceDensity(t);
			}
			if (density > peakDensity) 
				peakDensity = density;
		}
		
		return peakDensity;
	}
	
	protected static double getPeakDensityShiftOneTask(ArrayList<PhasedTask> taskSet)
	{
		ArrayList<PhasedTask> baseTasks = new ArrayList<PhasedTask>();
		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			int optimalPhase = shiftOneTask(baseTasks, task);
			task.setPhase(optimalPhase);
			baseTasks.add(task);
//			System.out.printf("%d : %d + (%d, %d, %d)\t  %f\n", 
//					task.taskID, optimalPhase, 
//					(int)task.getPeriod(), 
//					(int)task.getExecutionTime(), 
//					(int)task.getDeadline(), 
//					getPeakDensity(baseTasks));
		}
		
		return getPeakDensity(baseTasks);
	}
	
	public static double getPeakDensitySmallestPeriodFirst(ArrayList<PhasedTask> taskSet)
	{
		// sort descending order of density
		for (int i = 0; i < taskSet.size(); i++)
		{
			double period1 = taskSet.get(i).getPeriod();
			for (int j =  i + 1; j < taskSet.size(); j++)
			{
				double period2 = taskSet.get(j).getPeriod();
				if (period1 > period2)
				{
					PhasedTask temp = taskSet.get(i);
					taskSet.set(i, taskSet.get(j));
					taskSet.set(j, temp);
				}
			}
		}
		
		return getPeakDensityShiftOneTask(taskSet);
	}
	public static double getPeakDensityLargestDeadlineFirst(ArrayList<PhasedTask> taskSet)
	{
		// sort descending order of density
		for (int i = 0; i < taskSet.size(); i++)
		{
			double deadline1 = taskSet.get(i).getDeadline();
			for (int j =  i + 1; j < taskSet.size(); j++)
			{
				double deadline2 = taskSet.get(j).getDeadline();
				if (deadline1 < deadline2)
				{
					PhasedTask temp = taskSet.get(i);
					taskSet.set(i, taskSet.get(j));
					taskSet.set(j, temp);
				}
			}
		}
		
		return getPeakDensityShiftOneTask(taskSet);
	}
	public static double getPeakDensityLargestDensityFirst(ArrayList<PhasedTask> taskSet)
	{
		// sort descending order of density
		for (int i = 0; i < taskSet.size(); i++)
		{
			double density1 = taskSet.get(i).getExecutionOverDeadline();
			for (int j =  i + 1; j < taskSet.size(); j++)
			{
				double density2 = taskSet.get(j).getExecutionOverDeadline();
				if (density1 < density2)
				{
					PhasedTask temp = taskSet.get(i);
					taskSet.set(i, taskSet.get(j));
					taskSet.set(j, temp);
				}
			}
		}
		
		return getPeakDensityShiftOneTask(taskSet);
	}
	public static double getPeakDensityLargestGammaFirst(ArrayList<PhasedTask> taskSet)
	{
		// sort descending order of gamma
		for (int i = 0; i < taskSet.size(); i++)
		{
			double gamma1 = taskSet.get(i).getDeadline() / taskSet.get(i).getPeriod();
			for (int j =  i + 1; j < taskSet.size(); j++)
			{
				double gamma2 = taskSet.get(j).getDeadline() / taskSet.get(j).getPeriod();
				if (gamma1 < gamma2)
				{
					PhasedTask temp = taskSet.get(i);
					taskSet.set(i, taskSet.get(j));
					taskSet.set(j, temp);
				}
			}
		}
		
		return getPeakDensityShiftOneTask(taskSet);
	}
	
	public static double getMinPeakDensity(ArrayList<PhasedTask> taskSet, option option)
	{
		ArrayList<ArrayList<PhasedTask>> harmonicGroups = makeHarmonicGroups(taskSet);
		double peakDensity = 0;
		
		

//		System.out.println("\nGroup Size = " + harmonicGroups.size());
		for (int i = 0; i < harmonicGroups.size(); i++)
		{

//			System.out.print("\tgroup " + (i + 1) + " size = " + harmonicGroups.get(i).size());
//			for (int j = 0; j < harmonicGroups.get(i).size(); j++)
//			{
//				System.out.print("\t" + harmonicGroups.get(i).get(j).getPeriod());
//			}
//			System.out.println();

			
			
			double groupDensity = 0;
			
			if (harmonicGroups.get(i).size() == 1)
				groupDensity = harmonicGroups.get(i).get(0).getExecutionOverDeadline();
			else
			{
				switch(option)
				{
				case L_GAMMA_FIRST:
					groupDensity = getPeakDensityLargestGammaFirst(harmonicGroups.get(i));
					break;
				case L_DENSITY_FIRST:
					groupDensity = getPeakDensityLargestDensityFirst(harmonicGroups.get(i));
					break;
				case L_DEADLINE_FIRST:
					groupDensity = getPeakDensityLargestDeadlineFirst(harmonicGroups.get(i));
					break;
				case S_PERIOD_FIRST:
					groupDensity = getPeakDensitySmallestPeriodFirst(harmonicGroups.get(i));
					break;
				}
			}
			peakDensity += groupDensity;
		}
//		System.out.println("PeakDensity = " + peakDensity);

		return peakDensity;
	}
	
	public static boolean isSchedulable(ArrayList<PhasedTask> taskSet)
	{
		int numProcessors = Param.NumProcessors;
		return isSchedulable(taskSet, numProcessors, option.L_GAMMA_FIRST);
	}
	public static boolean isSchedulable(ArrayList<PhasedTask> taskSet, int numProcessors)
	{
		return isSchedulable(taskSet, numProcessors, option.L_GAMMA_FIRST);
	}
		
	public static boolean isSchedulable(ArrayList<PhasedTask> taskSet, int numProcessors, option option)
	{
		if (taskSet == null) return false;
		if (getMinPeakDensity(taskSet, option) <= numProcessors) return true;
		return false;
	}
	
	
/*	

	static double getPeakDensity(TaskSet taskSet)
	{
		ArrayList<ScheduleBlock> blocks = new ArrayList<ScheduleBlock>();
		
		for (int i = 0; i < taskSet.size(); i++)
		{
			int deadline, period;
			double density;

			Task task = taskSet.get(i);
	
			deadline = (int)task.getDeadline();
			period = (int)task.getPeriod();			
			
			density = rtas.new TaskSnippets(task).getOptimalDensity();		// calculate density from RTAS method
			
			ScheduleBlock block = new ScheduleBlock(deadline, period, density);			
			blocks.add(block);
			
//			double totalDensity = BlockScheduler.getPeakDensity(blocks,  BlockScheduler.Algorithm.LDF);
//			System.out.println("Block Scheduler num task set = " + (i + 1) + "new block density = " + density + " " + deadline + " " + period + " " + totalDensity);
//			if ( totalDensity > Param.NumProcessors) return i;
			
		}
		
	
		File f = new File(taskSet.getTaskSetID() + ".txt");
		
		try {						
			BufferedWriter bw  = new BufferedWriter(new FileWriter(f));
			
			for (int i = 0; i < blocks.size(); i++)
			{
				bw.write(blocks.get(i).toString());
				bw.newLine();
			}	
			
			bw.close();
					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return taskSet.size();	
		
	}
*/
}
