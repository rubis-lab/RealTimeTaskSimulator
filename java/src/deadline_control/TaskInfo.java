package deadline_control;

import data.Task;

public class TaskInfo {
	protected int executionTime;
	protected int deadline;
	protected int period;
	protected int realPeriod;
	protected double pOverhead;
	private int controlledDeadline;
	private int phase;
	protected int taskID;
	protected double dMin, dMax;
	protected double densityMin, densityMax;
	protected int oMax;
	private int option;
	protected int defaultOption;
//	protected ArrayList<TaskInfo> nextTasks;
	private TaskInfo prevTask;
	boolean calculatePhase;
	double defaultDensity;
	private int instancePhase;
	public Task task;

	/*
	public TaskInfo(TaskInfo info)
	{
		executionTime = info.executionTime;
		deadline = info.deadline;
		period = info.period;
		realPeriod = info.realPeriod;
		pOverhead = info.pOverhead;
		controlledDeadline = info.controlledDeadline;
		phase = info.phase;
		taskID = info.taskID;
		dMin = info.dMin;
		dMax = info.dMax;
		densityMin = info.densityMin;
		densityMax = info.densityMax;
		oMax = info.oMax;
		option = info.option;
		defaultOption = info.defaultOption;
		prevTask = info.prevTask;
		calculatePhase = info.calculatePhase;
		defaultDensity = info.defaultDensity;
		this.task = info.task;

	}
	*/
	public TaskInfo(Task task)
	{
		executionTime = task.getExecutionTimeOfThread(0, 0, 0);
		deadline = task.getRealDeadline();
		period = task.getPeriod();
		realPeriod = task.getRealPeriod();
		controlledDeadline = deadline;
		phase = 0;
		pOverhead = calculateOverheadFactor(task);
		taskID = task.getTaskID();
		oMax = task.getNumOptions();
		dMin = executionTime * (pOverhead + (1 - pOverhead) / (double)oMax);
		dMax = Math.min(deadline, period);
		densityMax = oMax;
		densityMin = executionTime / dMax;
		option = 0;
		this.task = task;
		calculateDefault(Math.min(deadline, realPeriod));
		prevTask = null;
		calculatePhase = false;
	}
	
	public void useRealExecutionModel()
	{
		dMin = task.getExecutionTimeOfThread(0, oMax - 1, 0);
	}
		
	
	
	
	public void useDefault()
	{
		period = realPeriod;
		dMax = Math.min(deadline, period);
		calculateDefault(dMax);
		controlledDeadline = (int)dMax;
		densityMin = defaultDensity;
		phase = 0;
		option = defaultOption;
	}
	
	public void setPreviousTask(TaskInfo prevTask)
	{
		this.prevTask = prevTask;
	}
	
	public void calculatePhase()
	{
		if (calculatePhase) return;
		if (prevTask == null)
			phase = 0;
		else
		{
			prevTask.calculatePhase();
			phase = prevTask.getPhase() + prevTask.getControlledDeadline();
		}
		calculatePhase = true;
	}
	
	public void setControlledDeadline(int controlledDeadline)
	{
		if (controlledDeadline < this.controlledDeadline)
			this.controlledDeadline = controlledDeadline;
	}
	public int getControlledDeadline()
	{
		return controlledDeadline;
	}
	public void setPhase(int phase)
	{
		if (this.phase != 0 && phase != this.phase)
			System.out.printf("Different Phases are assigned\n");
		this.phase = phase;
	}
	public int getPhase()
	{
		return phase;
	}
	public int getAbsolutePhase()
	{
		return instancePhase + phase;
	}
	public void setOption(int option)
	{
		assert(option > 0);
		if (option > oMax) 
			option = oMax;
		if (this.option < option)
			this.option = option;
	}
	public int getOption()
	{
		return option;
	}
	public int getOptionIndex()
	{
		return option - 1;
	}

	protected void calculateDefault(double dMax)
	{
		if (dMax == 0) return;
		defaultOption = 0;
		for (int i = 0; i < task.getNumOptions(); i++)
		{
			if (task.getMaxExecutionTimeOfSegment(0, i) <= dMax)
			{
				defaultOption = i + 1;
				break;
			}
		}
		//todo:
		if (defaultOption == 0)
			defaultOption = 1;
		
		defaultDensity = task.getTotalExecutionTimeOfSegment(0, defaultOption - 1) / dMax;
	}
	protected double calculateOverheadFactor(Task task)
	{
		double maxOverhead = 0;
		for (int i = 1; i < task.getNumOptions(); i++)
		{
			int executionTimeOfThread = task.getExecutionTimeOfThread(0, i, 0);
			double overhead = (executionTimeOfThread * (i + 1) / (double)this.executionTime - 1) / (double)i;
			if (maxOverhead < overhead) maxOverhead = overhead;
		}
		return maxOverhead;
	}
	public void setInstancePhase(int instancePhase) {
		this.instancePhase = instancePhase;
	}
	
	public int getPeriod3()
	{
		return realPeriod;
	}
	public int getHarmonizedPeriod()
	{
		return period;
	}
	public int getExecutionTime()
	{
		return executionTime;
	}
	public void setHarmonizedPeriod(int period)
	{
		this.period = period;
		dMax = Math.min(deadline, period);
		densityMin = executionTime / dMax;
		calculateDefault(dMax);
	}
}
