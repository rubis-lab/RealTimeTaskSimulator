package logic;

import java.util.ArrayList;

import multicore_exp.Util;
import data.*;

public class DensityCalculator {
	
	
	
	protected static double getDensityAt(ArrayList<? extends PhasedTask> taskSet, long t)
	{
		double density = 0;
		for (int i = 0; i < taskSet.size(); i++)
		{
			density += taskSet.get(i).getInstanceDensity(t);
		}
		return density;
	}
	
	public static double peakDensity(ArrayList<? extends PhasedTask> taskSet)
	{
		double peakDensity = 0;
		long LCM = Util.getTaskSetLCM(taskSet);

		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			int period = (int) task.getPeriod();

			for (long j = 0; j < LCM / period; j++)
			{
				long t = j * period;
				double density = getDensityAt(taskSet, t);
				if (density > peakDensity)
					peakDensity = density;
			}
		}
		
		return peakDensity;
	}

}
