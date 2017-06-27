package logic;

import multicore_exp.Param;
import multicore_exp.Util;
import data.Segment;
import data.Task;
import data.TaskSet;

public class SHIN {
	
	public static TaskSet SampleTaskGenerate()
	{
		TaskSet taskSet = new TaskSet();
		for (int i=0; i<50; i++)
		{
			Task task = new Task(4);
			
			task.setTaskID(i);		
			task.setPeriod(8);
			task.setDeadline(8);
	

			task.setExecutionTime(0, 0, 0, 1);
	
			task.setExecutionTime(1, 2, 0, 2);
			task.setExecutionTime(1, 2, 1, 2);
			task.setExecutionTime(1, 2, 2, 2);
	
			task.setExecutionTime(2, 1, 0, 1);
			task.setExecutionTime(2, 1, 1, 1);
	
			task.setExecutionTime(3, 0, 0, 2);
			
			task.selectOption(0, 1-1);
			task.selectOption(1, 3-1);
			task.selectOption(2, 2-1);
			task.selectOption(3, 1-1);
	
			taskSet.add(task);
		}
		return taskSet;
	}

	public static TaskSet SampleTaskGenerate2()
	{
		TaskSet taskSet = new TaskSet();
		
		Task task1 = new Task(3);		
		task1.setTaskID(1);		
		task1.setPeriod(3);
		task1.setDeadline(3);
		
		task1.setExecutionTime(0, 0, 0, 1);
		task1.setExecutionTime(1, 1, 0, 1);
		task1.setExecutionTime(1, 1, 1, 1);
		task1.setExecutionTime(2, 0, 0, 1);
		
		task1.selectOption(0, 0);
		task1.selectOption(1, 1);
		task1.selectOption(2, 0);		
		taskSet.add(task1);
	
		
		Task task2 = new Task(2);		
		task2.setTaskID(2);		
		task2.setPeriod(3);
		task2.setDeadline(3);
		
		task2.setExecutionTime(0, 0, 0, 1);
		task2.setExecutionTime(1, 0, 0, 1);
		
		task2.selectOption(0, 0);
		task2.selectOption(1, 0);
		taskSet.add(task2);
		
		return taskSet;
	}
	public static TaskSet SampleTaskGenerate3()
	{
		TaskSet taskSet = new TaskSet();
		
		Task task1 = new Task(1);		
		task1.setTaskID(1);		
		task1.setPeriod(4);
		task1.setDeadline(3);
		
		task1.setExecutionTime(0, 1, 0, 2);
		task1.setExecutionTime(0, 1, 1, 2);
				
		task1.selectOption(0, 1);
		taskSet.add(task1);
	
		
		Task task2 = new Task(1);		
		task2.setTaskID(2);		
		task2.setPeriod(4);
		task2.setDeadline(4);
		
		task2.setExecutionTime(0, 1, 0, 2);
		task2.setExecutionTime(0, 1, 1, 2);
		//task2.setExecutionTime(1, 0, 0, 1);
		
		task2.selectOption(0, 1);
		//task2.selectOption(1, 0);
		taskSet.add(task2);
		
		return taskSet;
	}
	
	public static void SelectOptionRandom(TaskSet set)
	{
		for (int i=0; i<set.size(); i++)
		{
			Task task = set.get(i);
			Util util = new Util(i);
			for (int k=0; k<task.getNumSegments(); k++)
			{
				int optionIndex = util.randomInt(0, Param.NumProcessors-1);
				task.selectOption(k, optionIndex);
				//System.out.println("Task:" + i + " Segment:" + k + " Option:" + (optionIndex+1));
			}			
		}
	}
	public static void SelectOptionMax(TaskSet set)
	{
		for (int i=0; i<set.size(); i++)
		{
			Task task = set.get(i);
			for (int k=0; k<task.getNumSegments(); k++)
			{
				task.selectOption(k, Param.NumProcessors-1);
				//System.out.println("Task:" + i + " Segment:" + k + " Option:" + (optionIndex+1));
			}			
		}
	}
	public static void SelectOptionSingle(TaskSet set)
	{
		for (int i=0; i<set.size(); i++)
		{
			Task task = set.get(i);
			for (int k=0; k<task.getNumSegments(); k++)
			{				
				task.selectOption(k, 0);
				//System.out.println("Task:" + i + " Segment:" + k + " Option:" + (optionIndex+1));
			}			
		}
	}
	
	
	public static int getNumSchedulableTasks(TaskSet taskSet) {
		TaskSet subTaskSet = new TaskSet();
		for (int i = 0; i < taskSet.size(); i++)
		{			
			Task task = taskSet.get(i);
			subTaskSet.add(task);
			if (!isSchedulable(subTaskSet))
			{				
				//for(int j=0; j<subTaskSet.size(); j++)
				//	System.out.println(subTaskSet.get(j).toString());
				
				return i;
			}
		}
		return taskSet.size();
	}		
	
	
	public static boolean isSchedulable(TaskSet taskSet) {

		
		for(int k=0; k<taskSet.size(); k++)
		{
			Task task_k = taskSet.get(k);

			double LC_k = task_k.getMaxExecutionTime();
			double D_k = task_k.getDeadline();

			if (D_k - LC_k < 0)
				return false;

			double interTask = 0;
			for(int i=0; i<taskSet.size(); i++)
			{
				if (i == k)
					continue;

				Task task_i = taskSet.get(i);
				
				// p-depth starts from 1
				for (int p=1; p<= task_i.getMaxNumOfThreads(); p++)
				{
					interTask += Math.min(W_i_k(task_i, p, D_k), D_k - LC_k);
				}							
			}

			double intraTask = 0;
			for (int p=1; p<=task_k.getMaxNumOfThreads(); p++)
			{
				intraTask += Math.min(W_k_k(task_k, p, D_k), D_k - LC_k);
			}

			//System.out.print("\tTask:" + (k+1) + "\tPF=" + task_k.getTotalExecutionTime() / task_k.getMaxExecutionTime());
			//System.out.println("\tCond: " + interTask + " + " + intraTask + " <> " + Param.NumProcessors * (D_k - LC_k) + " (D_k-LC_k=" + (D_k-LC_k) + ")");

			//if(interTask == 0 && intraTask == 0 && D_k == LC_k)
			//	return true;
				
			if (interTask + intraTask >= Param.NumProcessors * (D_k - LC_k))
			{
				if (Param.DebugMessage)
				{
					System.out.print("\tTask:" + (k+1) + "\tPF=" + task_k.getTotalExecutionTime() / task_k.getMaxExecutionTime());
					System.out.println("\tCond: " + interTask + " + " + intraTask + " <> " + Param.NumProcessors * (D_k - LC_k) + " (D_k-LC_k=" + (D_k-LC_k) + ")");
				}
				return false;
			}
		}		
		
		return true;
	}		
	
