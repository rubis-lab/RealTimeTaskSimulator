package multicore_exp;

import java.util.ArrayList;
import java.util.EnumSet;

import period_harmonizer.PeriodHarmonizer;
import period_harmonizer.PeriodModifier;
import phase_shifting.ExhaustiveSearchGA;
import tool.Plot;
import tool.TimeDensityDistribution;
import logic.DensityCalculator;
import logic.PhaseShiftingHarmonic;
import logic.PhaseShiftingHarmonicWithDeadlineControl;
import data.PhasedTask;
import data.SporadicTask;
import data.TaskSet;
import data.TaskSet.ParallelizedOption;
import deadline_control.DeadlineControl;
import deadline_control.DeadlineControlRTAS;
import deadline_control.DeadlineControlRTAS2;
import deadline_control.DeadlineControlRTAS3;
import generator.TaskGenerator;
import generator.TaskGenerator2;
import generator.TaskGeneratorSingleSegment;

public class PhaseShiftTest {
	
	boolean DEBUG = false;
	boolean DEBUG_TASK = false;
	boolean DEBUG_PLOT = false;
	boolean DEBUG_DC = false;
	public enum Option
	{
		PS,			// Phase Shifting
		PH,			// Period Harmonization
		DC_SP,		// modifying parallelization overhead according to given density
		DC_H1,		// horizontal packing using closed equations
		DC_H2,		// horizontal packing using RTAS method
		DC_H3,		// horizontal packing using RTAS method with heuristic 2
		DC_H4,		// horizontal packing using RTSS method. iterative PH
		MINDEN,		// possible minimum density
		MAXPAL,		// possible minimum density
		SINGLE,		// without parallelization
		GA,			// Genetic Algorithm
		
	};

	
	
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
		if (options.contains(Option.MINDEN))
		{
			double utilization = 0;
			for (int i = 0; i < taskSet.size(); i++)
			{
				utilization += taskSet.get(i).getExecutionTimeOfThread(0, 0, 0) / 
						taskSet.get(i).getPeriod();
			}

			result = new ExperimentResult(null, options, -1);
			result.invalid = false;
			result.peakDensity = utilization;
			if (utilization <= Param.NumProcessors)
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

			

		
				

		// 6. Check GA Option
		if (options.contains(Option.GA))
		{
			ExhaustiveSearchGA ga = new ExhaustiveSearchGA();
			ga.isSchedulableByGA(generator, seed);
			return null;
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
	
	
	// experiment for effect by various factors
	public void exp151231()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.PS));
		optionSet.add(EnumSet.of(Option.PH));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH));
		optionSet.add(EnumSet.of(Option.PS, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		
		
		
		int expType = 0;
		
		int iter = 100;
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();

		generator.setRandomBeta(0.1, 1.2);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,10);
		String resultForGraph = "";
		for (double gamma = 0.1; gamma <= 1.0; gamma += 0.1)
		{
			generator.setFixedGamma(gamma);
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			for (int seed = 0; seed < iter; seed++)
			{
				//			System.out.printf("%5d\t", seed);
				for (int i = 0; i < optionSet.size(); i++)
				{
					//				System.out.printf("%10s:", optionSet.get(i).toString());
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);
					//				System.out.printf("%5b\t", result.schedulable);
				}
				//			System.out.printf("\n");
			}

			System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

			resultForGraph += String.format("%3.2f \t", gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.2f\t", results.get(i).schedulability()); 
			}
			resultForGraph += "\n";
		}
		System.out.printf("%s\n", resultForGraph); 
	}

	// task is now generated period first
	public void exp160120()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.PS));
		optionSet.add(EnumSet.of(Option.PH));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH));
		optionSet.add(EnumSet.of(Option.PS, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		
		int expType = 0;
		
		int iter = 100;
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

//		PeriodModifier periodModifier = new PeriodModifier(14);
		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setRandomBeta(0.1, 0.1);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,20);
		
		Plot plotter = new Plot();

		for (double beta = 0.1f; beta <= 1.5f; beta += 0.1)
		{
//		double beta = 0.2;
			generator.setFixedBeta(beta);
			String resultForGraph = "";
			String resultForGraphPeakDensity = "";
			for (double gamma = 0.1; gamma <= 1.0f; gamma += 0.1)
			{
//			double gamma = 1.0;
				// init results
				ArrayList<Result> results = new ArrayList<Result>();
				for (int i = 0; i < optionSet.size(); i++)
				{
					results.add(new Result(optionSet.get(i)));
				}
				generator.setFixedGamma(gamma);
				for (int seed = 0; seed < iter; seed++)
				{
//					System.out.printf("Seed : %5d\n", seed);
					for (int i = 0; i < optionSet.size(); i++)
					{
						//				System.out.printf("%10s:", optionSet.get(i).toString());
						ExperimentResult result;
						result = runScheduler(generator, seed, optionSet.get(i));
						results.get(i).add(result);

//					/*debug code*/
//					{
//						System.out.printf("%5b\t", result.schedulable);
//						ArrayList<PhasedTask> taskSet = result.taskSet;
//						for (int ii = 0; ii < taskSet.size(); ii++)
//						{
//							System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
//									(int)taskSet.get(ii).getPeriod(), 
//									(int)taskSet.get(ii).getExecutionTime(),
//									(int)taskSet.get(ii).getDeadline());
//						}
//						System.out.printf("\n");
//						if (i == optionSet.size() - 1)
//						{
//						String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
//						String outputFilename = String.format("%s.eps", dataFilename);
//						String scriptFilename = String.format("plot/%d.plt", i);
//						plotter.drawGNUPlotWithScript(scriptFilename, 
//								dataFilename, outputFilename, taskSet);
//						}
//					}
//					/*debug code*/
					}
//	System.out.printf("\n");
				}

				System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

				resultForGraph += String.format("%3.2f \t%3.2f \t", beta, gamma);
				resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, gamma);
				System.out.println(results.get(0).toStringHeader());
				for (int i = 0; i < results.size(); i++)
				{
					System.out.println(results.get(i));
					resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
					resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
				}
				resultForGraph += "\n";
				resultForGraphPeakDensity += "\n";
			}
			System.out.printf("-------------\n");
			System.out.printf("%s\n", resultForGraph);
			System.out.printf("-------------\n");
			System.out.printf("%s\n", resultForGraphPeakDensity);
		}
	}

	// beta is fixed, m is increased, alpha ranged is changed
	public void exp160201()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.PS));
		optionSet.add(EnumSet.of(Option.PH));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH));
		optionSet.add(EnumSet.of(Option.PS, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 100;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

//		PeriodModifier periodModifier = new PeriodModifier(14);
		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setFixedBeta(beta);
		/***********************************/
		generator.setRandomAlpha(0, 0.0);/**/
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,20);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		for (double gamma = 0.1; gamma <= 1.0f; gamma += 0.1)
		{
//			double gamma = 0.2;
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
			for (int seed = 0; seed < iter; seed++)
			{
//				/*debug code*/
//				{
//					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
//					System.out.println(taskSet);
//				}
//				/*debug code*/
				//					System.out.printf("Seed : %5d\n", seed);
				for (int i = 0; i < optionSet.size(); i++)
				{
					//				System.out.printf("%10s:", optionSet.get(i).toString());
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

//										/*debug code*/
//										{
//											System.out.printf("%5b\t", result.schedulable);
//											ArrayList<PhasedTask> taskSet = result.taskSet;
//											if (taskSet != null)
//											{
//												for (int ii = 0; ii < taskSet.size(); ii++)
//												{
//													System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
//															(int)taskSet.get(ii).getPeriod(), 
//															(int)taskSet.get(ii).getExecutionTime(),
//															(int)taskSet.get(ii).getDeadline());
//												}
//												System.out.printf("\n");
//												if (i == optionSet.size() - 1)
//												{
//													String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
//													String outputFilename = String.format("%s.eps", dataFilename);
//													String scriptFilename = String.format("plot/%d.plt", i);
//													plotter.drawGNUPlotWithScript(scriptFilename, 
//															dataFilename, outputFilename, taskSet);
//												}
//											}
//										}
//										/*debug code*/
				}
//					System.out.printf("\n");
			}

			System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, gamma);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
	}
	// delta is fixed. beta = e/p, gamma = d/p, delta = e/d. thus gamma / beta is fixed
	public void exp160202()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.PS));
		optionSet.add(EnumSet.of(Option.PH));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH));
		optionSet.add(EnumSet.of(Option.PS, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		double gamma = 0.5;
		int iter = 100;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

//		PeriodModifier periodModifier = new PeriodModifier(14);
		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		/***********************************/
		generator.setRandomAlpha(0, 0.5);/**/
		/***********************************/
		generator.setRandomTaskNum(3,20);
		generator.setFixedGamma(gamma);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		for (double delta = 0.1f; delta <= 4f; delta += 0.2f)
		{
//			double gamma = 0.2;
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			double beta = gamma * delta;
			generator.setFixedBeta(beta);
			for (int seed = 0; seed < iter; seed++)
			{
//				/*debug code*/
//				{
//					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
//					System.out.println(taskSet);
//				}
//				/*debug code*/
				//					System.out.printf("Seed : %5d\n", seed);
				for (int i = 0; i < optionSet.size(); i++)
				{
					//				System.out.printf("%10s:", optionSet.get(i).toString());
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

//										/*debug code*/
//										{
//											System.out.printf("%5b\t", result.schedulable);
//											ArrayList<PhasedTask> taskSet = result.taskSet;
//											if (taskSet != null)
//											{
//												for (int ii = 0; ii < taskSet.size(); ii++)
//												{
//													System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
//															(int)taskSet.get(ii).getPeriod(), 
//															(int)taskSet.get(ii).getExecutionTime(),
//															(int)taskSet.get(ii).getDeadline());
//												}
//												System.out.printf("\n");
//												if (i == optionSet.size() - 1)
//												{
//													String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
//													String outputFilename = String.format("%s.eps", dataFilename);
//													String scriptFilename = String.format("plot/%d.plt", i);
//													plotter.drawGNUPlotWithScript(scriptFilename, 
//															dataFilename, outputFilename, taskSet);
//												}
//											}
//										}
//										/*debug code*/
				}
//					System.out.printf("\n");
			}

			System.out.printf("\n--------------RESULT delta = %f-------------\n", delta);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, delta);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, delta);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
	}

	// DeadlineControl is added
	// gamma graph
	public void exp160316()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//		optionSet.add(EnumSet.of(Option.PS));
