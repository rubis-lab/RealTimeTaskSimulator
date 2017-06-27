package period_harmonizer;

import data.TaskSet;

public class ExponentialHarmonize extends PeriodHarmonizer {

	@Override
	public boolean harmonize(TaskSet taskSet) {
		// TODO Auto-generated method stub
		long GCD = getGCD(taskSet);
		
		for (int i = 0; i < taskSet.size(); i++)
		{
			int period = taskSet.get(i).getPeriod();
			int periodMultiplier = period / (int)GCD;
					
			double newPeriodExponential = Math.floor(Math.log(periodMultiplier) / Math.log(2));
			double newPeriod = GCD * (int)Math.pow(2, newPeriodExponential);

			setHarmonizedPeriod(taskSet.get(i), (int)newPeriod);
		}
		return true;

	}

}
