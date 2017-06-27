package phaseshifting;

public class ScheduleBlock {

	private int phase;
	private int deadline;	
	private int period;
	private double density;
	
	public ScheduleBlock(int deadline, int period, double density)
	{
		if (density > 1000) density = 1000;
		this.phase = 0;
		this.deadline = deadline;
		this.period = period;
		this.density = density;
	}
	
	public int setPhase(int phase)
	{		
		this.phase = phase % period;
		return this.phase;
	}
	
	public int getPeriod()
	{
		return period;
	}
	
	public int getPhase()
	{
		return phase;	
	}
	
	public double getDensity()
	{
		return density;	
	}

	public int getDeadline() {
		return deadline;
	}
	
	public double getDensity(int phase, int time)
	{		
		if ((time + (period - phase)) % period < deadline)
			return density;
		return 0;
	}
	
	public double getDensity(int time)
	{
		return getDensity(phase, time);
	}
	
	public String toString()
	{
		return deadline + " " + period + " " + density;
	}
	
	
}
