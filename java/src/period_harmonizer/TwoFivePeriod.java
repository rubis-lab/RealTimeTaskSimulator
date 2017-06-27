package period_harmonizer;

import java.util.ArrayList;
import java.util.Collections;

import data.Task;
import data.TaskSet;


public class TwoFivePeriod {
	protected ArrayList<Integer> candidates;
	protected int limit = 100000000;
	public TwoFivePeriod()
	{
		candidates = new ArrayList<Integer>();
		for (int i = 0; i < 100000; i++)
		{
			int val2 = (int)Math.pow(2, i);
			if (val2 > limit) break;
			for (int j = 0; j < 100000; j++)
			{
				int val5 = (int)Math.pow(5, j);
				if (val2 * val5 > limit) break;
				candidates.add(val2 * val5);
			}
		}
		Collections.sort(candidates);
	}

	public void modify(Task task)
	{
		int period = task.getPeriod();
		for (int i = 0; i < candidates.size(); i++)
		{
			if (candidates.get(i) >= period)
			{
				task.setPeriod(candidates.get(i));
//				System.out.printf("%d -> %d\n", period, candidates.get(i));
				break;
			}
		}
	}
	
	public void modify(TaskSet taskSet)
	{
		for (int i = 0; i < taskSet.size(); i++)
		{
			modify(taskSet.get(i));
		}
	}
}
