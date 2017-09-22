package period_harmonizer;


import java.util.ArrayList;

import tool.Plot;
import generator.TaskGeneratorSingleSegment;
import logic.Fluid;
import logic.PhaseShiftingHarmonic;
import multicore_exp.Param;
import multicore_exp.Util;
import data.PhasedTask;
import data.SporadicTask;
import data.Task;
import data.TaskSet;
import data.TaskSet.ParallelizedOption;
import deadline_control.TaskInfo;

public class PeriodHarmonizer {

	
	protected static void setHarmonizedPeriod(Task task, int harmonizedPeriod)
	{
		task.setRealPeriod(task.getPeriod());
		task.setRealDeadline(task.getDeadline());
		task.setPeriod(harmonizedPeriod);
//		System.out.printf("Set %d-> %d as %d\n", task.getTaskID(), task.getRealPeriod(), harmonizedPeriod);
		if (harmonizedPeriod < task.getDeadline())
			task.setDeadline(harmonizedPeriod);
	}
	
	public long getGCD(TaskSet taskSet)
	{
		long GCD = taskSet.get(0).getPeriod();
		for (int i = 1; i < taskSet.size(); i++)
		{
			GCD = Util.getGCD(GCD, taskSet.get(i).getPeriod());
		}

		return GCD;
	}
	
	public boolean harmonize(TaskSet taskSet)
	{
		return harmonize(taskSet, TaskSet.ParallelizedOption.SINGLE);
	}
	
	public boolean harmonize(ArrayList<TaskInfo> taskSet)
	{
		return intervalSearch(taskSet);

	
	}

	public boolean intervalSearch(ArrayList<TaskInfo> taskSet)
	{
		IntervalSearchBackward intervalBackward = new IntervalSearchBackward();
		boolean ret;		
		ret = intervalBackward.backwardInterval(taskSet);
		if (!ret) return false;
		for (int i = 0; i < taskSet.size(); i++)
		{
			if (taskSet.get(i).getHarmonizedPeriod() == 0) return false;
		}
		return true;
		
	}
	public boolean harmonize(TaskSet taskSet, TaskSet.ParallelizedOption option)
	{
		IntervalSearchBackward intervalBackward = new IntervalSearchBackward();
		boolean ret;
		ret = intervalBackward.backwardInterval(taskSet, option);
		if (!ret) return false;
		for (int i = 0; i < taskSet.size(); i++)
		{
			if (taskSet.get(i).getPeriod() == 0) return false;
		}
		return true;
//		exponentialHarmonize(taskSet);
	}
	
	public void exponentialHarmonize(TaskSet taskSet)
	{
		long GCD = getGCD(taskSet);
		
		for (int i = 0; i < taskSet.size(); i++)
		{
			int period = taskSet.get(i).getPeriod();
			int periodMultiplier = period / (int)GCD;
					
			double newPeriodExponential = Math.floor(Math.log(periodMultiplier) / Math.log(2));
			double newPeriod = GCD * (int)Math.pow(2, newPeriodExponential);

			setHarmonizedPeriod(taskSet.get(i), (int)newPeriod);
//			System.out.println("++++" + Math.log(periodMultiplier) + "\t" + Math.log(2) + "\t" + Math.log(periodMultiplier) / Math.log(2));
//			System.out.println("----" + GCD + "\t" + periodMultiplier + "\t" + newPeriodExponential);
//			System.out.println(period + "\t" + newPeriod + "\t" + taskSet.get(i).getDeadline());
		}
//		if (taskSet.isValid())
//			System.out.println("---------------------------------");
//		else
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
	
	public static void test1()
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		generator.SetMaxNumThreads(4);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			numTask += taskSet.size();
			
			boolean ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
			System.out.println("PS start");
			boolean ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
			System.out.println("PS + PH start");
			boolean ret2 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
			
			System.out.println(ret1 + "\t" + ret2 + "\t" + taskSet.isValid());
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	
	public static void test2()
	{
		Param.NumProcessors = Param.NumThreads_MAX = 2;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			
			boolean ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
			System.out.println("PS start");
			boolean ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
			System.out.println("PS + PH start");
			boolean ret2 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
			
			System.out.println(ret1 + "\t" + ret2 + "\t" + taskSet.isValid());
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test3()
	{
		Param.NumProcessors = Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			
			boolean ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
			System.out.println("PS start");
			boolean ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
			System.out.println("PS + PH start");
			boolean ret2 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
			
			System.out.println(ret1 + "\t" + ret2 + "\t" + taskSet.isValid());
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test4()
	{
		Param.NumProcessors = Param.NumThreads_MAX = 2;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			
			boolean ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
			System.out.println("PS start");
			boolean ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
			System.out.println("PS + PH start");
			boolean ret2 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
			
			System.out.println(ret1 + "\t" + ret2 + "\t" + taskSet.isValid());
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test5()
	{
		Param.NumProcessors = 2;
		Param.NumThreads_MAX = 2;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,5);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

//		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000L);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			
			boolean ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
			System.out.println("PS start");
			boolean ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
			System.out.println("PS + PH start");
			boolean ret2 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
			
			System.out.println(ret1 + "\t" + ret2 + "\t" + taskSet.isValid());
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test6()
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		
		boolean ret0, ret1 , ret2;
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000L);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			harmonizer25.modify(taskSet);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
				continue;
			
				
			
			
			ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));
			
//			System.out.println("PS start");
			ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
//			System.out.println("PH start");
			harmonizer.harmonize(taskSet);
//			System.out.println("PS + PH start");
			ArrayList<PhasedTask> phasedTask = taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT);
			ret2 = taskSet.isValid();
			
