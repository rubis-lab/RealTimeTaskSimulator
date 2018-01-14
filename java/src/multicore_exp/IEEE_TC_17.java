package multicore_exp;

import java.util.ArrayList;
import java.util.EnumSet;

import logic.PhaseShiftingHarmonic;
import multicore_exp.PhaseShiftTest.Option;
import period_harmonizer.PeriodHarmonizer;
import period_harmonizer.PeriodModifier;
import tool.Plot;
import tool.TimeDensityDistribution;
import data.PhasedTask;
import data.TaskSet;
import data.TaskSet.ParallelizedOption;
import deadline_control.DeadlineControl;
import deadline_control.DeadlineControlRTAS;
import deadline_control.DeadlineControlRTAS2;
import deadline_control.DeadlineControlRTAS3;
import generator.TaskGenerator;
import generator.TaskGenerator2;
import generator.TaskGenerator3;

public class IEEE_TC_17 {

	boolean DEBUG = false;
	boolean DEBUG_TASK = false;
	boolean DEBUG_PLOT = false;
	boolean DEBUG_DC = false;
	
	
	public ExperimentResult runScheduler(TaskGenerator generator, int seed, EnumSet<Option> options)
	{
		
		TaskSet taskSet0 = generator.GenerateTaskSet(seed, seed);
		TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
		TaskSet taskSetH = generator.GenerateTaskSet(seed, seed);

//		int[] basePeriod = new int[]{2,3,5,7,11,13,17,19,23,29,31,37,41,43};
//		PeriodModifier periodModifier = new PeriodModifier(basePeriod);

		
//		periodModifier.modifyPeriod(taskSet0);
//		periodModifier.modifyPeriod(taskSet);
//		periodModifier.modifyPeriod(taskSetH);
		
//		System.out.println(taskSet);
		
		
		
		double peakDensity = -1; 
		
		ArrayList<PhasedTask> phasedTaskSet;
		ExperimentResult result = null;
		ParallelizedOption convertingOption = ParallelizedOption.SINGLE;
		
		// 0. Check MinSum Schedulability
		if (options.contains(Option.SINGLE))
		{
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SINGLE);
			result = new ExperimentResult(phasedTaskSet, options, -1);
			result.invalid = false;
			result.schedulable = false;
			result.refTaskSet = taskSet;
			
			double utilization = 0;
			double density = 0;
			for (int i = 0; i < taskSet.size(); i++)
			{
				double executionTime = taskSet.get(i).getExecutionTimeOfThread(0, 0, 0);
				double deadline = taskSet.get(i).getDeadline();
				double period = taskSet.get(i).getPeriod();
				if (executionTime > deadline ||
						deadline > period)
				{
					result.invalid = true;
					result.schedulable = false;
					return result;
				}

				density += executionTime / deadline;
				utilization += executionTime / period;
			}

			result.peakDensity = density;
			if (density <= Param.NumProcessors)
					result.schedulable = true;
			return result;
		}
		
		if (options.contains(Option.MAXPAL))
		{
			phasedTaskSet = new ArrayList<PhasedTask>();
			peakDensity = 0;
			for (int i = 0; i < taskSet.size(); i++)
			{
				data.Task task = taskSet.get(i);
				int optionIndex = task.getNumOptions() - 1;
				int period = task.getPeriod();
				int executionTime = task.getTotalExecutionTime(0, optionIndex);
				int deadline = task.getDeadline();
				int executionTimeSingle = task.getExecutionTimeOfThread(0, optionIndex, 0);
				PhasedTask phasedTask = new PhasedTask(period, executionTime, deadline);
				phasedTask.linkedTask = task;
				phasedTaskSet.add(phasedTask);
				if (deadline < executionTimeSingle)
				{
					result = new ExperimentResult(null, options, -1);
					result.refTaskSet = taskSet;
					return result;
				}
				peakDensity += executionTime / (double)deadline;
			}
			result = new ExperimentResult(phasedTaskSet, options, peakDensity);
			result.refTaskSet = taskSet;
			return result;
		}
					

