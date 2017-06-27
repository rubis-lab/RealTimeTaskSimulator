package logic;

import java.util.ArrayList;
import multicore_exp.Util;
import data.PhasedTask;

public class PhaseShiftingNaive {
	
	protected static double minPeak;
	protected static double progressTotalCount;
	protected static double progressCount;
	
	
	
	public static boolean isSchedulable(ArrayList<PhasedTask> taskSet, int numProcessors)
	{
		double ret = getMinimumPeakDensity(taskSet);
		if (ret <= numProcessors)
			return true;
		return false;
	}
	
	private static long getLCM(ArrayList<PhasedTask> taskSet)
	{
		long LCM = 1;
		for (int i = 0; i < taskSet.size(); i++)
		{
			LCM = Util.getLCM(LCM, (long)taskSet.get(i).getPeriod());
		}
		
		return LCM;
	}
	
	// before calling getPeakDensity, setLCM should have be called.
	protected static double getPeakDensity(ArrayList<PhasedTask> taskSet, long LCM)
	{
		double maxDensitySum = 0;
		for (int t = 0; t < LCM; t++)
		{
			double densitySum = 0;
			for (int i = 0; i < taskSet.size(); i++)
			{
				densitySum += taskSet.get(i).getInstanceDensity(t);
			}
			if (densitySum > maxDensitySum)
				maxDensitySum = densitySum;
		}
		
		return maxDensitySum;
	}
	
	public static double getMinimumPeakDensity(ArrayList<PhasedTask> taskSet)
	{
		double ret = shiftRecursive(taskSet);
		
		return ret;
	}
	
	public static double shiftRecursive(ArrayList<PhasedTask> taskSet)
	{
		minPeak = -1;
		progressCount = 0;
		
		long LCM = getLCM(taskSet);
		
		taskSet.get(0).setPhase(0);
		
		for (int i = 0; i < taskSet.size(); i++)
		{
			if (i == 0) progressTotalCount = 1;
			else progressTotalCount *= taskSet.get(i).getPeriod();
		}

		shiftRecursive(taskSet, LCM, 1);
		
		return minPeak;
	}
	