			//TODO: isSchedulable argument is modified to get Task class
			if (ret2)
				ret2 = PhaseShiftingHarmonic.isSchedulable(phasedTask, Param.NumProcessors);
			
			
			System.out.println(i + " : " + ret0 + "\t" + ret1 + "\t" + ret2);
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test6_2()
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.1, 1.1);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

//		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		
		boolean ret0, ret1 , ret2;
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000L);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			harmonizer25.modify(taskSet);
			numTask += taskSet.size();
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ArrayList<SporadicTask> taskSet_s2 = taskSet.toSporadicTask(i, ParallelizedOption.MAX);
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			if (u > Param.NumProcessors)
			{
				ret0 = ret1 = ret2 = false;
			}
			else
			{
			
			
				
			
			
				ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));

				//			System.out.println("PS start");
				ret1 = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
				//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
				//			System.out.println("PH start");
				harmonizer.harmonize(taskSet);
				//			System.out.println("PS + PH start");
				ArrayList<PhasedTask> phasedTask = taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT);
				ret2 = taskSet.isValid();

				//TODO: isSchedulable argument is modified to get Task class
				if (ret2)
					ret2 = PhaseShiftingHarmonic.isSchedulable(phasedTask, Param.NumProcessors);
			}
			
			
			System.out.println(i + "\t" + ret0 + "\t" + ret1 + "\t" + ret2);
			
			if (ret0) cnt0 ++;
			if (ret1) cnt1 ++;
			if (ret2) cnt2 ++;
			
			if (!ret1 && ret2) cnt3++;
			if (ret1 && !ret2) cnt4++;
			
		}
		System.out.println(cnt0 + "\t" + cnt1 + "\t" + cnt2 + "\tFT:" + cnt3 + "\tTF:" + cnt4);
		System.out.println(numTask / (double)iter);
	}
	public static void test7()
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt1 = 0, cnt2 = 0;
		int cnt3 = 0, cnt4 = 0;
		
		int numTask = 0;

		generator.setFixedAlpha(0);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		
		boolean ret0, ret1 , ret2;
		int iter = 100;
		double u1_avg = 0, u2_avg = 0;
		double d_avg = 0;
		int fail = 0;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000L);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			harmonizer25.modify(taskSet);
			numTask += taskSet.size();
			
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			double u1 = 0, u2 = 0;
			
			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u1 += taskSet_s1.get(j).getExecutionOverPeriod();
				d_avg += taskSet_s1.get(j).getExecutionOverDeadline();
			}
			harmonizer.harmonize(taskSet);
			
			if (!taskSet.isValid()) 
			{
				fail ++;
				System.out.printf("%f \t-> fail\n", u1);
				continue;
			}
			

			taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);

			for (int j = 0; j < taskSet_s1.size(); j++)
			{
				u2 += taskSet_s1.get(j).getExecutionOverPeriod();
			}
			
			System.out.printf("%f \t-> %f\n", u1, u2);
			u1_avg += u1;
			u2_avg += u2;
		}
		System.out.printf("reuslt : (%d / %d) success\n", iter - fail, iter);
		System.out.printf("averge util. : %f \t-> %f\n", u1_avg / (iter - fail), u2_avg / (iter - fail));
		System.out.printf("averge density : %f\n", d_avg / iter);
		System.out.printf("averge numTask: %f", numTask / (double)iter);
	}
	
	
	
	public static void test8()
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.1, 1.2);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,10);
		
		int cnt0 = 0;
		int cnt_PS = 0, cnt_PH_PS = 0;
		int cnt_PS_DC = 0, cnt_PH_PS_DC = 0;
		int cnt_FT = 0, cnt_TF = 0;
		
		int numTask = 0;
		int numTaskScheduled = 0;
		int cntScheduled = 0;

