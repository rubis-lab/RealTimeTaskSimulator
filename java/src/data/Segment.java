package data;

public class Segment {

	private int[] executionTimes;
	private int totalExecutionTime;
	public int maxExecutionTime;
	private int nThreads;
	private int deadline;
	private boolean hasDeadline;
	
	public Segment(int nThreads)
	{
		this.nThreads = nThreads;
		executionTimes = new int[nThreads];	
		hasDeadline = false;
	}
	
	public void setExecutionTime(int threadIndex, int executionTime)
	{
		if (threadIndex >= nThreads)
			throw new RuntimeException("out of index");
		executionTimes[threadIndex] = executionTime;
		calculateExecutionTimeInfo();
	}
	
	// automatically set execution time of each thread
	public void setExecutionTimeEven(int totalExecutionTime)
	{
		for (int i = 0; i < nThreads; i++)
		{
			executionTimes[i] = totalExecutionTime / nThreads;			
		}
		calculateExecutionTimeInfo();
	}
	
		// set deadline
	public void setDeadline(int deadline)
	{
		this.deadline = deadline;
		this.hasDeadline = true;
	}
	
	public int getThreadSize()	
	{
		return nThreads;
	}
	
	public int getExecutionTime(int threadIndex)
	{
		if (threadIndex >= nThreads)
			throw new RuntimeException("out of index");
		return executionTimes[threadIndex];
	}
	
	public int getTotalExecutionTime()
	{
		return totalExecutionTime;
	}

	public int getMaxExecutionTime()
	{
		return maxExecutionTime;
	}
	public boolean hasDeadline()
	{
		return hasDeadline;
	}
	
	public int getDeadline()
	{
		if (!hasDeadline)
			throw new RuntimeException("deadline is not set");
		return deadline;
	}
	
	public double getDensity()
	{
		if (!hasDeadline)
			throw new RuntimeException("deadline is not set");
		if (deadline == 0)
			throw new RuntimeException("deadline is zero");
		
		return totalExecutionTime / (double)deadline;	
	}	
	
	private void calculateExecutionTimeInfo()
	{
		totalExecutionTime = 0;
		maxExecutionTime = 0;
		for (int i = 0; i < nThreads; i++)
		{
			totalExecutionTime += executionTimes[i];
			if (executionTimes[i] > maxExecutionTime) 
				maxExecutionTime = executionTimes[i];			
		}
	
	}
	
	
	public String toString()
	{
		String buffer ="";
		
		for (int i = 0; i < nThreads; i++)
		{
			buffer += executionTimes[i] + "\t";
		}
		
		return buffer;	
	}
	
}