	/**
	 * @param task_i task_i
	 * @param p p-depth
	 * @param D_k any interval length
	 * @return a bound on the at least p-depth body-job workload in any interval of length D_k
	 */
	public static double BD_i_k(Task task_i, int p, double D_k)
	{
		double leftTerm = Math.floor( D_k / task_i.getPeriod() );
		double rightTerm = 0;
				
		for (int i=0; i<task_i.getNumSegments(); i++)
		{
			if (task_i.getNumThreadsOfSegment(i) >= p)
				rightTerm += task_i.getMaxExecutionTimeOfSegment(i);
		}
				
		return leftTerm * rightTerm;
	}
	
	
	/**
	 * @param task
	 * @param L
	 * @return the index of the earliest segment that is fully included in the carry-in interval L
	 */
	public static int get_h(Task task, double L)
	{
		double L_intermediate = L;
		for (int i=task.getNumSegments()-1; i>=0; i--)
		{
			L_intermediate -= task.getMaxExecutionTimeOfSegment(i);
			if (L_intermediate < 0)
				return i+1;
			else if (L_intermediate == 0)
				return i;
		}
		return 0;
	}
	
	/**
	 * @param task_i
	 * @param p
	 * @param L
	 * @return bound on the at least p-depth carry-in workload in any carry-in interval of length L
	 */
	public static double CI_i_k(Task task_i, int p, double L)
	{
		double value = 0;
	
		if (L <= 0) {
			return 0;
		} else if (L <= task_i.getMaxExecutionTime()) {
			// h indicates the earliest segment index that is fully included in the carry-in interval
			int h = get_h(task_i, L);
			
			int h_minus_1 = Math.max(0, h-1);
			int m_i_h_minus_1 = task_i.getNumThreadsOfSegment(h_minus_1);

			for(int j=h; j < task_i.getNumSegments(); j++) {					
				if (task_i.getNumThreadsOfSegment(j) >= p)
					value += task_i.getMaxExecutionTimeOfSegment(j);
			}				
			
			if (m_i_h_minus_1 < p) {
				return value;
			} else {
				double temp = 0;
				for(int j=h; j < task_i.getNumSegments(); j++) {
					temp += task_i.getMaxExecutionTimeOfSegment(j);
				}
				return value + L - temp;
			}
		}		
		
		for(int i=0; i<task_i.getNumSegments(); i++)
		{
			if (task_i.getNumThreadsOfSegment(i) >= p)
				value += task_i.getMaxExecutionTimeOfSegment(i);
		}
		
		return value;
	}
		
	/**
	 * @param task_i
	 * @param p
	 * @param D_k
	 * @return at least p-depth workload that will contribute to the worst case for (i != k)
	 */
	public static double W_i_k(Task task_i, int p, double D_k)
	{		
		return BD_i_k(task_i, p, D_k) + CI_i_k(task_i, p, D_k % task_i.getPeriod());
	}
	
	
	/**
	 * @param task_k
	 * @param p
	 * @param D_k
	 * @return workload that will contribute to the worst case
	 */
	public static double W_k_k(Task task_k, int p, double D_k)
	{
		double value = 0;
		
		for (int i=0; i<task_k.getNumSegments(); i++)
		{
			if (task_k.getNumThreadsOfSegment(i) >= p+1)
				value += task_k.getMaxExecutionTimeOfSegment(i);
		}
		return value;
	}
}