		// 1. Check Deadline Control Option
		if (options.contains(Option.DC_SP) ||
			options.contains(Option.DC_H2) ||
			options.contains(Option.DC_H3) ||
			options.contains(Option.DC_H4) ||
			options.contains(Option.DC_H1))
			convertingOption = ParallelizedOption.BEST_EFFORT;

		// 2. Try Period Harmonization using taskSetH
		boolean harmonizable;
		double harmonizationFactor = 0;
		PeriodHarmonizer harmonizer = new PeriodHarmonizer();
//		harmonizable = harmonizer.harmonize(taskSetH, convertingOption);
		harmonizable = harmonizer.harmonize(taskSetH, ParallelizedOption.SINGLE);
		
		// 3. Check Period Harmonization Option
		if (options.contains(Option.PH))
		{
//			ParallelizedOption harmonizerOption = convertingOption;
			ParallelizedOption harmonizerOption = ParallelizedOption.SINGLE;
			
			if (!harmonizable)
			{
				phasedTaskSet = taskSet0.toPhasedTask(harmonizerOption);
				result = new ExperimentResult(phasedTaskSet, options, -1);
				result.harmonizationFail();
				result.refTaskSet = taskSet0;
				return result;
			}
			taskSet = taskSetH;
		}

		// 4. Check integrity after harmonization
		if (!taskSet.isValid())
		{
			phasedTaskSet = taskSet0.toPhasedTask(convertingOption);
			result = new ExperimentResult(phasedTaskSet, options, -1);
			result.invalid = true;
			result.refTaskSet = taskSet0;
			return result;
		}
		phasedTaskSet = taskSet.toPhasedTask(convertingOption);

		// 5. Check Phase Shifting Option
		if (options.contains(Option.PS))
		{
			if (phasedTaskSet == null)
				peakDensity = -1;
			peakDensity = PhaseShiftingHarmonic.getMinPeakDensity(
					phasedTaskSet, PhaseShiftingHarmonic.option.L_GAMMA_FIRST);
		}
		// 6. Check Deadline Control
		if (options.contains(Option.DC_H1))
		{
			if (options.contains(Option.PS))
				System.out.printf("DC_H1 and PS are exclusive. PS result will be ignored.\n");
			DeadlineControl dc = new DeadlineControl();
			dc.DEBUG = DEBUG_DC;
			peakDensity = dc.getPeakDensity(taskSet);
			if (dc.invalid)
			{
				result = new ExperimentResult(phasedTaskSet, options, -1);
				result.invalid = true;
				result.refTaskSet = taskSet;
				return result;
			}
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
		}
		else if (options.contains(Option.DC_H2))
		{
			if (options.contains(Option.PS))
				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");
			DeadlineControlRTAS dc = new DeadlineControlRTAS();
			dc.DEBUG = DEBUG_DC;
			peakDensity = dc.getPeakDensity(taskSet);
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
		}
		else if (options.contains(Option.DC_H3))
		{
			if (options.contains(Option.PS))
				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");
			DeadlineControlRTAS2 dc = new DeadlineControlRTAS2();
			dc.DEBUG = DEBUG_DC;
			peakDensity = dc.getPeakDensity(taskSet);
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
		}
		else if (options.contains(Option.DC_H4))
		{
			if (options.contains(Option.PS))
				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");

			DeadlineControlRTAS3 dc = new DeadlineControlRTAS3();
			dc.DEBUG = DEBUG_DC;
			peakDensity = dc.getPeakDensity(taskSet);
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
		}

			
		
		/*
		System.out.printf("\n");
		for (int i = 0 ; i < taskSet.size(); i++)
		{
			if (phasedTaskSet == null) continue;
			System.out.printf("%d(%d)\t", (int)taskSet.get(i).getPeriod(), (int)phasedTaskSet.get(i).getPeriod());
		}
		System.out.printf("\n");
		*/
		// 7. Make Experiment Result
		result = new ExperimentResult(phasedTaskSet, options, peakDensity);
		result.refTaskSet = taskSet;
		
