package data;

import java.util.ArrayList;
import multicore_exp.Util;

public class TaskSet implements Cloneable {
		
	public enum ParallelizedOption
	{
		SINGLE,
		MAX,
		RANDOM,
		BEST_EFFORT,
		SELECTED,
	}
	
	protected ArrayList<Task> listTasks = new ArrayList<Task>();
	
	protected double alpha; // parallelization overhead
	protected double beta;  // deadline tightness
	protected double gamma; // period tightness
	protected int taskNum;
	protected int taskSetID;
	protected double totalPeakDensity;
	
	public TaskSet(double alpha, double beta, double gamma)
	{
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
	}
	
	public TaskSet(int taskSetID, double alpha, double beta, double gamma)
	{
		this.taskSetID = taskSetID;
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
	}
	
	
	public TaskSet()
	{
		 listTasks = new ArrayList<Task>();
	}
	
	public TaskSet copyTaskset()
	{
		TaskSet newtaskset = new TaskSet(-1, this.alpha, this.beta, this.gamma);
//		newtaskset.listTasks = this.listTasks;
//		ArrayList listTasksCopy = new ArrayList();
		ArrayList<Task> copyListTasks = (ArrayList<Task>)this.listTasks.clone();
				
		newtaskset.listTasks = copyListTasks;
		newtaskset.taskNum = this.taskNum;
		newtaskset.totalPeakDensity = this.totalPeakDensity;
		
		return newtaskset;
	}

	
	public void setTaskSetID(int taskSetID)
	{
		this.taskSetID = taskSetID;
	}
	
	public void add(Task task)
	{
		this.taskNum++;
		listTasks.add(task);
	}
	
	public Task get(int index)
	{
		return listTasks.get(index);
	}
	
	public void setSelectOption(int index, int segmentIndex, int optionIndex)
	{
		listTasks.get(index).selectOption(segmentIndex, optionIndex);
	}
	
	public double getAlpha()
	{
		return alpha;
	}
	
	public double getBeta()
	{
		return beta;
	}
	
	public double getGamma()
	{
		return gamma;
	}
	
	public int getTaskSetID()
	{
		return taskSetID;
	}
	
	public int size()
	{
		return listTasks.size();
	}
	public int getTaskNum()
	{
		return this.taskNum;
	}
	public void setTaskNum(int taskNum)
	{
		this.taskNum = taskNum;
	}
	
	public void setTotalPeakDensity(double totalPeakDensity)
	{
		this.totalPeakDensity = totalPeakDensity;
	}
	public double getTotalPeakDensity()
	{
		return this.totalPeakDensity;
	}

	public void setListTasks(ArrayList<Task> listTasks)
	{
		this.listTasks = listTasks;
	}
	
	public ArrayList<Task> getListTasks()
	{
		return this.listTasks;
	}
	
	public Object clone() throws CloneNotSupportedException 
	{
		return super.clone();
	}
	
	public String toString()
	{
		String buffer = "";
		for (int i = 0; i < listTasks.size(); i++)
		{
			buffer += listTasks.get(i).toString();
		}
		
		return buffer;
	}
	

	public ArrayList<SporadicTask> toSporadicTask(int taskSetID, ParallelizedOption option)
	{
		Util util = new Util(0);

		ArrayList<SporadicTask> sporadicTaskSet = new ArrayList<SporadicTask>();
		
		for (int i = 0; i < listTasks.size(); i++)
		{
			int p = listTasks.get(i).getPeriod();
			int d = listTasks.get(i).getIntermediateDeadline(0);
			int numOptions = 1;

			switch(option)
			{
			case SINGLE:
				numOptions = 1;
				break;
			case MAX:
				numOptions = listTasks.get(i).getNumOptions();
				break;
			case RANDOM:
				numOptions = util.randomInt(1, listTasks.get(i).getNumOptions());
				break;
			case BEST_EFFORT:
			{
				Task task = listTasks.get(i);
				for (int j = 0; j < task.getNumOptions(); j++)
				{
					// in this method, single segment task is assumed
					if (task.getMaxExecutionTimeOfSegment(0, j) < task.getDeadline())
					{
						numOptions = j + 1;
						break;
					}
				}
				break;
			}
			case SELECTED:
			{
				Task task = listTasks.get(i);
				numOptions = task.selectedOption(0) + 1;
				d = listTasks.get(i).getIntermediateDeadline(0);
				break;
			}
			default:
				
			}
			
			int e;

			for (int j = 0; j < numOptions; j++)
			{
				e = listTasks.get(i).getExecutionTimeOfThread(0, numOptions-1, j);
				if (e > d) return null;
				SporadicTask st = new SporadicTask(p, e, d);
				st.taskID = i+1;
				st.taskSetID = taskSetID+1;
				sporadicTaskSet.add(st);
			}
		}
		
		return sporadicTaskSet;
	}
	