	public static void shiftRecursive(ArrayList<PhasedTask> taskSet, long LCM, int taskIndex)
	{
		if (taskIndex == taskSet.size())
		{
			double peakDensity = getPeakDensity(taskSet, LCM);
			if (minPeak < 0 || peakDensity < minPeak)
				minPeak = peakDensity;
			
			return;
		}
		
		for (int i = 0; i < taskSet.get(taskIndex).getPeriod(); i++)
		{
			taskSet.get(taskIndex).setPhase(i);
			shiftRecursive(taskSet, LCM, taskIndex + 1);
		}
	}


			
	
	
/*	
	public static int getNumSchedulableTasks(TaskSet taskSet) {
		RTAS rtas = new RTAS();
		
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
	
	public static double getPeakDensity(TaskSet taskSet)
	{
		RTAS rtas = new RTAS();
		
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
		}	
		return BlockScheduler.getPeakDensity(blocks,  BlockScheduler.Algorithm.LDF);
	
	}
	
	
	

	public static void test1()
	{
		int iterations = 100;

		// Generate Task
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setRandomAlpha(0, 1);
		taskGenerator.setRandomBeta(0.0, 15);
		taskGenerator.setFixedGamma(5.0);
		taskGenerator.setFixedTaskNum(50);
		System.out.println("RTAS alpha experiment");
		System.out.println(String.format("alpha\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
		for (double i = 0; i <= 1.0; i += 0.2)
		{
			taskGenerator.setFixedAlpha(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_psNaive = 0;
			
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet taskSet = taskGenerator.GenerateTaskSet(j,  j);
				//taskGenerator.WriteToFile(taskSet);
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
				nSchedulable_psNaive += PhaseShiftingNaive.getNumSchedulableTasks(taskSet);
				
			}
			nSchedulable /= 500;
			nSchedulable_single /= 500;
			nSchedulable_random /= 500;
			nSchedulable_max /= 500;
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_psNaive));
			
		}
	}
	public static void test2()
	{
		int iterations = 100;

		// Generate Task
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setRandomAlpha(0, 1);
		
		taskGenerator.setRandomBeta(0.0, 15);
		taskGenerator.setFixedGamma(5.0);
		taskGenerator.setFixedTaskNum(50);	

		System.out.println("\nRTAS beta experiment");		
		System.out.println(String.format("beta\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
		for (double i = 0; i <= 1.8; i += 0.2)
		{
			taskGenerator.setFixedBeta(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;		
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
	
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet taskSet = taskGenerator.GenerateTaskSet(j,  j);
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);

				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;		
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;	
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));			
		}
	}
	
	public static void test3()
	{
		int iterations = 5;
		int seed = 2;

		// Generate Task
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setFixedAlpha(0.0);
		taskGenerator.setRandomBeta(1.0, 15);
		taskGenerator.setFixedGamma(2.0);
		taskGenerator.setFixedTaskNum(50);
		
		TaskSet taskSet2 = taskGenerator.GenerateTaskSet(0,  0);
		for (int i = 0; i < taskSet2.size(); i++)
		{
			System.out.println(taskSet2.get(i).getPeriod());
		}
//		return;
		System.out.println("PSN alpha experiment");
		System.out.println(String.format("alpha\tours\tN_S\tN_R\tN_M\tS\tPS_naive"));
		int taskSetID = 0;
		for (double i = 0; i <= 1.0; i += 0.2)
		{
						
			taskGenerator.setFixedAlpha(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_psNaive = 0;
			double nSchedulable_shin = 0;
			
			
			for (int j = 0; j < iterations; j++)
			{
				System.out.print(".");
				TaskSet taskSet = taskGenerator.GenerateTaskSet(taskSetID ++,  j);
				//taskGenerator.WriteToFile(taskSet);
				
//				PhaseShiftingNaive.taskSetRegulation(taskSet);
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet) / (double)iterations;
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE) / (double)iterations;
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM) / (double)iterations;
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX) / (double)iterations;

				SHIN.SelectOptionRandom(taskSet);				
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet) / (double)iterations;
				
				nSchedulable_psNaive += PhaseShiftingNaive.getNumSchedulableTasks(taskSet) / (double)iterations;
				
			}
			System.out.println("");
			 
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_psNaive));
			
		}
		
	}
	
	public static void taskSetRegulation(TaskSet taskSet)
	{
		int basePeriod = 100;
		for (int i = 0; i < taskSet.size(); i++)
		{
			int period = taskSet.get(i).getPeriod();
			period = ((int)(period / basePeriod)) * basePeriod;			
			while(taskSet.get(i).getDeadline() > period)
				period += basePeriod;
			
			taskSet.get(i).setPeriod(period);
		}		
	}
	
	public static void test4()
	{
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setFixedAlpha(0.2);
		taskGenerator.setRandomBeta(1.0, 4.0);
		taskGenerator.setFixedGamma(1.5);
		taskGenerator.setFixedTaskNum(4);
		
		//System.out.println("alpha experiment : phase-shifting exhaustive search");
		//taskGenerator.setFixedAlpha(0.5);
		TaskSet taskSet = taskGenerator.GenerateTaskSet(1, 1);
		
		ExhaustiveSearch exhaustiveSearch = new ExhaustiveSearch(taskSet);
		exhaustiveSearch.doExhaustiveSearch();		
	}
	
	public static void test5()
	{
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setFixedAlpha(0.0);
		taskGenerator.setRandomBeta(1.0, 3.0);
		taskGenerator.setFixedGamma(2.0);
		taskGenerator.setFixedTaskNum(3);
		
		System.out.println("PSN alpha experiment");
		System.out.println(String.format("alpha\tours\tN_S\tN_R\tN_M\tPS_naive\tPS_exhaustive"));
		
		
		int[] seed = {1,13,18,36,38,39,47};
		int iterations = seed.length;
		
		int taskSetID = 0;
		for (double i = 0; i <= 1.0; i += 0.2)
		{
						
			taskGenerator.setFixedAlpha(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_psNaive = 0;
			double nSchedulable_psExhaustive = 0;
//			double nSchedulable_shin = 0;
			
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet taskSet = taskGenerator.GenerateTaskSet(taskSetID ++,  seed[j]);
				//taskGenerator.WriteToFile(taskSet);
				
//				PhaseShiftingNaive.taskSetRegulation(taskSet);
				
				double densityRTAS = RTAS.getPeakDensity(taskSet);
				double densityNS = NBG.getPeakDensity(taskSet, NBG.Type.SINGLE);
				double densityNR = NBG.getPeakDensity(taskSet, NBG.Type.RANDOM);
				double densityNM = NBG.getPeakDensity(taskSet, NBG.Type.MAX);
				double densityPS_N = PhaseShiftingNaive.getPeakDensity(taskSet);
				
				ExhaustiveSearch exhaustiveSearch = new ExhaustiveSearch(taskSet);
				double densityPS_E = exhaustiveSearch.doExhaustiveSearch();
				
				nSchedulable += densityRTAS / (double)iterations;
				nSchedulable_single += densityNS / (double)iterations;
				nSchedulable_random += densityNR / (double)iterations;
				nSchedulable_max += densityNM / (double)iterations;
				nSchedulable_psNaive += densityPS_N / (double)iterations;
				nSchedulable_psExhaustive += densityPS_E / (double)iterations;
				
//				SHIN.SelectOptionRandom(taskSet);				
//				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet) / (double)iterations;
				
				
				System.out.println(String.format("   %d\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
						j, densityRTAS, densityNS, densityNR, densityNM, densityPS_N, densityPS_E));
				
				
			}			 
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_psNaive, nSchedulable_psExhaustive));
			
		}
		
	}
	*/

	      

	

	public static void main(String[] args) {
		System.out.println("Start phase shifting naive");
		
		
		System.out.println("End");
	}
		
	
	
}
