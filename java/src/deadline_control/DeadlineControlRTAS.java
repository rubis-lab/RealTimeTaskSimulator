package deadline_control;

import java.util.ArrayList;

import data.Task;
import data.TaskSet;


public class DeadlineControlRTAS extends DeadlineControl {
	
	protected ArrayList<DensityGroupWrapperSnippets> densityGroups;
	public DeadlineControlRTAS()
	{
		super();
		densityGroups = new ArrayList<DensityGroupWrapperSnippets>();
	}
	
	
	protected double filanizeDensityGroup(TaskSet taskSet)
	{
//		double peakDensity = 0;
		for (int i = 0; i < densityGroups.size(); i++)
		{
			DensityGroupWrapperSnippets densityGroup = densityGroups.get(i);
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
			DensityGroupWrapperSnippets densityGroupWrapper = densityGroups.get(i);
			double maxGroupDensity = -1;
			for (int j = 0; j < densityGroupWrapper.nInstances; j++)
			{
				DensityGroupSnippets densityGroup = densityGroupWrapper.densityGroupInstances.get(j);
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
	protected boolean addDensityGroup(DensityGroupWrapperSnippets group, TaskInfo info)
	{
		boolean ret = false;
		
		double increase = group.getExpectedMaxEvenDensity(info) - group.maxDensity;
		double decrease = info.defaultDensity;
		if (DEBUG)
		{
//			System.out.printf("%d : increase %.3f(%.3f - %.3f), decrease %.3f gain %f\n", 
//					info.taskID - 1, increase, increase + group.maxDensity, 
//					group.maxDensity, decrease, decrease - increase);
		}
		if (increase < decrease - 0.01)
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
			DensityGroupWrapperSnippets densityGroup = 
					new DensityGroupWrapperSnippets(groupHeader.period, LCM / groupHeader.period);
			densityGroups.add(densityGroup);

			boolean ret = densityGroup.insertTask(groupHeader);
			taskInfoReady.remove(0);
			if (!ret)
			{
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
				DensityGroupWrapperSnippets densityGroupW = densityGroups.get(i);
				System.out.printf("Grp %3d :", i); 
				for (int j = 0; j < densityGroupW.nInstances; j++)
				{
					DensityGroupSnippets densityGroup = densityGroupW.densityGroupInstances.get(j);
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
	
	
	
	
	
