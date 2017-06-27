package deadline_control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import data.Task;
import multicore_exp.Param;

public class TasksetSnippets {
	public class DeadlineDensitySnippet
	{		
		private int nOptions;
		private double totalExecutionTime;
		private double maxThreadExecutionTime;
		public int taskID;
		private double maxDeadline;

		/**
		 * @param nOptions the number of parallelization options
		 * @param maxThreadExecutionTime maximum execution time among nOptions threads
		 * @param totalExecutionTime the sum of the execution time of all threads
		 * @param deadline2 the original deadline of the task
		 */
		public DeadlineDensitySnippet(int nOptions, double maxThreadExecutionTime, double totalExecutionTime, double maxDeadline)
		{
			this.nOptions = nOptions;
			this.maxThreadExecutionTime = maxThreadExecutionTime;	// C^max_ik A.K.A minimum deadline
			this.totalExecutionTime = totalExecutionTime;			
			this.maxDeadline = maxDeadline;
		}
		
		/**
		 * @return possible maximum density of snippet defined as (total execution time / maximum thread execution time) 
		 */
		public double getMaxDensity()
		{
			return totalExecutionTime / maxThreadExecutionTime;
		}

		/**
		 * @return possible minimum density of snippet defined as (total execution time / Deadline) 
		 */
		public double getMinDensity() {
			return totalExecutionTime / maxDeadline;
		}
		
		/**
		 * @param deadline objective deadline
		 * @return the density of given deadline
		 */
		@Deprecated
		public double getDenstiy(double deadline)
		{
			return totalExecutionTime / deadline;
		}
		
		/**
		 * @param density objective deadline
		 * @return deadline of given density
		 */
		public double getDeadline(double density)
		{			
			if (density > getMaxDensity()) 
				return totalExecutionTime / getMaxDensity();
			double deadline = totalExecutionTime / density;
			if (deadline > this.maxDeadline) return this.maxDeadline;
			return totalExecutionTime / density;
		}
		
		/**
		 * @return the number of options of this snippet
		 */
		public int getOption()
		{
			return nOptions;
		}
		
		/**
		 * @return total execution time
		 */
		public double getTotalExecutionTime()
		{
			return totalExecutionTime;
		}

	}

	public class TaskSnippets
	{
		private ArrayList<DeadlineDensitySnippet> snippets;
		public TaskSnippets()
		{
			snippets = new ArrayList<DeadlineDensitySnippet>();
		}
		
		public void addSnippet(DeadlineDensitySnippet snippet)
		{
			snippets.add(snippet);			
		}
		
		/**
		 * @param density given density
		 * @return optimal deadline density snippet of the segment for given density
		 */
		public DeadlineDensitySnippet getOptimalSnippet(double density)
		{
			double minDeadline = -1;
			int snippetIndex = 0;
			for (int i = 0; i < snippets.size(); i++)
			{
				if (minDeadline < 0 || minDeadline > snippets.get(i).getDeadline(density))
				{
					if (snippets.get(i).maxThreadExecutionTime <= snippets.get(i).getDeadline(density))
					{
						minDeadline = snippets.get(i).getDeadline(density);
						snippetIndex = i;
					}
				}				
			}
			
			return snippets.get(snippetIndex);
		}
	}
	private ArrayList<TaskSnippets> tasksetSnippets = new ArrayList<TaskSnippets>();
	private ArrayList<DeadlineDensitySnippet> sortedSnippets = new ArrayList<DeadlineDensitySnippet>();
	private ArrayList<DeadlineDensitySnippet> optimalSnippets;
	private double period;
	private double optimalDensity;
	public TasksetSnippets(ArrayList<TaskInfo> taskset, double period)
	{			
		this.period = period;
		for (int i = 0; i < taskset.size(); i++)
		{
			TaskSnippets taskSnippets = new TaskSnippets();
			Task task = taskset.get(i).task;
			double deadline = task.getDeadline();
			for (int j = 0; j < task.getNumOptions(); j++)
			{
				int nOptions = j + 1;
				double totalExecutionTime = task.getTotalExecutionTime(0, j);				
				double maxThreadExecutionTime = task.getMaxExecutionTimeOfSegment(0, j);

				DeadlineDensitySnippet snippet = new DeadlineDensitySnippet(nOptions, maxThreadExecutionTime, 
								totalExecutionTime, deadline);
				snippet.taskID = task.getTaskID();
				taskSnippets.addSnippet(snippet);
				sortedSnippets.add(snippet);
//				deadline = maxThreadExecutionTime;
			}
			tasksetSnippets.add(taskSnippets);				
		}
		Collections.sort(sortedSnippets, Collections.reverseOrder(new Comparator<DeadlineDensitySnippet>() {
			@Override
			public int compare(DeadlineDensitySnippet  snippet1, DeadlineDensitySnippet  snippet2)
			{
				return Double.compare(snippet1.getMaxDensity(), snippet2.getMaxDensity());
			}
		}));

		findOptimalDensity();
//		setOptimalDeadline(taskset);
		
	}