//		generator.setFixedAlpha(0);
//		generator.setFixedGamma(0.1);
		
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
		TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		
		Plot plotter = new Plot();
		
		boolean ret0, ret_PS , ret_PH_PS, ret_PS_DC, ret_PH_PS_DC;
		boolean reth1, reth2;
		int iter = 100;
		for (int i = 0; i < iter; i ++)
		{
//			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 1000000L);
			TaskSet taskSet = generator.GenerateTaskSet(i, i);
			TaskSet taskSet2 = generator.GenerateTaskSet(i, i);
//			harmonizer25.modify(taskSet);
			numTask += taskSet.size();
//			System.out.println(taskSet);
			
			double u = 0;
			ArrayList<SporadicTask> taskSet_s1 = taskSet.toSporadicTask(i, ParallelizedOption.SINGLE);
			ret0 = ret_PS = ret_PH_PS = false;
			reth1 =  reth2 = false;
			if (taskSet_s1 != null)
			{
				for (int j = 0; j < taskSet_s1.size(); j++)
				{
					u += taskSet_s1.get(j).getExecutionOverPeriod();
				}
			}
			if (u > Param.NumProcessors)
			{
				ret0 = ret_PS = ret_PH_PS = ret_PS_DC = ret_PH_PS_DC = false;
			}
			else
			{
				ret0 = Fluid.isSchedulable(taskSet.toSporadicTask(i, ParallelizedOption.SINGLE));

				//			System.out.println("PS start");
				ret_PS = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.SINGLE), Param.NumProcessors);
				ret_PS_DC = PhaseShiftingHarmonic.isSchedulable(taskSet.toPhasedTask(ParallelizedOption.BEST_EFFORT), Param.NumProcessors);
				//			ArrayList<PhasedTask> phasedTaskSet = taskSet.toHarmonicPhasedTask(TaskSet.ParallelizedOption.SINGLE);
				//			System.out.println("PH start");
				reth1 = harmonizer.harmonize(taskSet, ParallelizedOption.SINGLE);
				reth2 = harmonizer.harmonize(taskSet2, ParallelizedOption.BEST_EFFORT);
//				System.out.println(taskSet);
//				System.out.println(taskSet2);
				//			System.out.println("PS + PH start");
				ArrayList<PhasedTask> phasedTask_PH_PS = taskSet.toPhasedTask(ParallelizedOption.SINGLE);
				ArrayList<PhasedTask> phasedTask_PH_PS_DC = taskSet2.toPhasedTask(ParallelizedOption.BEST_EFFORT);
				

				ret_PH_PS = taskSet.isValid();
				ret_PH_PS_DC = taskSet2.isValid();

				//TODO: isSchedulable argument is modified to get Task class
				if (ret_PH_PS)
					ret_PH_PS = PhaseShiftingHarmonic.isSchedulable(phasedTask_PH_PS, Param.NumProcessors);
				if (ret_PH_PS_DC)
				{
					ret_PH_PS_DC = PhaseShiftingHarmonic.isSchedulable(phasedTask_PH_PS_DC, Param.NumProcessors);
					String dataFilename = String.format("plot/data%d", i);
					String outputFilename = String.format("%s.eps", dataFilename);
					String scriptFilename = String.format("plot/%d.plt", i);
//					plotter.drawGNUPlot(dataFilename, outputFilename, phasedTask_PH_PS_DC);
					plotter.drawGNUPlotWithScript(scriptFilename, 
							dataFilename, outputFilename, phasedTask_PH_PS_DC); 
					
				}
			}
			
			
			System.out.printf("%d\t%b\t%b\t%b\t%b\t%b\tH1: %b\tH2: %b\n", i, ret0, ret_PS, 
					ret_PH_PS, ret_PS_DC, ret_PH_PS_DC, 
					reth1, reth2);
//			System.out.println(i + "\t" + ret0 + "\t" + ret_PS + "\t" + ret_PH_PS + "\tharmonization : " + reth);
			
			if (ret0) cnt0 ++;
			if (ret_PS) cnt_PS ++;
			if (ret_PH_PS) cnt_PH_PS ++;
			if (ret_PS_DC) cnt_PS_DC++;
			if (ret_PH_PS_DC) cnt_PH_PS_DC++;
			
			if (ret0 || ret_PS || ret_PH_PS || ret_PS_DC || ret_PH_PS_DC)
			{
				numTaskScheduled += taskSet.size();
				cntScheduled ++;
			}
			
			if (!ret_PS && ret_PH_PS) cnt_FT++;
			if (ret_PS && !ret_PH_PS) cnt_TF++;
			
		}
		System.out.printf("\t%d\t%d\t%d\t%d\t%d\tFT:%d\tTF:%d\n", cnt0, cnt_PS, 
				cnt_PH_PS, cnt_PS_DC, cnt_PH_PS_DC, cnt_FT, cnt_TF);
		System.out.printf("\tF\tPS\tPH_PS\tPS_DC\tPH_PS_DC\n");
//		System.out.println(cnt0 + "\t" + cnt_PS + "\t" + cnt_PH_PS + "\tFT:" + cnt_FT + "\tTF:" + cnt_TF);
		System.out.println(numTask / (double)iter);
		System.out.println(numTaskScheduled / (double)cntScheduled);
	}
	public static void main(String[] args)
	{
		test8();
	}

}