		// Calculate harmonization factor
		for (int i = 0; i < taskSet.size(); i++)
		{
			int periodH = taskSetH.get(i).getPeriod();
			int period = taskSet.get(i).getPeriod();
//			harmonizationFactor += Math.pow(periodH - period, 2);
			harmonizationFactor += periodH / period;
		}
		result.harmonizable = harmonizable;
//		result.harmonizationFactor = Math.sqrt(harmonizationFactor / taskSet.size());
		result.harmonizationFactor = harmonizationFactor / taskSet.size();
		
		return result;
	}
	public void exp170927_gamma()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		double beta_deviation = 0.1;
		int iter = 5000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setNormalDistributionBeta(beta, beta_deviation);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0.0, 0.3);/**/
		generator.setRandomTaskNum(2,20);
		

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];
		int targetFalse = 0;

		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;

		for (double gamma = 0.1; gamma <= 1.01f; gamma += 0.05)
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
			for (int seed = 0; seed < iter; seed++)
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (result.schedulable)	reason2[seed][i] = 1;
					else if (result.notSchedulableSingleThread) reason2[seed][i] = 2;
					else if (result.notSchedulableMultiThread) reason2[seed][i] = 3;
					else if (result.notSchedulableInvalid) reason2[seed][i] = 4;
					else if (result.notSchedulableHarmonization) reason2[seed][i] = 5;
					else System.out.printf("Warning : result has no reason %d %d\n", seed, i);
						
				}
			}

			System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, gamma);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, gamma);
			resultForGraphReason += String.format("%.3f\t", gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				double resultSize = result.numResult;
				
				System.out.println(result);
				resultForGraph += String.format("%5.3f\t", result.schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", result.peakDensity());
				resultForGraphReason += String.format("%5.3f\t%5.3f\t%5.3f\t%5.3f\t%5.3f\t\t",
						result.schedulability(), 
						result.notSchedulableSingleThread / resultSize,
						result.notSchedulableMultiThread / resultSize,
						result.notSchedulableInvalid / resultSize,
						result.notSchedulableHarmonization / resultSize);
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";
			resultForGraphReason += "\n";
			
			resultForGraphReason2 += String.format("%.1f\t", gamma);
			resultForGraphReason3 += String.format("%.1f\t", gamma);
			int[][] reason = new int[optionSet.size()][5];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
					if (reason2[i][optionSet.size() - 1] == 1 &&
							reason2[i][j] != 1)
					{
						reason[j][reason2[i][j] - 1] ++;
						unschedulable[j] ++;
					}
				}
			}
			for (int i = 0; i < optionSet.size() - 1; i++)
			{
				resultForGraphReason2 += String.format("\t%d\t", unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason2 += 
						String.format("%.3f\t", reason[i][j] / (double)unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason2 += 
						String.format("%d\t", reason[i][j]);
			}
			resultForGraphReason2 += String.format("\n");
			reason = new int[optionSet.size()][5];
			unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size(); j++)
				{
					reason[j][reason2[i][j] - 1] ++;
					unschedulable[j] ++;
				}
			}
			for (int i = 0; i < optionSet.size(); i++)
			{
				resultForGraphReason3 += String.format("\t%d\t", unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason3 += 
						String.format("%.3f\t", reason[i][j] / (double)unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason3 += 
						String.format("%d\t", reason[i][j]);
			}
			resultForGraphReason3 += String.format("\n");

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason2);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason3);
		System.out.printf("Target false = %d\n", targetFalse);
			
	}
	
	public void exp170927_uunifast_gamma()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double betasum = 5;
		int iter = 5000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator3 generator = new TaskGenerator3(periodModifier);

