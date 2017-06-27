package deadline_control;

import java.util.ArrayList;

public class DeadlineControlRTAS2 extends DeadlineControlRTAS{

	protected double getGain(DensityGroupWrapperSnippets group, TaskInfo info)
	{
		double increase = group.getExpectedMaxEvenDensity(info) - group.maxDensity;
		double decrease = info.defaultDensity;
		
		return decrease - increase;
		
	}

	protected void makeDensityGroup()
	{
		ArrayList<TaskInfo> taskInfoReady = new ArrayList<TaskInfo>(taskInfo);
		
		while (taskInfoReady.size() > 0)
		{
			TaskInfo info = taskInfoReady.get(0);
			double maxGain = 0;
			int maxIndex = -1;
			boolean isAdded = false;
			for (int i = 0; i < densityGroups.size(); i++)
			{
				DensityGroupWrapperSnippets examineDensityGroup = densityGroups.get(i);
				double gain = getGain(examineDensityGroup, info);
				if (gain > 0.01 && gain > maxGain)
				{
					maxGain = gain;
					maxIndex = i;
				}
			}
			// add task into the group that makes max gain
			if (maxIndex >= 0)
			{
				isAdded = addDensityGroup(densityGroups.get(maxIndex), info);
			}
			if (!isAdded)
			{
				// if info is not added into one of group then add new group
				TaskInfo groupHeader = info;
				DensityGroupWrapperSnippets densityGroup = 
						new DensityGroupWrapperSnippets(groupHeader.period, LCM / groupHeader.period);
				densityGroups.add(densityGroup);

				boolean ret = densityGroup.insertTask(groupHeader);
				if (!ret)
				{
					System.out.printf("DeadlineControl.makeDensityGroup: initial insertion fail\n");
				}
			}
			taskInfoReady.remove(0);
		}
	}
}