//		optionSet.add(EnumSet.of(Option.PH));
		optionSet.add(EnumSet.of(Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PS, Option.PH));
//		optionSet.add(EnumSet.of(Option.PS, Option.DC1));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC1));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 100;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

//		PeriodModifier periodModifier = new PeriodModifier(14);
		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setFixedBeta(beta);
		/***********************************/
		generator.setRandomAlpha(0, 0.0);/**/
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,20);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO: gamma = 0.2, PS result weird.

		for (double gamma = 0.1; gamma <= 1.0f; gamma += 0.1)
//		double gamma = 0.1;
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
			for (int seed = 0; seed < iter; seed++)
//			int seed = 6;
			{
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
			}

			System.out.printf("\n--------------RESULT gamma = %f-------------\n", gamma);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, gamma);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, gamma);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
	}
	// alpha graph
	public void exp160406()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 100;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setFixedBeta(beta);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0, 0.0);/**/
		generator.setRandomTaskNum(3,20);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:

		for (double alpha = 0.0; alpha <= 1.0f; alpha += 0.1)
//		double alpha = 0.1;
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedAlpha(alpha);
			for (int seed = 0; seed < iter; seed++)
//			int seed = 6;
			{
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
			}

			System.out.printf("\n--------------RESULT alpha = %f-------------\n", alpha);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, alpha);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, alpha);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
	}
	// alpha graph
	public void exp160427()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
		optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 100;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setFixedBeta(beta);
		/***********************************/
		generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0, 0.0);/**/
		generator.setRandomTaskNum(3,20);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = !false;
		DEBUG_TASK = !false;
		DEBUG_PLOT = !false;
		DEBUG_DC = !false;
		//TODO:

