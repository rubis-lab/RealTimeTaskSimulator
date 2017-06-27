package deadline_control;

import java.util.ArrayList;

public class DensityGroupWrapperSnippets extends DensityGroupWrapper{
	protected ArrayList<DensityGroupSnippets> densityGroupInstances;
	
	public DensityGroupWrapperSnippets(int period, int nInstances)
	{
		super(period, nInstances);
		densityGroupInstances = new ArrayList<DensityGroupSnippets>();
		for (int i = 0; i < nInstances; i++)
		{
			densityGroupInstances.add(new DensityGroupSnippets(period));
		}
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
			System.out.printf("DensityGroupWrapperSnippet : period is not multiple\n");

		int multiple = info.period / period;
		boolean firstInstance = true;

		for (int i = minDensityIndex % multiple; i < nInstances; i+= multiple)
		{
			DensityGroupSnippets densityGroup = densityGroupInstances.get(i);
			boolean isFit = densityGroup.insert(info);
			if (densityGroup.evenDensity > maxDensity)
				maxDensity = densityGroup.evenDensity;
			if (!isFit)
			{
				if (i != minDensityIndex)
					System.out.printf("DensityGroupWrapperSnippets : inserted task is not fit.\n");
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
			DensityGroupSnippets densityGroup = densityGroupInstances.get(i);
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
			DensityGroupSnippets densityGroup = densityGroupInstances.get(i);
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