	public ArrayList<PhasedTask> toPhasedTask(ParallelizedOption option)
	{
		Util util = new Util(0);

		ArrayList<PhasedTask> phasedTaskSet = new ArrayList<PhasedTask>();
		
		for (int i = 0; i < listTasks.size(); i++)
		{
			int numOptions = 1;
			int p = listTasks.get(i).getPeriod();
			int d = listTasks.get(i).getDeadline();

			switch(option)
			{
			case SINGLE:
				numOptions = 1;
				break;
			case MAX:
				numOptions = listTasks.get(i).getNumOptions();
				break;
			case RANDOM:
				numOptions = util.randomInt(1, listTasks.get(i).getNumOptions());
				break;
			case BEST_EFFORT:
			{
				Task task = listTasks.get(i);
				for (int j = 0; j < task.getNumOptions(); j++)
				{
					// in this method, single segment task is assumed
					if (task.getMaxExecutionTimeOfSegment(0, j) <= task.getDeadline())
					{
						numOptions = j + 1;
						break;
					}
				}
				break;
			}
			case SELECTED:
			{
//				System.out.printf("task ID = %d\n", i);
				Task task = listTasks.get(i);
				numOptions = task.selectedOption(0) + 1;
				d = listTasks.get(i).getIntermediateDeadline(0);
				break;
			}
			default:
			}
			

			double e = 0;
			int maxE = 0;
			listTasks.get(i).selectOption(0, numOptions - 1);
			for (int j = 0; j < numOptions; j++)
			{
				double threadExecutionTime = listTasks.get(i).getExecutionTimeOfThread(0, numOptions - 1, j);
				if (threadExecutionTime > d) return null;
				e += threadExecutionTime;
				if ((int)threadExecutionTime > maxE)
					maxE = (int)threadExecutionTime;

			}
			PhasedTask phasedTask = new PhasedTask(p, (int)e, d); 
			phasedTask.taskID = i;
			phasedTask.phase = listTasks.get(i).getPhase();
			phasedTask.linkedTask = listTasks.get(i);
			phasedTask.setOption(numOptions);
			phasedTask.setMaxThreadExecutionTime(maxE);
			phasedTaskSet.add(phasedTask);

		}
		
		return phasedTaskSet;
	}
	
	@Deprecated
	public ArrayList<PhasedTask> toHarmonicPhasedTask(ParallelizedOption option)
	{
		ArrayList<PhasedTask> taskSet = toPhasedTask(option);
		for (int i = 0; i < taskSet.size(); i++)
		{
			/* in here, period of task is adjusted to 2^n*/
			double period = taskSet.get(i).getPeriod();
			double exponential = (int)Math.ceil(Math.log(period) / Math.log(2));
			
			taskSet.get(i).setPeriod((int)Math.pow(2, exponential));

		}
		
		return taskSet;
	}
	
	/* this function check consistency of the task*/
	public boolean isValid()
	{
		// check period and deadline is larger than minimum executionTime
		for (int i = 0; i < listTasks.size(); i++)
		{
			Task task = listTasks.get(i);
			int deadline = task.getDeadline();
			int period = task.getPeriod();
			int nOption = task.getNumOptions();
			int deadlineLimit = task.getMaxExecutionTimeOfSegment(0, nOption - 1);
			
			if (period < deadline) return false;
			if (deadline < deadlineLimit) return false;
		}
		
		return true;
	}
}
