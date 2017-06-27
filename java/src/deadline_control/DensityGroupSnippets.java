package deadline_control;

import java.util.ArrayList;


public class DensityGroupSnippets extends DensityGroup {

	public DensityGroupSnippets(int period) {
		super(period);
	}
	
	public double getEvenDensity()
	{
		return TasksetSnippets.getOptimalDensity(taskInfo, period);
	}
	
	public double getExpectedEvenDensity(TaskInfo info)
	{
		return TasksetSnippets.getOptimalDensity(taskInfo, period, info);
	}
	
	public boolean insert(TaskInfo info)
	{
		ArrayList<TaskInfo> newTaskSet = new ArrayList<TaskInfo>(taskInfo);
		newTaskSet.add(info);
		
		TasksetSnippets tasksetSnippets = new TasksetSnippets(newTaskSet, period);
		
		
		double expectedDensityEven = tasksetSnippets.getOptimalDensity();
		if (taskInfo.size() == 0)
			expectedDensityEven = info.defaultDensity;
		
		for (int i = 0; i < newTaskSet.size(); i++)
			if (expectedDensityEven > newTaskSet.get(i).densityMax) 
				return false;
		
		if (taskInfo.size() > 0)
			info.setPreviousTask(taskInfo.get(taskInfo.size() - 1));
		
		taskInfo.add(info);
		evenDensity = expectedDensityEven;
		return true;
	}
	
	protected void calculateDeadline()
	{
		if (taskInfo.size() == 1)
		{
			taskInfo.get(0).useDefault();
			evenDensity = taskInfo.get(0).densityMin;
			return;
		}
		TasksetSnippets tasksetSnippets = new TasksetSnippets(taskInfo, period);
		tasksetSnippets.setOptimalDeadline(taskInfo);
		
	}

}