//		for (double alpha = 0.0; alpha <= 1.0f; alpha += 0.1)
		double alpha = 0.1;
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedAlpha(alpha);
//			for (int seed = 0; seed < iter; seed++)
			int seed = 58;
			{
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
			}

			System.out.printf("\n--------------RESULT alpha = %f-------------\n", alpha);

			resultForGraph += String.format("%3.2f \t%3.2f \t", beta, alpha);
			resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", beta, alpha);
			System.out.println(results.get(0).toStringHeader());
			for (int i = 0; i < results.size(); i++)
			{
				System.out.println(results.get(i));
				resultForGraph += String.format("%5.3f\t", results.get(i).schedulability()); 
				resultForGraphPeakDensity += String.format("%5.3f\t", results.get(i).peakDensity());
			}
			resultForGraph += "\n";
			resultForGraphPeakDensity += "\n";

		}
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraph);
		System.out.printf("-------------\n");
		System.out.printf("%s\n", resultForGraphPeakDensity);
	}
	// alpha graph
	public void exp160427_gamma()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//		optionSet.add(EnumSet.of(Option.MINDEN));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 1000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
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
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];
		int targetFalse = 0;

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:

		for (double gamma = 0.1; gamma <= 1.0f; gamma += 0.1)
//		double gamma = 1.0;
//		for (double gamma = 0.2; gamma <= 0.3f; gamma += 0.1)
		{
			// init results
			ArrayList<Result> results = new ArrayList<Result>();
			for (int i = 0; i < optionSet.size(); i++)
			{
				results.add(new Result(optionSet.get(i)));
			}
			generator.setFixedGamma(gamma);
			for (int seed = 0; seed < iter; seed++)
//			int seed = 226;
//			int seed = 817;
			{
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (result.schedulable)	reason2[seed][i] = 1;
					else if (result.notSchedulableSingleThread) reason2[seed][i] = 2;
					else if (result.notSchedulableMultiThread) reason2[seed][i] = 3;
					else if (result.notSchedulableInvalid) reason2[seed][i] = 4;
					else if (result.notSchedulableHarmonization) reason2[seed][i] = 5;
					else System.out.printf("Warning : result has no reason %d %d\n", seed, i);
						
					

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						targetFalse++;
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
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
//			int[][] unschedulable = new int[optionSet.size()][2];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
//					if (reason2[i][j] != 1)
//						unschedulable[j][1] ++;
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

	public void exp160427_density()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//		optionSet.add(EnumSet.of(Option.MINDEN));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 1000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:
		int [][] densitySchedulability = new int[optionSet.size()][128];
		int [][] densitySchedulabilityTotal = new int[optionSet.size()][128];
		int targetFalse = 0;


		for (int m = 1; m <= 8; m++)
		{
/*****************/
			Param.NumProcessors = m;
			Param.NumThreads_MAX = 8;			
			TaskGenerator2 generator = new TaskGenerator2(periodModifier);

			generator.setFixedBeta(beta);
			/***********************************/
//			generator.setRandomBeta(0.1, 1.0);
//			generator.setRandomGamma(0.6, 0.8);
			generator.setRandomGamma(0.5, 1.0);
//			generator.setFixedGamma(0.75);
			/***********************************/
			generator.setRandomAlpha(0, 0.1);/**/
			generator.setRandomTaskNum(3,15);
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
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
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
							

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						targetFalse ++;
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
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
//			int[][] unschedulable = new int[optionSet.size()][2];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
//					if (reason2[i][j] != 1)
//						unschedulable[j][1] ++;
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


//		System.out.printf("------------ Density Schedulability ----------\n");
//		for (int j = 0; j < Param.NumProcessors; j++)
//		{
//			System.out.printf("%d\t", j + 1);
//			for (int i = 0; i < optionSet.size(); i++)
//			{
//				System.out.printf("%f\t", 
//						densitySchedulability[i][j] / (double) densitySchedulabilityTotal[i][j]);
//			}
//			System.out.printf("\n");
//		}
		System.out.printf("Target false = %d\n", targetFalse);
	}
	public void exp160501_distribution()
	

	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//		optionSet.add(EnumSet.of(Option.MINDEN));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		double beta = 0.5;
		int iter = 1000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
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
		
		Plot plotter = new Plot();

		int targetFalse = 0;

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
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
//		int seed = 4;
		{
			if (DEBUG_TASK)
			{
				TaskSet taskSet = generator.GenerateTaskSet(seed, seed);

				System.out.println(taskSet);
			}
			if (DEBUG)
			{
				resultOther = false;
				resultTarget = false;
				TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
				nTasks = taskSet.size();
				System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
			}
			for (int i = 0; i < optionSet.size(); i++)
			{
				if (DEBUG)
				{
					System.out.printf("%10s:", optionSet.get(i).toString());
				}
				ExperimentResult result;
				result = runScheduler(generator, seed, optionSet.get(i));
				results.get(i).add(result);

				distribution[i].addResult(result.refTaskSet, result.peakDensity, result.schedulable);

				if (DEBUG)
				{
					if (i == optionSet.size() - 1)
						resultTarget = result.schedulable;
					else
						resultOther = resultOther | result.schedulable;
					/*debug code*/
					System.out.printf("%5b", result.schedulable);
					if (result.invalid) System.out.printf("I");
					System.out.printf("\t");
					ArrayList<PhasedTask> taskSet = result.taskSet;
					if (taskSet != null)
					{
						if (DEBUG_TASK)
						{
							for (int ii = 0; ii < taskSet.size(); ii++)
							{
								System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
										(int)taskSet.get(ii).getPeriod(), 
										(int)taskSet.get(ii).getExecutionTime(),
										(int)taskSet.get(ii).getDeadline());
							}
							System.out.printf("\n");
						}
						if (DEBUG_PLOT)
						{
							if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
								//								if (i == optionSet.size() - 2)
							{
								String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
								String outputFilename = String.format("%s.eps", dataFilename);
								String scriptFilename = String.format("plot/%d.plt", i);
								plotter.drawGNUPlotWithScript(scriptFilename, 
										dataFilename, outputFilename, taskSet);
							}
						}
					}
				}
			}
			if (DEBUG)
			{
				if (resultOther && !resultTarget)
				{
					targetFalse++;
					if (minTasks < 0 || minTasks > nTasks)
					{
						minTasks = nTasks;
						minTaskSeed = seed;
					}
				}
				System.out.printf("\n");
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
		if (DEBUG)
		{
			System.out.printf("debugging this seed : %d\n", minTaskSeed);
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
	
	public void starlab16()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//		optionSet.add(EnumSet.of(Option.MINDEN));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
//		optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		int iter = 10000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		generator.setRandomBeta(0.3, 1.0);
		/***********************************/
		generator.setRandomGamma(0.3, 1.0);
		/***********************************/
		generator.setRandomAlpha(0, 0.1);/**/
		generator.setRandomTaskNum(6,10);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];
		int targetFalse = 0;

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:

		ArrayList<Result> results = new ArrayList<Result>();
		for (int i = 0; i < optionSet.size(); i++)
		{
			results.add(new Result(optionSet.get(i)));
		}
//		for (double gamma = 0.1; gamma <= 1.0f; gamma += 0.1)
//		double gamma = 1.0;
//		for (double gamma = 0.2; gamma <= 0.3f; gamma += 0.1)
		{
			// init results
			for (int seed = 0; seed < iter; seed++)
//			int seed = 226;
//			int seed = 817;
			{
				if (DEBUG_TASK)
				{
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					
					System.out.println(taskSet);
				}
				if (DEBUG)
				{
					resultOther = false;
					resultTarget = false;
					TaskSet taskSet = generator.GenerateTaskSet(seed, seed);
					nTasks = taskSet.size();
					System.out.printf("Seed : %5d, nTasks = %d\n", seed, taskSet.size());
				}
				for (int i = 0; i < optionSet.size(); i++)
				{
					if (DEBUG)
					{
						System.out.printf("%10s:", optionSet.get(i).toString());
					}
					ExperimentResult result;
					result = runScheduler(generator, seed, optionSet.get(i));
					results.get(i).add(result);

					if (result.schedulable)	reason2[seed][i] = 1;
					else if (result.notSchedulableSingleThread) reason2[seed][i] = 2;
					else if (result.notSchedulableMultiThread) reason2[seed][i] = 3;
					else if (result.notSchedulableInvalid) reason2[seed][i] = 4;
					else if (result.notSchedulableHarmonization) reason2[seed][i] = 5;
					else System.out.printf("Warning : result has no reason %d %d\n", seed, i);
						
					

					if (DEBUG)
					{
						if (i == optionSet.size() - 1)
							resultTarget = result.schedulable;
						else
							resultOther = resultOther | result.schedulable;
						/*debug code*/
						System.out.printf("%5b", result.schedulable);
						if (result.invalid) System.out.printf("I");
						System.out.printf("\t");
						ArrayList<PhasedTask> taskSet = result.taskSet;
						if (taskSet != null)
						{
							if (DEBUG_TASK)
							{
								for (int ii = 0; ii < taskSet.size(); ii++)
								{
									System.out.printf("%2d(%4d,%4d,%4d)\t", ii, 
											(int)taskSet.get(ii).getPeriod(), 
											(int)taskSet.get(ii).getExecutionTime(),
											(int)taskSet.get(ii).getDeadline());
								}
								System.out.printf("\n");
							}
							if (DEBUG_PLOT)
							{
								if (i == optionSet.size() - 1 || i == optionSet.size() - 2)
//								if (i == optionSet.size() - 2)
								{
									String dataFilename = String.format("plot/data%d_%d_%b", seed, i, result.schedulable);
									String outputFilename = String.format("%s.eps", dataFilename);
									String scriptFilename = String.format("plot/%d.plt", i);
									plotter.drawGNUPlotWithScript(scriptFilename, 
											dataFilename, outputFilename, taskSet);
								}
							}
						}
						/*debug code*/
					}
				}
				if (DEBUG)
				{
					if (resultOther && !resultTarget)
					{
						targetFalse++;
						if (minTasks < 0 || minTasks > nTasks)
						{
							minTasks = nTasks;
							minTaskSeed = seed;
						}
					}
					System.out.printf("\n");
				}
			}
			if (DEBUG)
			{
				System.out.printf("debugging this seed : %d\n", minTaskSeed);
			}


		}
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
			
			int[][] reason = new int[optionSet.size()][5];
//			int[][] unschedulable = new int[optionSet.size()][2];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
//					if (reason2[i][j] != 1)
//						unschedulable[j][1] ++;
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
		
		for (int i = 0; i < optionSet.size(); i++)
		{
			System.out.printf("%s\n", optionSet.get(i));
			results.get(i).printHistogram();
		}
		
			

	}
	
	
	// alpha graph
	public void exp170919_unifast()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
//			optionSet.add(EnumSet.of(Option.MINDEN));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
//			optionSet.add(EnumSet.of(Option.PS, Option.PH, Option.DC_SP));
//			optionSet.add(EnumSet.of(Option.PH, Option.DC_H1));
//			optionSet.add(EnumSet.of(Option.PH, Option.DC_H2));
//			optionSet.add(EnumSet.of(Option.PH, Option.DC_H3));
		optionSet.add(EnumSet.of(Option.DC_H4));
		
		int expType = 0;
		
		/*********************/
		Param.NumProcessors = 8;
		Param.NumThreads_MAX = 8;
		
		int iter = 1000;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;

		PeriodModifier periodModifier = new PeriodModifier(10);
		TaskGenerator2 generator = new TaskGenerator2(periodModifier);

		//generator.setFixedBeta(beta);
		/***********************************/
		//generator.setRandomGamma(0.1, 1);
		/***********************************/
		generator.setRandomAlpha(0, 0.1);/**/
		generator.setRandomTaskNum(3,15);
		
		Plot plotter = new Plot();

		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[][] reason2 = new int[iter][optionSet.size()];
		int targetFalse = 0;

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		DEBUG = !false;
		DEBUG_TASK = false;
		DEBUG_PLOT = false;
		DEBUG_DC = false;
		//TODO:
		generator.setRandomGamma(0.1, 1.0);
		for (double utilsum = 0.2; utilsum <= Param.NumProcessors; utilsum += 0.2)
//			double gamma = 1.0;
//			for (double gamma = 0.2; gamma <= 0.3f; gamma += 0.1)
		{
			// init results
			generator.setFixedBeta(utilsum);
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

					if (result.schedulable)	reason2[seed][i] = 1;
					else if (result.notSchedulableSingleThread) reason2[seed][i] = 2;
					else if (result.notSchedulableMultiThread) reason2[seed][i] = 3;
					else if (result.notSchedulableInvalid) reason2[seed][i] = 4;
					else if (result.notSchedulableHarmonization) reason2[seed][i] = 5;
					else System.out.printf("Warning : result has no reason %d %d\n", seed, i);

					
				}
				
			}
			

			System.out.printf("\n--------------RESULT utilsum = %f-------------\n", utilsum);

			resultForGraph += String.format("%3.2f \t", utilsum);
			resultForGraphPeakDensity += String.format("g:%3.2f \t", utilsum);
			resultForGraphReason += String.format("%.3f\t", utilsum);
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
			
			resultForGraphReason2 += String.format("%.1f\t", utilsum);
			resultForGraphReason3 += String.format("%.1f\t", utilsum);
			int[][] reason = new int[optionSet.size()][5];
//				int[][] unschedulable = new int[optionSet.size()][2];
			int[] unschedulable = new int[optionSet.size()];
			for (int i = 0; i < iter; i++)
			{
				for (int j = 0; j < optionSet.size() - 1; j++)
				{
//						if (reason2[i][j] != 1)
//							unschedulable[j][1] ++;
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
	public static void main(String[] args)
	{
		PhaseShiftTest test = new PhaseShiftTest();

//		test.exp151231();
//		test.exp160120();
//		test.exp160201();
//		test.exp160202();
//		test.exp160316();
//		test.exp160406();

		//test.exp160427();

//		test.exp160427_gamma();
//		test.exp160427_density();
//		test.exp160501_distribution();
		
//		test.starlab16();
		test.exp170919_unifast();

/*
		if (args.length == 0)
			test1();
		else
			test2(Integer.parseInt(args[0]));
*/
	}

}