//		generator.setFixedBetaSum(betasum);
		generator.setRandomBetaSum(0.1, betasum);
		generator.setRandomAlpha(0.0, 0.3);/**/
		generator.setRandomTaskNum(2,20);
		

		String resultForGraph = "";

		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;

			
		for (double gamma = 0.2f; gamma <= 3.01f; gamma += 0.2f)
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
//			generator.setNormalDistributionGamma(gamma, 0.1);

			for (int seed = 0; seed < iter; seed++)
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
				}
			}

			System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

			resultForGraph += String.format("%3.2f \t%3.2f \t", betasum, gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				double resultSize = result.numResult;
				
				System.out.println(result);
				resultForGraph += String.format("%5.3f\t", result.schedulability()); 
			}
			resultForGraph += "\n";
		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
	}

	public void exp171008_uunifast_density()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		double beta_deviation = 0.1;
		double betasum = 5.0;
		int iter = 5000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		

		String resultForGraph = "";

		for (int m = 1; m <= 8; m++)
		{
/*****************/
			Param.NumProcessors = m;
			Param.NumThreads_MAX = m;			
			TaskGenerator2 generator = new TaskGenerator2(periodModifier);

//			generator.setNormalDistributionBeta(beta, beta_deviation);
			generator.setRandomBetaSum(0.1, betasum);
			generator.setRandomGamma(0.1, 1.0);
			generator.setRandomAlpha(0, 0.3);/**/
			generator.setRandomTaskNum(2,20);
/*****************/


			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			for (int seed = 0; seed < iter; seed++)
//			int seed = 0;
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
				}
			}

			System.out.printf("\n--------------RESULT m = %d-------------\n", m);

			resultForGraph += String.format("%d\t", m);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				double resultSize = result.numResult;
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
			}
			resultForGraph += "\n";
		}
		
			
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
	}

	public void exp170927_density()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		double beta_deviation = 0.1;
		int iter = 5000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];

		int [][] densitySchedulability = new int[optionSet.size()][128];
		int [][] densitySchedulabilityTotal = new int[optionSet.size()][128];
		int targetFalse = 0;


		for (int m = 1; m <= 8; m++)
		{
/*****************/
			Param.NumProcessors = m;
			Param.NumThreads_MAX = 8;			
			TaskGenerator2 generator = new TaskGenerator2(periodModifier);

			generator.setNormalDistributionBeta(beta, beta_deviation);
			/***********************************/
//			generator.setRandomBeta(0.1, 1.0);
//			generator.setRandomGamma(0.6, 0.8);
			generator.setRandomGamma(0.1, 1.0);
//			generator.setFixedGamma(0.75);
			/***********************************/
			generator.setRandomAlpha(0, 0.3);/**/
			generator.setRandomTaskNum(2,20);
/*****************/


			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			for (int seed = 0; seed < iter; seed++)
//			int seed = 0;
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (result.schedulable)	reason2[seed][i] = 1;
					else if (result.notSchedulableSingleThread) reason2[seed][i] = 2;
					else if (result.notSchedulableMultiThread) reason2[seed][i] = 3;
					else if (result.notSchedulableInvalid) reason2[seed][i] = 4;
					else if (result.notSchedulableHarmonization) reason2[seed][i] = 5;
					else System.out.printf("Warning : result has no reason %d %d\n", seed, i);
					
					for (int j = 0; j < m; j++)
					{
						if (result.schedulable && result.peakDensity <= j + 1)
							densitySchedulability[i][j] ++;
						densitySchedulabilityTotal[i][j]++;
					}
				}
			}

			System.out.printf("\n--------------RESULT m = %d-------------\n", m);

			resultForGraph += String.format("%d\t", m);
			resultForGraphPeakDensity += String.format("%d\t", m);
			resultForGraphReason += String.format("%d\t", m);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				double resultSize = result.numResult;
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
				resultForGraphReason += String.format("%5.3f\t%5.3f\t%5.3f\t%5.3f\t%5.3f\t\t",
						result.schedulability(), 
						result.notSchedulableSingleThread / resultSize,
						result.notSchedulableMultiThread / resultSize,
						result.notSchedulableInvalid / resultSize,
						result.notSchedulableHarmonization / resultSize);
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";
			resultForGraphReason += "\n";

			resultForGraphReason2 += String.format("%d\t", m);
			resultForGraphReason3 += String.format("%d\t", m);
			int[][] reason = new int[optionSet.size()][5];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
					if (reason2[i][optionSet.size() - 1] == 1 &&
							reason2[i][j] != 1)
					{
						reason[j][reason2[i][j] - 1] ++;
						unschedulable[j] ++;
					}
				}
			}
			for (int i = 0; i < optionSet.size() - 1; i++)
			{
				resultForGraphReason2 += String.format("\t%d\t", unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason2 += 
						String.format("%.3f\t", reason[i][j] / (double)unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason2 += 
						String.format("%d\t", reason[i][j]);
			}
			resultForGraphReason2 += String.format("\n");

			reason = new int[optionSet.size()][5];
			unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size(); j++)
				{
					reason[j][reason2[i][j] - 1] ++;
					unschedulable[j] ++;
				}
			}
			for (int i = 0; i < optionSet.size(); i++)
			{
				resultForGraphReason3 += String.format("\t%d\t", unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason3 += 
						String.format("%.3f\t", reason[i][j] / (double)unschedulable[i]);
				for (int j = 0; j < 5; j++)
					resultForGraphReason3 += 
						String.format("%d\t", reason[i][j]);
			}
			resultForGraphReason3 += String.format("\n");
		}
		
			
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason2);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphReason3);


		System.out.printf("Target false = %d\n", targetFalse);
	}
	public void exp170927_distribution()
	

	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 1000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setFixedBeta(beta);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0, 0.1);/**/
		generator.setRandomTaskNum(3,15);

		generator.setFixedGamma(0.5);
		


		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;

		TimeDensityDistribution[] distribution = 
				new TimeDensityDistribution[optionSet.size()];
		for (int i = 0; i < optionSet.size(); i++)
			distribution[i] = new TimeDensityDistribution(optionSet.get(i));
				
		// init results
		ArrayList<Result> results = new ArrayList<Result>();
		for (int i = 0; i < optionSet.size(); i++)
		{
			results.add(new Result(optionSet.get(i)));
		}
		for (int seed = 0; seed < iter; seed++)
