package multicore_exp;

import java.util.ArrayList;
import java.util.EnumSet;

import data.PhasedTask;
import multicore_exp.PhaseShiftTest.Option;

public class Result 
{
	public EnumSet<Option> options;

//	public long LCM;
	public int numTasks;
	public double peakDensity;
	public double peakDensitySchedulable;
	public int schedulable;
	public double utilization;
	public double utilizationSchedulable;
	public double minUtilizationSchedulable;
	public double minUtilizationUnSchedulable;
	public double maxUtilizationSchedulable;
	public double taskSetDensity;
	public double densitySum_LCM;
	public double densityRoom_LCM;
	public double densityRoom;
	public double densityRoomSchedulable;
	public int notSchedulableSingleThread;
	public int notSchedulableMultiThread;
	public int notSchedulableInvalid;
	public int notSchedulableHarmonization;
	public int[] histogram;

	// below factor should be set from outside
	public double harmonizationFactor;
	public double alpha;
	public double beta;
	public double gamma;
	public int invalid;
	public int harmonizable;

	public int numResult;

	public Result(EnumSet<Option> options)
	{
		this.options = options;
		maxUtilizationSchedulable = -1;
		minUtilizationSchedulable = -1;
		minUtilizationUnSchedulable = -1;
		histogram = new int[20];
	}
	public void add(ExperimentResult result)
	{
		if (result.peakDensity  < 0)
			System.out.print("!\n");
		numResult++;
		if (result.invalid)
		{
			invalid++;
			if (result.harmonizationFail)
			{
				notSchedulableHarmonization++;
				result.notSchedulableHarmonization = true;
			}
			else
			{
				notSchedulableInvalid++;
				result.notSchedulableInvalid = true;
			}
			return;
		}
//		LCM += result.LCM;
		numTasks += result.numTasks;
		peakDensity += result.peakDensity;

		utilization += result.utilization;
		taskSetDensity += result.taskSetDensity;
		densitySum_LCM += result.densitySum_LCM;
		densityRoom_LCM += result.densityRoom_LCM;
		densityRoom += result.densityRoom;
		if (result.schedulable) 
		{
			schedulable++;
			peakDensitySchedulable += result.peakDensity;
			densityRoomSchedulable += result.densityRoom;
			utilizationSchedulable += result.utilization;
			if (maxUtilizationSchedulable < 0 || result.utilization > maxUtilizationSchedulable)
				maxUtilizationSchedulable = result.utilization;
			if (minUtilizationSchedulable < 0 || result.utilization < minUtilizationSchedulable)
				minUtilizationSchedulable = result.utilization;
			
			double parallelismUtilization = result.utilization / Param.NumProcessors;
/*
			if (parallelismUtilization / 0.05 >= 20)
			{
				System.out.printf("%f %f\n", parallelismUtilization, result.peakDensity);
				System.out.println(result.refTaskSet);
			}
*/
//			histogram[(int)(parallelismUtilization / 0.05)] ++; 
		}
		else
		{
			if (minUtilizationUnSchedulable < 0 || result.utilization < minUtilizationUnSchedulable)
				minUtilizationUnSchedulable = result.utilization;
			//if not schedulable, count why
			boolean isSingleThread = true;
			for (int i = 0; i < result.taskSet.size(); i++)
			{
				PhasedTask task = result.taskSet.get(i);
				if (task.linkedTask == null)
					break;
				if (task.linkedTask.getExecutionTimeOfThread(0, 0, 0) != task.getExecutionTime())
				{
					isSingleThread = false;
					break;
				}
			}
			if (isSingleThread)
			{
				notSchedulableSingleThread ++;
				result.notSchedulableSingleThread = true;
			}
			else
			{
				notSchedulableMultiThread ++;
				result.notSchedulableMultiThread = true;
			}
		}


		if (result.harmonizable)
		{
			harmonizable++;
			harmonizationFactor += result.harmonizationFactor;
		}
		alpha += result.alpha;
		beta += result.beta;
		gamma += result.gamma;

	}

	public double schedulability()
	{
		return schedulable / (double)numResult;
	}

	public double harmonizationFactor()
	{
		return harmonizationFactor / (double)harmonizable;
	}

	public double densityRoom()
	{
		return densityRoom / (double)(numResult - invalid);
	}

	public double densityRoomSchedulable()
	{
		return densityRoomSchedulable / (double)schedulable;
	}

	public double peakDensity()
	{
		return peakDensity / (double)(numResult - invalid);
	}

	public double peakDensitySchedulable()
	{
		return peakDensitySchedulable / (double)schedulable;
	}
	
	public double utilization()
	{
		return utilization / (numResult - invalid);
	}
	public double utilizationSchedulable()
	{
		return utilizationSchedulable / schedulable;
	}

	public String toStringHeader()
	{
		String ret = "";
		ret += String.format("%15s\t", "option");
		ret += String.format("%10s\t", "# results");
		ret += String.format("%10s\t", "# invalid");
		ret += String.format("%30s\t", "util.sched");
		ret += String.format("%20s\t", "util.unsched");
		ret += String.format("%10s\t", "NS_Single");
		ret += String.format("%10s\t", "NS_Multi");
		ret += String.format("%20s\t", "schedulability");
		ret += String.format("%20s\t", "har. factor");
		ret += String.format("%20s\t", "dens.room");
		ret += String.format("%20s\t", "dens.room.sched");
		ret += String.format("%20s\t", "peak dens.");
		ret += String.format("%20s\t", "peak dens.sched");
		return ret;
	}
	public String toString()
	{
		String ret = "";
		ret += String.format("%15s\t", options);
		ret += String.format("%10d\t", numResult);
		ret += String.format("%10d\t", invalid);
		ret += String.format("%8.3f (%5d) %5.3f %5.3f\t", utilizationSchedulable(), schedulable, minUtilizationSchedulable, maxUtilizationSchedulable);
		ret += String.format("%20.3f\t", minUtilizationUnSchedulable);
		ret += String.format("%10d\t", notSchedulableSingleThread);
		ret += String.format("%10d\t", notSchedulableMultiThread);
		ret += String.format("%12.3f (%5d)\t", schedulability(), numResult);
		ret += String.format("%12.3f (%5d)\t", harmonizationFactor(), harmonizable);
		ret += String.format("%12.3f (%5d)\t", densityRoom(), numResult - invalid);
		ret += String.format("%12.3f (%5d)\t", densityRoomSchedulable(), schedulable);
		ret += String.format("%12.3f (%5d)\t", peakDensity(), numResult - invalid);
		ret += String.format("%12.3f (%5d)\t", peakDensitySchedulable(), schedulable);

		return ret;
	}
	public void printHistogram()
	{
		for (int i = 0; i < 20; i++)
		{
			System.out.printf("%3.2f\t%d\n", i * 0.05, histogram[i]);
		}
	}
}
