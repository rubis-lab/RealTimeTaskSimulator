package deadline_control;

import java.util.ArrayList;

public class DensityGroupWrapper {
	protected ArrayList<DensityGroup> densityGroupInstances;
	protected int period;
	protected int nInstances;
	protected int minDensityIndex;
	protected double minDensity;
	protected double maxDensity;
	protected ArrayList<TaskInfo> groupTasks;

	public DensityGroupWrapper(int period, int nInstances)
	{
		this.period = period;
		this.nInstances = nInstances;
		densityGroupInstances = new ArrayList<DensityGroup>();
		for (int i = 0; i < nInstances; i++)
		{
			densityGroupInstances.add(new DensityGroup(period));
		}

		minDensity = 0;
		minDensityIndex = 0;
		groupTasks = new ArrayList<TaskInfo>();
		maxDensity = 0;
	}

	protected void findMinDensityInstance()
	{
		minDensity = -1;
		for (int i = 0; i < nInstances; i++)
		{
			double density = densityGroupInstances.get(i).evenDensity;
			if (minDensity < 0 || density < minDensity)
			{
				minDensity = density;
				minDensityIndex = i;
			}
		}

	}
	public boolean insertTask(TaskInfo info)
	{
		if (info.period % period != 0) 
			System.out.printf("DensityGroupWrapper : period is not multiple\n");

		int multiple = info.period / period;
		boolean firstInstance = true;

		for (int i = minDensityIndex % multiple; i < nInstances; i+= multiple)
		{
			DensityGroup densityGroup = densityGroupInstances.get(i);
			boolean isFit = densityGroup.insert(info);
			if (densityGroup.evenDensity > maxDensity)
				maxDensity = densityGroup.evenDensity;
			if (!isFit)
			{
				if (i != minDensityIndex)
					System.out.printf("DensityGroupWrapper : inserted task is not fit.\n");
				return false;
			}
			if (firstInstance)
			{
				info.setInstancePhase(i * period);
				firstInstance=false;
			}
				
		}
		findMinDensityInstance();
		groupTasks.add(info);
		return true;
	}

	public void finalize()
	{
		for (int i = 0; i < nInstances; i++)
		{
			DensityGroup densityGroup = densityGroupInstances.get(i);
			densityGroup.calculateDeadline();
		}
		for (int i = 0; i < groupTasks.size(); i++)
		{
			groupTasks.get(i).calculatePhase();
		}
	}

	public double getExpectedMaxEvenDensity(TaskInfo info)
	{
		double expectedMaxEvenDensity = maxDensity;
		int multiple = info.period / period;
		for (int i = minDensityIndex % multiple; i < nInstances; i+= multiple)
		{
			DensityGroup densityGroup = densityGroupInstances.get(i);
			double evenDensity = densityGroup.getExpectedEvenDensity(info);
			if (evenDensity > expectedMaxEvenDensity)
				expectedMaxEvenDensity = evenDensity;
		}

		return expectedMaxEvenDensity;
	}
	public double getExpectedMinEvenDensity(TaskInfo info)
	{
		return densityGroupInstances.get(minDensityIndex).getExpectedEvenDensity(info);
	}

}