//		int seed = 4;
		{
			for (int i = 0; i < optionSet.size(); i++)
			{
				ExperimentResult result;
				result = runScheduler(generator, seed, optionSet.get(i));
				results.get(i).add(result);

				distribution[i].addResult(result.refTaskSet, result.peakDensity, result.schedulable);
			}
		}

		int targetIdx = optionSet.size() - 1;
		for (int i = 0; i < optionSet.size(); i++)
		{
			/* commet out below code to get all result*/
			for (int j = 0; j < iter; j++)
			{
				if (!distribution[targetIdx].pairs.get(j).schedulable)
					distribution[i].pairs.get(j).draw = false;
			}
			/**/
					
			distribution[i].draw("plot/distribution");
		}
		for (int i = 0; i < optionSet.size(); i++)
		{
			int conditionalSchedulableCount = 0;
			for (int j = 0; j < iter; j++)
			{
				if (distribution[targetIdx].pairs.get(j).schedulable &&
						distribution[i].pairs.get(j).schedulable)
					conditionalSchedulableCount ++;
			}
			System.out.printf("%d\t", conditionalSchedulableCount);
		}
		System.out.printf("\n");
	}
	
	// alpha graph
	public void exp170919_unifast()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		
		int iter = 5000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		//generator.setFixedBeta(beta);
		/***********************************/
		//generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0.0, 0.3);/**/
	//	generator.setFixedAlpha(0.3);/**/
		generator.setRandomTaskNum(2,20);
		
		String resultForGraph = "";

		generator.setRandomGamma(0.1, 1.0);
		for (double utilsum = 0.2; utilsum <= Param.NumProcessors + 0.1; utilsum += 0.2)
		{
			// init results
			generator.setFixedBetaSum(utilsum);
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			
			for (int seed = 0; seed < iter; seed++)
			{		

				for (int i = 0; i < optionSet.size(); i++)
				{
					
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
				}
				
			}
			

			System.out.printf("\n--------------RESULT utilsum = %f-------------\n", utilsum);

			resultForGraph += String.format("%3.2f \t", utilsum);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				
				System.out.println(result);
				resultForGraph += String.format("%5.3f\t", result.schedulability()); 
			}
			resultForGraph += "\n";
		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);

	}
	
	public void exp171007_uunifast_gamma_utilization()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double betasum = 4;
		int iter = 1000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

