package deadline_control;

import java.util.ArrayList;

public class DensityGroup {
	protected ArrayList<TaskInfo> taskInfo;
	protected double sumC;
	protected double sumCAlpha; 
	protected int period;
	protected double dMax;
	protected double evenDensity;

	DensityGroup(int period)
	{
		taskInfo = new ArrayList<TaskInfo>();
		this.period = period;
		sumC = 0;
		sumCAlpha = 0;
		dMax = 0;
	}

	public int size()
	{
		return taskInfo.size();
	}
	public TaskInfo get(int index)
	{
		return taskInfo.get(index);
	}

	public double getEvenDensity()
	{
		return (sumC - sumCAlpha) / (period - sumCAlpha);
	}
	public double getExpectedEvenDensity(TaskInfo info)
	{
		double tempSumC = sumC + info.executionTime;
		double tempSumCAlpha = sumCAlpha + info.executionTime * info.pOverhead;
		double tempDMax = dMax + info.deadline;
		double densityEven = (tempSumC - tempSumCAlpha) / 
					(Math.min(period, tempDMax) - tempSumCAlpha);

		return densityEven;
	}

	public boolean insert(TaskInfo info)
	{
		//find density even point
		double tempSumC = sumC + info.executionTime;
		double tempSumCAlpha = sumCAlpha + info.executionTime * info.pOverhead;
		double tempDMax = dMax + info.deadline;
		double expectedDensityEven = (tempSumC - tempSumCAlpha) / 
				(Math.min(period,tempDMax) - tempSumCAlpha);

		if (tempSumCAlpha > period) return false;

		// check even density is smaller than all tasks' possible max density
		// arithmetically, densityMax is oMax 
		for (int i = 0; i < taskInfo.size(); i++)
		{
			if (expectedDensityEven > taskInfo.get(i).densityMax)
				return false;
		}

		if (expectedDensityEven > info.densityMax) return false;
		/*
//			if (info.taskID == 15)
			{
				System.out.printf("add task %d\n", info.taskID);
				System.out.printf("sumC : %f->%f\n", sumC, tempSumC);
				System.out.printf("sumCAlpha : %f->%f\n", sumCAlpha, tempSumCAlpha);
				System.out.printf("evenDensity : %f->%f\n", evenDensity, expectedDensityEven);

			}
		 */
		if (taskInfo.size() > 0)
			info.setPreviousTask(taskInfo.get(taskInfo.size() - 1));
		
		taskInfo.add(info);
		sumC = tempSumC;
		sumCAlpha = tempSumCAlpha;
		dMax = tempDMax;
		evenDensity = expectedDensityEven;


		return true;
	}
	protected void calculateDeadline()
	{
		// if density group has only one task, don't use harmonized period
		if (taskInfo.size() == 1)
		{
			taskInfo.get(0).useDefault();
			evenDensity = taskInfo.get(0).densityMin;
			return;
		}
		for (int i = 0; i < taskInfo.size(); i++)
		{
			TaskInfo info = taskInfo.get(i);
			double c = info.executionTime;
			double alpha = info.pOverhead;
			double cAlpha = c * alpha;
			double dCont = (c - cAlpha) / evenDensity + cAlpha; 
			double d = Math.floor(dCont);
			if (d > info.dMax) d = info.dMax;
			info.setControlledDeadline((int)d);

			info.setOption ((int)Math.ceil((c - cAlpha) / (d - cAlpha)));

		}
	}
}
