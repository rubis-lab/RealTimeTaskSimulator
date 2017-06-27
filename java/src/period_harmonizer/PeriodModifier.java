package period_harmonizer;

import data.TaskSet;

public class PeriodModifier {
	private int[] basePeriod;
	private int optimalPeriod;
	
	
	public PeriodModifier(int[] basePeriod)
	{
		this.basePeriod = basePeriod;
	}
	
	public PeriodModifier(int numPrime)
	{
		basePeriod = new int[numPrime];
		
		int num = 2;
		int numCollectedPrime = 0;
		do
		{
			boolean isPrime = true;
			for (int i = 0; i < numCollectedPrime; i++)
			{
				if (num % basePeriod[i] == 0)
				{
					isPrime = false;
					break;
				}
			}
			if (isPrime)
			{
				basePeriod[numCollectedPrime] = num;
				numCollectedPrime ++;
			}
			num ++;
		}while(numCollectedPrime < numPrime);
	}
	
	
	public int modifyPeriod(int period)
	{
		optimalPeriod = 0;
		
		modifyPeriod(1, period, 0);
		
		return optimalPeriod;
		
	}
	
	private void modifyPeriod(int periodInProgress, int period, int basePeriodIndex)
	{
		if (basePeriodIndex >= basePeriod.length) return;
		
		do
		{
			modifyPeriod(periodInProgress, period, basePeriodIndex + 1);

			if (period - periodInProgress < period - optimalPeriod)
				optimalPeriod = periodInProgress;

			periodInProgress *= basePeriod[basePeriodIndex];
		}while (periodInProgress <= period);
	}
	
	public void modifyPeriod(TaskSet taskSet)
	{
		for (int i = 0; i < taskSet.size(); i++)
		{
			int period = taskSet.get(i).getPeriod();
			int modifiedPeriod = modifyPeriod(period);
			taskSet.get(i).setPeriod(modifiedPeriod);
		}
	}
}