//		generator.setFixedBetaSum(betasum);
//		generator.setRandomBetaSum(2, betasum);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0.1, 0.3);/**/
		generator.setFixedAlpha(0.0);/**/
		generator.setRandomTaskNum(3,15);
		

		String resultForGraph = "";

		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
			
		for (double utilsum = 1.0f; utilsum <= 8.0f; utilsum += 1.0f)
		{
			generator.setFixedBetaSum(utilsum);
		for (double gamma = 0.1f; gamma <= 1.01f; gamma += 0.05f)
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
			for (int seed = 0; seed < iter; seed++)
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
				}
			}

			System.out.printf("\n--------------RESULT utilsum = %5.3f\tgamma = %f-------------\n", utilsum, gamma);

			resultForGraph += String.format("%3.2f \t%3.2f \t", utilsum, gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				Result result = results.get(i);
				
				System.out.println(result);
				resultForGraph += String.format("%5.3f\t", result.schedulability()); 
			}
			resultForGraph += "\n";
			
		}
		resultForGraph += String.format("\n");
		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
			
	}

	public void exp171008_alpha()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		double beta_deviation = 0.1;
		int iter = 1000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);
//		generator.setNormalDistributionBeta(beta, beta_deviation);
		generator.setRandomBetaSum(0.1, 5);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(2,20);

		String resultForGraph = "";


		for (double alpha = 0.0; alpha < 1.01; alpha += 0.1f)
		{
			generator.setFixedAlpha(alpha);

			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			for (int seed = 0; seed < iter; seed++)
//			int seed = 0;
			{
				for (int i = 0; i < optionSet.size(); i++)
				{
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
				}
			}

			System.out.printf("\n--------------RESULT alpha = %f-------------\n", alpha);

			resultForGraph += String.format("%.3f\t", alpha);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
			}
			resultForGraph += "\n";
		}
		
			
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
	}
	public void exp171009_distribution()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.of(Option.SINGLE));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double betasum = 5;
		int iter = 1000;
		/*********************/
		Param.scmin = 100;
		Param.scmax = 10000;
		Param.Period_MIN = 200;
		Param.Period_MAX = 10000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setRandomBetaSum(0.1, betasum);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0, 0.3);/**/
		generator.setRandomTaskNum(2,20);

//		generator.setFixedGamma(0.5);
		
		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:

		TimeDensityDistribution[] distribution = 
				new TimeDensityDistribution[optionSet.size()];
		for (int i = 0; i < optionSet.size(); i++)
			distribution[i] = new TimeDensityDistribution(optionSet.get(i));
				
		// init results
		ArrayList<Result> results = new ArrayList<Result>();
		for (int i = 0; i < optionSet.size(); i++)
		{
			results.add(new Result(optionSet.get(i)));
		}
		for (int seed = 0; seed < iter; seed++)
		{
			for (int i = 0; i < optionSet.size(); i++)
			{
				ExperimentResult result;
				result = runScheduler(generator, seed, optionSet.get(i));
				results.get(i).add(result);

				distribution[i].addResult(result.refTaskSet, result.peakDensity, result.schedulable);
			}
		}

		int targetIdx = optionSet.size() - 1;
		for (int i = 0; i < optionSet.size(); i++)
		{
			/* commet out below code to get all result*/
			for (int j = 0; j < iter; j++)
			{
				if (!distribution[targetIdx].pairs.get(j).schedulable)
					distribution[i].pairs.get(j).draw = false;
			}
			/**/
					
			distribution[i].draw("plot/distribution");
		}
		for (int i = 0; i < optionSet.size(); i++)
		{
			int conditionalSchedulableCount = 0;
			for (int j = 0; j < iter; j++)
			{
				if (distribution[targetIdx].pairs.get(j).schedulable &&
						distribution[i].pairs.get(j).schedulable)
					conditionalSchedulableCount ++;
			}
			System.out.printf("%d\t", conditionalSchedulableCount);
		}
		System.out.printf("\n");
	}
	public static void main(String[] args)
	{
		IEEE_TC_17 test= new IEEE_TC_17();

//		test.exp170919_unifast();	
//		test.exp170927_uunifast_gamma();
//		test.exp171008_uunifast_density();
		test.exp171008_alpha();
//		test.exp171009_distribution();
//		test.exp171007_uunifast_gamma_utilization();	// deprecated
//		test.exp170927_gamma();			// deprecated
//		test.exp170927_density();		// deprecated

	}
	
	
	
}
