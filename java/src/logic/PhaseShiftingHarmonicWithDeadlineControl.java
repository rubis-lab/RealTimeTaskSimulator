package logic;

import java.util.ArrayList;
import java.util.Collections;

import data.PhasedTask;
import data.Task;
import data.TaskSet;

public class PhaseShiftingHarmonicWithDeadlineControl extends
		PhaseShiftingHarmonic {
	
	protected static double getPeakDensityShiftOneTask(ArrayList<PhasedTask> taskSet)
	{
		System.out.printf("it works!");
		
		ArrayList<PhasedTask> baseTasks = new ArrayList<PhasedTask>();
		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			int optimalPhase = shiftOneTask(baseTasks, task);
			task.setPhase(optimalPhase);
			task.linkedTask.setPeriod(optimalPhase);
			

			baseTasks.add(task);
		}
		
		return getPeakDensity(baseTasks);
	}

}
