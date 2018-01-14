package multicore_exp;

import java.util.ArrayList;
import java.util.EnumSet;

import multicore_exp.PhaseShiftTest.Option;
import data.PhasedTask;
import data.TaskSet;

public class ExperimentResult
{
	public ArrayList<PhasedTask> taskSet;
	public EnumSet<Option> options;
	public TaskSet refTaskSet;

//	public long LCM;
	public int numTasks;
	public double peakDensity;
	public boolean schedulable;
	public double utilization;
	public double taskSetDensity;
	public double densitySum_LCM;
	public double densityRoom_LCM;
	public double densityRoom;

	// below factor should be set from oustide
	public double harmonizationFactor;
	public double alpha;
	public double beta;
	public double gamma;
	public boolean invalid;
	public boolean harmonizable;
	public boolean harmonizationFail;

	// unschedulable reason
	public boolean notSchedulableSingleThread;
	public boolean notSchedulableMultiThread;
	public boolean notSchedulableInvalid;
	public boolean notSchedulableHarmonization;

	public ExperimentResult(ArrayList<PhasedTask> taskSet, EnumSet<Option> options, double peakDensity)
	{
		if (taskSet == null)
		{
			invalid = true;
			schedulable = false;
			harmonizable = false;
			peakDensity = 0;
			return;
		}
		this.taskSet = taskSet;
		this.options = options;
		try
		{
//			LCM = Util.getTaskSetLCM(taskSet);
			invalid = false;
		}
		catch (Exception e)
		{
			invalid = true;
		}
		numTasks = taskSet.size();
		harmonizable = options.contains(Option.PH);

		utilization = 0;
		taskSetDensity = 0;
		densitySum_LCM = 0;
		alpha = 0;
		beta = 0;
		gamma = 0;
		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			utilization += task.getExecutionOverPeriod();
			taskSetDensity += task.getExecutionOverDeadline();
			//densitySum_LCM += (LCM / task.getPeriod()) * task.getExecutionTime();
		}
		//utilization /= taskSet.size();

		if (peakDensity <= 0) 
			peakDensity = taskSetDensity;

		this.peakDensity = peakDensity;
		schedulable = this.peakDensity <= Param.NumProcessors;

//		densityRoom_LCM = (LCM * peakDensity) - densitySum_LCM;
//		densityRoom = densityRoom_LCM / (double)LCM;	// peakDensity - utilization?
	}


	//		protected double getPeakDensity()
	//		{
	//			if (options.contains(Option.PS))
	//				return DensityCalculator.peakDensity(taskSet);
	//			return taskSetDensity;
	//		}

	public void harmonizationFail()
	{
		schedulable = false;
		//			invalid = true;
		harmonizable = false;
		harmonizationFail = true;
	}
}
