package period_harmonizer;

import data.Task;

public class Interval {
	public int min, max;
	public Interval(int min, int max)
	{
		this.min = min;
		this.max = max;
	}
	public Interval(Task task)
	{
			int nOption = task.getNumOptions();
			min = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
			max = task.getPeriod();
	}
	public Interval multiple(int multiplier)
	{
		return new Interval(this.min * multiplier, this.max * multiplier);
	}
	public boolean merge(Interval interval)
	{
		if (interval.min > max) return false;
		if (interval.max < min) return false;
		
		min = Math.min(interval.min, min);
		max = Math.max(interval.max, max);
		return true;
	}
	public Interval cross(Interval interval)
	{
		if (interval.min > max) return null;
		if (interval.max < min) return null;
		
		int crossMin = Math.max(interval.min, min);
		int crossMax = Math.min(interval.max, max);
		
		return new Interval(crossMin, crossMax);
	}
	public Interval divide(int multiplier)
	{
		return new Interval(min / multiplier, max / multiplier);
	}
}