	private double getTotalDeadline(double density)
	{
		double totalDeadline = 0;
		for (int i = 0; i < tasksetSnippets.size(); i++)
		{
			totalDeadline += tasksetSnippets.get(i).getOptimalSnippet(density).getDeadline(density);
		}

		return totalDeadline;
	}

	/**
	 * @param density
	 * @return a set of deadline-density-snippets. It contains each segment's proper snippet according to the given density.
	 */
	private ArrayList<DeadlineDensitySnippet> getSnippetsByDensity(double density)
	{
		ArrayList<DeadlineDensitySnippet> snippets = new ArrayList<DeadlineDensitySnippet>();
		for (int j = 0; j < tasksetSnippets.size(); j++)
		{
			snippets.add(tasksetSnippets.get(j).getOptimalSnippet(density));
		}
		return snippets;
	}		

	private ArrayList<DeadlineDensitySnippet> getSnippetsByDeadline(double deadline)
	{			
//		double feasibleDensity = -1;
//		for (int i = 0; i < sortedSnippets.size(); i++)
//		{
//			// higher density leads shorter deadline. 
//			// thus, deadline monotonically increases in this loop
//			// if the density that makes larger deadline than given is found,
//			// then very previous step is the feasible one. 
//
//			double currentDensity = sortedSnippets.get(i).getMaxDensity();				
//			if (getTotalDeadline(currentDensity) > deadline)
//			{
//				// if the first set is not feasible than there is no solution
//				if (feasibleDensity < 0) 
//					return null;
//
//				// previous set is feasible one
//				return getSnippetsByDensity(feasibleDensity);
//			}
//			feasibleDensity = currentDensity;
//		}
//
//		// all set is feasible. the last one has the smallest density
//		return getSnippetsByDensity(feasibleDensity);	
		
		double feasibleDensity = -1;
		for (int i = sortedSnippets.size() - 1; i >= 0; i --)
		{
			double currentDensity = sortedSnippets.get(i).getMaxDensity();
			if (getTotalDeadline(currentDensity) <= deadline)
			{
				return getSnippetsByDensity(currentDensity);
			}
		}
		return null;
			
	}

	public void findOptimalDensity()
	{
		optimalDensity = 0;
		optimalSnippets = getSnippetsByDeadline(period);			

		if (optimalSnippets == null)
		{
			optimalDensity = -1;
			//				System.out.println("Optimal Density = not feasible");
			return;
		}


		double maxDeadlineSum = 0;
		for (int i = 0; i < optimalSnippets.size(); i++)
		{
			optimalDensity += optimalSnippets.get(i).getTotalExecutionTime();
			maxDeadlineSum += optimalSnippets.get(i).maxDeadline;
		}
		optimalDensity /= Math.min(maxDeadlineSum, period);
		
		for (int i = 0; i < optimalSnippets.size(); i++)
		{
			if (optimalSnippets.get(i).getMinDensity() > optimalDensity)
				optimalDensity = optimalSnippets.get(i).getMinDensity();
			if (optimalDensity > Param.NumThreads_MAX)
				optimalDensity = Param.NumThreads_MAX;
		}
		
		/*			
			System.out.println("Optimal Density = " + optimalDensity);
			for (int i = 0; i < optimalSnippets.size(); i++)
				System.out.println("Segment " + (i + 1) + " : option=" + getOptimalOption(i) + " deadline=" + getIntermediateDeadline(i));
		 */				

	}
	
	public void setOptimalDeadline(ArrayList<TaskInfo> taskset)
	{
		int deadlineSum = 0;
		for (int i = 0; i < taskset.size(); i++)
		{
			for (int j = 0; j < optimalSnippets.size(); j++)
			{
				if (taskset.get(i).taskID == optimalSnippets.get(j).taskID)
				{
					int deadline =  (int)optimalSnippets.get(j).getDeadline(optimalDensity);
					taskset.get(i).setControlledDeadline(deadline);
					taskset.get(i).setOption(optimalSnippets.get(j).getOption());
					break;
				}
			}
		}
	}


	public double getOptimalDensity()
	{
		//			if (optimalDensity < 0) return Double.POSITIVE_INFINITY;
		if (optimalDensity < 0) 
			return Param.NumThreads_MAX * 2;
		return optimalDensity;
	}

	public int getOptimalOption(int taskIndex)
	{
		return optimalSnippets.get(taskIndex).getOption();
	}

	public double getIntermediateDeadline(int segmentIndex)
	{
		return optimalSnippets.get(segmentIndex).getDeadline(optimalDensity);
	}
	
	public static double getOptimalDensity(ArrayList<TaskInfo> taskset, int period)
	{
		TasksetSnippets tasksetSnippet = new TasksetSnippets(taskset, period);
		return tasksetSnippet.getOptimalDensity();
	}

	public static double getOptimalDensity(ArrayList<TaskInfo> taskset, int period, TaskInfo newtask)
	{
		ArrayList<TaskInfo> newtaskset = new ArrayList<TaskInfo>(taskset);
		newtaskset.add(newtask);
		TasksetSnippets tasksetSnippet = new TasksetSnippets(newtaskset, period);
		return tasksetSnippet.getOptimalDensity();
	}

}
