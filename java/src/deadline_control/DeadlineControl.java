package deadline_control;

import java.util.ArrayList;
import data.*;

public class DeadlineControl {
	protected ArrayList<ArrayList<TaskInfo>> periodGroup;
	protected ArrayList<DensityGroupWrapper> densityGroups;
	protected ArrayList<TaskInfo> taskInfo;
	protected double peakDensity;
	protected int LCM;
	public boolean DEBUG;
	public boolean invalid = false;

	public DeadlineControl()
	{
		periodGroup = new ArrayList<ArrayList<TaskInfo>>();
		densityGroups = new ArrayList<DensityGroupWrapper>();
		taskInfo = new ArrayList<TaskInfo>();

	}
	

	protected boolean makeTaskInfo(TaskSet taskSet)
	{
		int basePeriod = 0;
		LCM = 1;
		boolean isHarmonic = true;
		for (int i = 0; i < taskSet.size(); i++)
		{
			int period = taskSet.get(i).getPeriod();
			taskInfo.add(new TaskInfo(taskSet.get(i)));
			if (basePeriod == 0 || period < basePeriod)
				basePeriod = period;
			
			if (period % basePeriod != 0)
			{
				isHarmonic = false;
				break;
			}
			if (period > LCM) LCM = period;
		}
		return isHarmonic;
	}
	
	protected void sortTaskInfo(ArrayList<TaskInfo> taskInfo)
	{
		for (int i = 0; i < taskInfo.size() - 1; i++)
		{
			for (int j = i + 1; j < taskInfo.size(); j++)
			{
				if (taskInfo.get(i).period >= taskInfo.get(j).period)
				{
					if (taskInfo.get(i).period == taskInfo.get(j).period &&
							taskInfo.get(i).executionTime < taskInfo.get(j).executionTime)
					{
						// do nothing
					}
					else
					{
						// swap
						TaskInfo temp = taskInfo.get(i);
						taskInfo.set(i, taskInfo.get(j));
						taskInfo.set(j, temp);
					}
				}
			}
		}
	}
	
	protected double filanizeDensityGroup(TaskSet taskSet)
	{
//		double peakDensity = 0;
		for (int i = 0; i < densityGroups.size(); i++)
		{
			DensityGroupWrapper densityGroup = densityGroups.get(i);
			densityGroup.finalize();
//			peakDensity += densityGroup.realPeakDensity;
		}

		//write back schedule information into taskSet
		for (int i = 0; i < taskInfo.size(); i++)
		{
			TaskInfo info = taskInfo.get(i);
			Task task = taskSet.get(info.taskID-1);
			task.setPeriod(info.period);
//			task.setDeadline(info.deadline);
			task.setIntermediateDeadline(0, info.getControlledDeadline());

			task.selectOption(0, info.getOptionIndex());
			task.setPhase(info.getAbsolutePhase());
		}
		return getGroupPeakDensity(taskSet);
	}
	
	protected double getGroupPeakDensity(TaskSet taskSet)
	{
		double peakDensity = 0;
		for (int i = 0; i < densityGroups.size(); i++)
		{
			DensityGroupWrapper densityGroupWrapper = densityGroups.get(i);
			double maxGroupDensity = -1;
			for (int j = 0; j < densityGroupWrapper.nInstances; j++)
			{
				DensityGroup densityGroup = densityGroupWrapper.densityGroupInstances.get(j);
				double groupDensity = -1;
				for (int k = 0; k < densityGroup.size(); k++)
				{
					TaskInfo info = densityGroup.get(k);
					Task task = taskSet.get(info.taskID - 1);
					double density = task.getDensity(0);
					if (groupDensity < 0 || groupDensity < density)
						groupDensity = density;
				}
				if (maxGroupDensity < 0 || maxGroupDensity < groupDensity)
					maxGroupDensity = groupDensity;
			}
			peakDensity += maxGroupDensity;
		}
		
		return peakDensity;
	}
	
	
	protected boolean addDensityGroup(DensityGroupWrapper group, TaskInfo info)
	{
		boolean ret = false;
		
		double increase = group.getExpectedMaxEvenDensity(info) - group.maxDensity;
		double decrease = info.defaultDensity;
		if (DEBUG)
		{
			System.out.printf("%d : increase %.3f(%.3f - %.3f), decrease %.3f\n", 
					info.taskID - 1, increase, increase + group.maxDensity, 
					group.maxDensity, decrease);
		}
		if (increase < decrease)
		{
			ret = group.insertTask(info);
		}
		
		return ret;
	}
	protected void makeDensityGroup()
	{
		ArrayList<TaskInfo> taskInfoReady = new ArrayList<TaskInfo>(taskInfo);
		
		while (taskInfoReady.size() > 0)
		{
			TaskInfo groupHeader = taskInfoReady.get(0);
			DensityGroupWrapper densityGroup = 
					new DensityGroupWrapper(groupHeader.period, LCM / groupHeader.period);
			densityGroups.add(densityGroup);

			boolean ret = densityGroup.insertTask(groupHeader);
			taskInfoReady.remove(0);
			if (!ret)
			{
				invalid = true;
//				System.out.printf("DeadlineControl.makeDensityGroup: initial insertion fail\n");
				continue;
			}
			
			for (int i = 0; i < taskInfoReady.size(); i++)
			{
				TaskInfo info = taskInfoReady.get(i);
				boolean isFit = addDensityGroup(densityGroup, info);
				if (isFit)
				{
					taskInfoReady.remove(i);
					i--;
				}
			}
		}
	}
	
	
	public void setDeadline(TaskSet taskSet)
	{
		boolean isHarmonic = makeTaskInfo(taskSet);
		if (!isHarmonic)
		{
			System.out.printf("Tasks are not harmonic\n");
		}
		
//		makePeriodGroup();
//		makeDensityGroup();
//		peakDensity = filanizeDensityGroup(taskSet);

		sortTaskInfo(taskInfo);
		makeDensityGroup();
/* DEBUG */  printDebug();
		peakDensity = filanizeDensityGroup(taskSet);
/* DEBUG */  printDebug();
	}
	
	public double getPeakDensity(TaskSet taskSet)
	{
		setDeadline(taskSet);
		return getPeakDensity();
	}
	
	public double getPeakDensity()
	{
		return peakDensity;
	}

	protected void printDebug()
	{
		if (DEBUG)
		{
			for (int i = 0; i < densityGroups.size() ;i++)
			{
				DensityGroupWrapper densityGroupW = densityGroups.get(i);
				System.out.printf("Grp %3d :", i); 
				for (int j = 0; j < densityGroupW.nInstances; j++)
				{
					DensityGroup densityGroup = densityGroupW.densityGroupInstances.get(j);
					System.out.printf("#%3d(", j);
					for (int k = 0; k < densityGroup.size(); k++)
					{
						TaskInfo info = densityGroup.get(k);
						System.out.printf("%d ", info.taskID - 1);
					}
					System.out.printf(") ");
				}
				System.out.printf("\n");
			}
			for (int i = 0; i < taskInfo.size(); i++)
			{
				TaskInfo info = taskInfo.get(i);
//				for (int j = 0; j < taskInfo.size(); j++)
//				{
//					TaskInfo info = taskInfo.get(j);
//					if (info.taskID - 1 == i)
//					{
						System.out.printf("%3d : +%3d (%3d(%3d), %3d, %3d(%3d), O=%d)\n", 
								info.taskID - 1, info.getAbsolutePhase(), info.period, info.realPeriod, 
								info.executionTime, info.getControlledDeadline(), 
								info.deadline, info.getOption());
//						break;
//					}
//				}
			}
			
		}
	}
}
