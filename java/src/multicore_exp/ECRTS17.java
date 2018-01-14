package multicore_exp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;

import data.PhasedTask;
import data.Task;
import data.TaskSet;
import data.TaskSet.ParallelizedOption;
import deadline_control.DeadlineControlRTAS3;
import period_harmonizer.PeriodHarmonizer;
import period_harmonizer.PeriodModifier;
import tool.Plot;
import multicore_exp.PhaseShiftTest.Option;

public class ECRTS17 {

	public ExperimentResult runScheduler(EnumSet<Option> options)
	{
		TaskSet taskSet0 = loadTaskSet();
		TaskSet taskSet = loadTaskSet();
		TaskSet taskSetH = loadTaskSet();
		
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
		
//		// 3. Check Period Harmonization Option
//		if (options.contains(Option.PH))
//		{
////			ParallelizedOption harmonizerOption = convertingOption;
//			ParallelizedOption harmonizerOption = ParallelizedOption.SINGLE;
//			
//			if (!harmonizable)
//			{
//				phasedTaskSet = taskSet0.toPhasedTask(harmonizerOption);
//				result = new ExperimentResult(phasedTaskSet, options, -1);
//				result.harmonizationFail();
//				result.refTaskSet = taskSet0;
//				return result;
//			}
//			taskSet = taskSetH;
//		}

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

//		// 5. Check Phase Shifting Option
//		if (options.contains(Option.PS))
//		{
//			if (phasedTaskSet == null)
//				peakDensity = -1;
//			peakDensity = PhaseShiftingHarmonic.getMinPeakDensity(
//					phasedTaskSet, PhaseShiftingHarmonic.option.L_GAMMA_FIRST);
//		}
		// 6. Check Deadline Control
//		if (options.contains(Option.DC_H1))
//		{
//			if (options.contains(Option.PS))
//				System.out.printf("DC_H1 and PS are exclusive. PS result will be ignored.\n");
//			DeadlineControl dc = new DeadlineControl();
//			dc.DEBUG = DEBUG_DC;
//			peakDensity = dc.getPeakDensity(taskSet);
//			if (dc.invalid)
//			{
//				result = new ExperimentResult(phasedTaskSet, options, -1);
//				result.invalid = true;
//				result.refTaskSet = taskSet;
//				return result;
//			}
//			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
//		}
//		else if (options.contains(Option.DC_H2))
//		{
//			if (options.contains(Option.PS))
//				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");
//			DeadlineControlRTAS dc = new DeadlineControlRTAS();
//			dc.DEBUG = DEBUG_DC;
//			peakDensity = dc.getPeakDensity(taskSet);
//			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
//		}
//		else if (options.contains(Option.DC_H3))
//		{
//			if (options.contains(Option.PS))
//				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");
//			DeadlineControlRTAS2 dc = new DeadlineControlRTAS2();
//			dc.DEBUG = DEBUG_DC;
//			peakDensity = dc.getPeakDensity(taskSet);
//			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
//		}
//		else if (options.contains(Option.DC_H4))
		if (options.contains(Option.DC_H4))
		{
			if (options.contains(Option.PS))
				System.out.printf("DC_H2 and PS are exclusive. PS result will be ignored.\n");

			DeadlineControlRTAS3 dc = new DeadlineControlRTAS3();
//			dc.DEBUG = DEBUG_DC;
			peakDensity = dc.getPeakDensity(taskSet);
			phasedTaskSet = taskSet.toPhasedTask(ParallelizedOption.SELECTED);
		}

			

		
				

//		// 6. Check GA Option
//		if (options.contains(Option.GA))
//		{
//			ExhaustiveSearchGA ga = new ExhaustiveSearchGA();
//			ga.isSchedulableByGA(generator, seed);
//			return null;
//		}
		
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
	
	public void printParam(int optionID, EnumSet<Option> option, ExperimentResult result)
	{
		ArrayList<PhasedTask> taskSet = result.taskSet;
		//plot result
		System.out.printf("#Alg TaskID     Offset SegID    Local.d   TotalExe ThreadWCET Option     Period density\n");
		if (option.isEmpty() || option.contains(Option.MAXPAL))
		{
			TaskSet taskSet_ = result.refTaskSet;
			int optionIndex;
			if (option.isEmpty())
				optionIndex = 0;
			else 
				optionIndex = Param.NumThreads_MAX - 1;

			for (int j = 0; j < taskSet_.size(); j++)
			{
				Task task = taskSet_.get(j);
				int executionTime = task.getTotalExecutionTime(0, optionIndex);
				System.out.printf("%4d %6d %10d     1 %10d %10d %10d %6d %10d %7.3f\n",
						optionID,
						task.getTaskID(),
						0 /*offset*/,
						(int)task.getDeadline(),
						executionTime, 
						task.getMaxExecutionTimeOfSegment(0, optionIndex),
						optionIndex + 1,
						(int)task.getPeriod(),
						executionTime / (double) task.getDeadline());
			}
		}
		else
		{
			for (int j = 0; j < taskSet.size();j ++)
			{
				System.out.printf("%4d %6d %10d     1 %10d %10d %10d %6d %10d %7.3f\n",
						optionID,
						taskSet.get(j).taskID + 1,
						taskSet.get(j).getPhase(),
						(int)taskSet.get(j).getDeadline(),
						(int)taskSet.get(j).getExecutionTime(),
						taskSet.get(j).getMaxThreadExecutionTime(),
						taskSet.get(j).getOption(),
						(int)taskSet.get(j).getPeriod(),
						taskSet.get(j).getExecutionOverDeadline());

			}

			if (DEBUG_PLOT)
			{
				Plot plotter = new Plot();
				String dataFilename = String.format("plot/data%d_%b", optionID, result.schedulable);
				String outputFilename = String.format("%s.eps", dataFilename);
				String scriptFilename = String.format("plot/%d.plt", optionID);
				plotter.drawGNUPlotWithScript(scriptFilename, 
						dataFilename, outputFilename, taskSet);
			}
		}
	}

	protected boolean DEBUG_PLOT = true;
	public void exp170123_real()
	{
		ArrayList<EnumSet<Option>> optionSet = new ArrayList<EnumSet<Option>>(); 
		optionSet.add(EnumSet.noneOf(Option.class));
		optionSet.add(EnumSet.of(Option.MAXPAL));
		optionSet.add(EnumSet.of(Option.DC_SP));
		optionSet.add(EnumSet.of(Option.DC_H4));


		/*********************/
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		/*********************/
		Param.scmin = 10;
		Param.scmax = 50;
		Param.Period_MIN = 200;
		Param.Period_MAX = 1000;
		Param.NumSegments_MIN = 1;
		Param.NumSegments_MAX = 1;


		String resultForGraph = "";
		String resultForGraphPeakDensity = "";
		String resultForGraphReason ="";
		String resultForGraphReason2 ="";
		String resultForGraphReason3 ="";
		int[] reason2 = new int[optionSet.size()];
		int targetFalse = 0;

		int nTasks = 0;
		int minTasks = -1;
		int minTaskSeed = 0;
		boolean resultOther = false;
		boolean resultTarget = false;
		//TODO:

//		DEBUG_PLOT = false;
		// init results
		ArrayList<Result> results = new ArrayList<Result>();
		for (int i = 0; i < optionSet.size(); i++)
		{
			results.add(new Result(optionSet.get(i)));
		}
		for (int i = 0; i < optionSet.size(); i++)
		{
			ExperimentResult result;
			result = runScheduler(optionSet.get(i));
			results.get(i).add(result);

			if (result.schedulable)	reason2[i] = 1;
			else if (result.notSchedulableSingleThread) reason2[i] = 2;
			else if (result.notSchedulableMultiThread) reason2[i] = 3;
			else if (result.notSchedulableInvalid) reason2[i] = 4;
			else if (result.notSchedulableHarmonization) reason2[i] = 5;
			else System.out.printf("Warning : result has no reason %d\n", i);
			
			printParam(i + 1, optionSet.get(i), result);
			
		}


		resultForGraph += String.format("%3.2f \t%3.2f \t", 0.0, 0.0);
		resultForGraphPeakDensity += String.format("%3.2f \tg:%3.2f \t", 0f, 0f);
		resultForGraphReason += String.format("%.3f\t", 0f);
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

		resultForGraphReason2 += String.format("%.1f\t", 0f);
		resultForGraphReason3 += String.format("%.1f\t", 0f);
		int[][] reason = new int[optionSet.size()][5];
		//			int[][] unschedulable = new int[optionSet.size()][2];
		int[] unschedulable = new int[optionSet.size()];
		for (int i = 0; i < optionSet.size() - 1; i++)
		{
			//					if (reason2[i][j] != 1)
			//						unschedulable[j][1] ++;
			if (reason2[optionSet.size() - 1] == 1 &&
					reason2[i] != 1)
			{
				reason[i][reason2[i] - 1] ++;
				unschedulable[i] ++;
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
		for (int i = 0; i < optionSet.size(); i++)
		{
			reason[i][reason2[i] - 1] ++;
			unschedulable[i] ++;
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
		
		for (int i = 0; i < results.size(); i++)
			System.out.printf("%f\n", results.get(i).peakDensity);

	}
	public void taskCreationTest()
	{
		Task task = new Task(1);
		
		for (int k = 0; k < Param.NumThreads_MAX; k++)
		{
			int Oik = k + 1;
			for (int x = 0; x < Oik; x++)
				task.setExecutionTime(0, k, x, 100 /*wcet*/);
		}
		task.setDeadline((int) 100 /*deadline*/);
	}
	
	protected TaskSet loadTaskSet()
	{
		TaskSet taskSet = new TaskSet();
		Scanner s = null;
		
		try {
			s = new Scanner(new File(inputFileName)).useDelimiter("\\s+");			
			int numCPU = s.nextInt();
			s.nextInt();
			int numTasks = s.nextInt();
			Param.NumProcessors = numCPU;
			Param.NumThreads_MAX = numCPU;
			
			for (int i = 0; i < numTasks; i++)
			{
				int numSegment = s.nextInt();
				int deadline = s.nextInt();
				int period = s.nextInt();
				Task task = new Task(numSegment);
				for (int j = 0; j < numSegment; j++)
				{
					s.nextInt();	// dummy
					for (int k = 0; k < numCPU; k++)
					{
						s.nextInt();	// optionCPU. this should be same as j + 1
						s.nextInt();	// dummy (optionGPU)
						for (int x = 0; x < k + 1; x ++)
						{
							task.setExecutionTime(j, k, x, s.nextInt() + (int)Math.ceil(20000.0 / (k + 1)));
						}
					}
				}
				task.setDeadline(deadline);
				task.setRealDeadline(deadline);
				task.setPeriod(period);
				task.setRealPeriod(period);
				task.setTaskID(i + 1);
				taskSet.add(task);
			}
			s.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return taskSet;
		
	}
	
	private String inputFileName = "input.txt";

	public static void main(String args[])
	{
		ECRTS17 test = new ECRTS17();
		test.loadTaskSet();
		test.exp170123_real();
	}
}
