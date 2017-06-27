package period_harmonizer;

import java.util.ArrayList;

import data.Task;

public class IntervalSet implements Comparable<IntervalSet>{

	protected ArrayList<Interval> intervalSet;
	protected Interval baseInterval;
	protected int min, max;
	protected int taskIndex; 
	
	
	protected void init(int taskIndex, Interval baseInterval)
	{
		this.taskIndex = taskIndex;
		this.baseInterval = baseInterval;
		intervalSet = new ArrayList<Interval>();
		min = -1;
		max = 0;
	}
		
	
	public IntervalSet(int taskIndex, Interval baseInterval)
	{
		init(taskIndex, baseInterval);
	}
	
	public IntervalSet(int taskIndex, Task task)
	{
		init(taskIndex, new Interval(task));
	}
	
	public int getTaskIndex()
	{
		return taskIndex;
	}
	
	protected void addInterval(Interval interval)
	{
		if (min < 0 || interval.min < min)
			min = interval.min;
		if (interval.max > max)
			max = interval.max;

		boolean doMerge = false;
		for (int i = 0; i < intervalSet.size(); i++)
		{
			doMerge = intervalSet.get(i).merge(interval);
			if (doMerge) break;
		}
		
		if (!doMerge)
			intervalSet.add(interval);
	}

	public void makeIntervalSet(IntervalSet nextIntervalSet)
	{
		if (nextIntervalSet == null)
		{
			addInterval(baseInterval);
			return;
		}
		int minMultiplier = (int)(nextIntervalSet.min / baseInterval.max) + 1;
		int maxMultiplier = (int)(Math.ceil(nextIntervalSet.max / (double)baseInterval.min)) - 1;
		ArrayList<Interval> initialIntervals = new ArrayList<Interval>();

		for (int multiplier = minMultiplier; multiplier <= maxMultiplier; multiplier++)
		{
			initialIntervals.add(baseInterval.multiple(multiplier));
		}
		
		for (int i = 0; i < nextIntervalSet.intervalSet.size(); i++)
		{
			Interval nextInterval = nextIntervalSet.intervalSet.get(i);
			for (int  j = 0; j < initialIntervals.size(); j++)
			{
				Interval crossInterval = nextInterval.cross(initialIntervals.get(j));
				if (crossInterval != null)
				{
					int multiplier = (int)(Math.ceil(crossInterval.max / (double)baseInterval.max)); 
					addInterval(crossInterval.divide(multiplier));
				}
			}
		}
	}
	
	public int getResultPeriod(int previousPeriod)
	{
		if (previousPeriod == 0) return max;
		
		return (int)(max / previousPeriod) * previousPeriod;
	}

	@Override
	public int compareTo(IntervalSet obj) {
		int ret;
		ret = max - obj.max;
		if (ret == 0)
			ret = min - obj.min;

		return ret;
	}	
}
