package period_harmonizer;

import java.util.ArrayList;

import data.Task;
import data.TaskSet;
import deadline_control.TaskInfo;

public class IntervalSearchBackward {

	
	public boolean backwardInterval(ArrayList<TaskInfo> taskSet)
	{
		ArrayList<IntervalSet> intervalSets = new ArrayList<IntervalSet>();
		for (int i = 0; i < taskSet.size(); i++)
		{
			TaskInfo task = taskSet.get(i);
			int	minPeriod = task.task.getExecutionTimeOfThread(0, 0, 0);
			int maxPeriod = task.task.getRealPeriod();
			if (minPeriod > maxPeriod) return false;
			Interval baseInterval = new Interval(minPeriod, maxPeriod);
			IntervalSet initialIntervalSet = new IntervalSet(i, baseInterval);
			intervalSets.add(initialIntervalSet);
//			System.out.print(taskSet.get(i).getPeriod() + "(" + minPeriod +"~" + maxPeriod + ")\t");
		}
		
		//sort ascending order of minPeriod;
		for (int i = 0; i < intervalSets.size() - 1; i++)
		{
			for (int j = i + 1; j < intervalSets.size(); j++)
			{
				if (intervalSets.get(i).baseInterval.max > intervalSets.get(j).baseInterval.max)
				{
					IntervalSet temp = intervalSets.get(i);
					intervalSets.set(i, intervalSets.get(j));
					intervalSets.set(j, temp);
				}
			}
		}
		
//		System.out.println();
//		for (int i = 0; i < intervalSets.size(); i++)
//		{
//			System.out.println("sort : " + intervalSets.get(i).getTaskIndex());
//		}
		

		// initialize intervalSet
		for (int i = intervalSets.size() - 1; i >= 0; i--)
		{
			IntervalSet intervalSet = intervalSets.get(i);
			if (i == intervalSets.size() - 1)
				intervalSet.makeIntervalSet(null);
			else
				intervalSet.makeIntervalSet(intervalSets.get(i + 1));
		}
		
		// find and set period
//		System.out.println();
//		for (int i = 0; i < intervalSets.size(); i++)
//		{
//			System.out.println("Interval Set for task index " + intervalSets.get(i).getTaskIndex());
//			for (int j = 0; j < intervalSets.get(i).intervalSet.size(); j++)
//			{
//				Interval interval = intervalSets.get(i).intervalSet.get(j);
//				System.out.println("\t" + interval.min + " - " + interval.max);
//			}
//			System.out.println();
//		}
			

		int previousPeriod = 0;
		for (int i = 0; i < intervalSets.size(); i++)
		{
			IntervalSet intervalSet = intervalSets.get(i);

			// zero for initial value of previous period;
			int harmonizedPeriod = intervalSet.getResultPeriod(previousPeriod);
			
			taskSet.get(intervalSet.getTaskIndex()).setHarmonizedPeriod(harmonizedPeriod);
//			PeriodHarmonizer.setHarmonizedPeriod(taskSet.get(intervalSet.getTaskIndex()), harmonizedPeriod);
			previousPeriod = harmonizedPeriod;
		}
//		for (int i = 0; i < taskSet.size(); i++)
//		{
//			System.out.print(taskSet.get(i).getPeriod() + "\t");
//		}
//		System.out.println();
		return true;
	}
	// this method only works with single segment tasks
	public boolean backwardInterval(TaskSet taskSet, TaskSet.ParallelizedOption option)
	{
		ArrayList<IntervalSet> intervalSets = new ArrayList<IntervalSet>();
		for (int i = 0; i < taskSet.size(); i++)
		{
			Task task = taskSet.get(i);
			int nOption = task.getNumOptions();
			int minPeriod = 0;
			
			switch(option)
			{
			case SINGLE:
				minPeriod = task.getMaxExecutionTimeOfSegment(0, 0);
				break;
			case MAX:
				minPeriod = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
				break;
			case BEST_EFFORT:
				minPeriod = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
				for (int j = 0; j < nOption; j++)
				{
					int e = task.getMaxExecutionTimeOfSegment(0, j);
					if (e <= task.getDeadline())
					{
						minPeriod = e;
						break;
					}
				}
				break;
			default:
				System.out.println("Not Defined option. backwardInterval");
			}
					
				
//			int minPeriod = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
//			int minPeriod = task.getDeadline();
			int maxPeriod = task.getPeriod();
			if (minPeriod > maxPeriod) return false;
			Interval baseInterval = new Interval(minPeriod, maxPeriod);
			IntervalSet initialIntervalSet = new IntervalSet(i, baseInterval);
			intervalSets.add(initialIntervalSet);
//			System.out.print(taskSet.get(i).getPeriod() + "(" + minPeriod +"~" + maxPeriod + ")\t");
		}
		
		//sort ascending order of minPeriod;
		for (int i = 0; i < intervalSets.size() - 1; i++)
		{
			for (int j = i + 1; j < intervalSets.size(); j++)
			{
				if (intervalSets.get(i).baseInterval.max > intervalSets.get(j).baseInterval.max)
				{
					IntervalSet temp = intervalSets.get(i);
					intervalSets.set(i, intervalSets.get(j));
					intervalSets.set(j, temp);
				}
			}
		}
		
//		System.out.println();
//		for (int i = 0; i < intervalSets.size(); i++)
//		{
//			System.out.println("sort : " + intervalSets.get(i).getTaskIndex());
//		}
		

		// initialize intervalSet
		for (int i = intervalSets.size() - 1; i >= 0; i--)
		{
			IntervalSet intervalSet = intervalSets.get(i);
			if (i == intervalSets.size() - 1)
				intervalSet.makeIntervalSet(null);
			else
				intervalSet.makeIntervalSet(intervalSets.get(i + 1));
		}
		
		// find and set period
//		System.out.println();
//		for (int i = 0; i < intervalSets.size(); i++)
//		{
//			System.out.println("Interval Set for task index " + intervalSets.get(i).getTaskIndex());
//			for (int j = 0; j < intervalSets.get(i).intervalSet.size(); j++)
//			{
//				Interval interval = intervalSets.get(i).intervalSet.get(j);
//				System.out.println("\t" + interval.min + " - " + interval.max);
//			}
//			System.out.println();
//		}
			

		int previousPeriod = 0;
		for (int i = 0; i < intervalSets.size(); i++)
		{
			IntervalSet intervalSet = intervalSets.get(i);

			// zero for initial value of previous period;
			int harmonizedPeriod = intervalSet.getResultPeriod(previousPeriod);
			
			PeriodHarmonizer.setHarmonizedPeriod(taskSet.get(intervalSet.getTaskIndex()), harmonizedPeriod);
			previousPeriod = harmonizedPeriod;
		}
//		for (int i = 0; i < taskSet.size(); i++)
//		{
//			System.out.print(taskSet.get(i).getPeriod() + "\t");
//		}
//		System.out.println();
		return true;
	}
	public void backwardInterval_old(TaskSet taskSet)
	{
		ArrayList<IntervalSet> intervalSets = new ArrayList<IntervalSet>();
		for (int i = 0; i < taskSet.size(); i++)
		{
			Task task = taskSet.get(i);
			int nOption = task.getNumOptions();
			int minPeriod = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
			int maxPeriod = task.getPeriod();
			Interval baseInterval = new Interval(minPeriod, maxPeriod);
			IntervalSet initialIntervalSet = new IntervalSet(i, baseInterval);
			intervalSets.add(initialIntervalSet);
//			System.out.print(taskSet.get(i).getPeriod() + "(" + minPeriod +"~" + maxPeriod + ")\t");
		}
		
		//sort ascending order of minPeriod;
		for (int i = 0; i < intervalSets.size() - 1; i++)
		{
			for (int j = i + 1; j < intervalSets.size(); j++)
			{
				if (intervalSets.get(i).baseInterval.max > intervalSets.get(j).baseInterval.max)
				{
					IntervalSet temp = intervalSets.get(i);
					intervalSets.set(i, intervalSets.get(j));
					intervalSets.set(j, temp);
				}
			}
		}
		
		System.out.println();
		for (int i = 0; i < intervalSets.size(); i++)
		{
			System.out.println("sort : " + intervalSets.get(i).getTaskIndex());
		}
		

		// initialize intervalSet
		for (int i = intervalSets.size() - 1; i >= 0; i--)
		{
			IntervalSet intervalSet = intervalSets.get(i);
			if (i == intervalSets.size() - 1)
				intervalSet.makeIntervalSet(null);
			else
				intervalSet.makeIntervalSet(intervalSets.get(i + 1));
		}
		
		// find and set period
		System.out.println();
		for (int i = 0; i < intervalSets.size(); i++)
		{
			System.out.println("Interval Set for task index " + intervalSets.get(i).getTaskIndex());
			for (int j = 0; j < intervalSets.get(i).intervalSet.size(); j++)
			{
				Interval interval = intervalSets.get(i).intervalSet.get(j);
				System.out.println("\t" + interval.min + " - " + interval.max);
			}
			System.out.println();
		}
			

		int previousPeriod = 0;
		for (int i = 0; i < intervalSets.size(); i++)
		{
			IntervalSet intervalSet = intervalSets.get(i);

			// zero for initial value of previous period;
			int harmonizedPeriod = intervalSet.getResultPeriod(previousPeriod);
			
			PeriodHarmonizer.setHarmonizedPeriod(taskSet.get(intervalSet.getTaskIndex()), harmonizedPeriod);
			previousPeriod = harmonizedPeriod;
		}
		for (int i = 0; i < taskSet.size(); i++)
		{
			System.out.print(taskSet.get(i).getPeriod() + "\t");
		}
		System.out.println();
	}
}
