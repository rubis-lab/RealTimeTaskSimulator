package data;

public class PhasedTask extends SporadicTask {
	
	protected int phase;
	protected double taskDensity;
	protected int option;
	public Task linkedTask = null;
	protected int maxThreadExecutionTime;


	public PhasedTask(int p, int e, int d)
	{
		super(p, e, d);
		taskDensity = e / (double) d;
		phase = 0;
	}
	
	public void setPhase(int phase)
	{
		this.phase = phase;
	}
	
	public int getPhase()
	{
		return phase;
	}
	
	public double getTaskDensity()
	{
		return taskDensity;
	}
	
	public double getInstanceDensity(long t)
	{
		if ((t - phase + period) % period < deadline) return taskDensity;
		return 0;
	}
	public double getInstanceDensityFallingEdge(long t) {
		long ret = (long)(t - phase + period) % (long)period;
		if (ret == 0) return 0;
		if (ret < deadline) return taskDensity;
		return 0;
	}
	public void setPeriod(int period)
	{
		this.period = period;
	}
	
	public void setOption(int option)
	{
		this.option = option;
	}
	public int getOption()
	{
		return this.option;
	}

	public int getMaxThreadExecutionTime() {
		return maxThreadExecutionTime;
	}
	
	public void setMaxThreadExecutionTime(int threadExecutionTime)
	{
		maxThreadExecutionTime = threadExecutionTime;
	}
}
