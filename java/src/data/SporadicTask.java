package data;

public class SporadicTask {
	
	public int taskSetID;
	public int taskID;
	
	double period;
	double executionTime;
	double deadline;
	
	public double R_ub;
	
	public boolean schedulableFluid = false;
	public boolean schedulableGEDF = false;
	public boolean schedulableBAK = false;
	public boolean schedulableBCL = false;
	public boolean schedulableGFB = false;
	public boolean schedulableBCL2 = false;
	public boolean schedulableRTA = false;


	public SporadicTask(double p, double e, double d)
	{
		period = p;
		executionTime = e;
		deadline = d;
	}
	
	public double getPeriod()					{return period;}	
	public double getDeadline()					{return deadline;}
	public double getExecutionTime()			{return executionTime;}
	public double getExecutionOverPeriod()		{return executionTime/period;}
	public double getExecutionOverDeadline()	{return executionTime/deadline;}
	
	public String toString()
	{
		return taskSetID + "\t" + taskID + "\t" + period + "\t" + executionTime + "\t" + deadline 
				+ "\t" + schedulableFluid 
				+ "\t" + schedulableGEDF 
				+ "\t" + schedulableGFB 
				+ "\t" + schedulableBAK 
				+ "\t" + schedulableBCL
				+ "\t" + schedulableBCL2
				+ "\t" + schedulableRTA;
	}
}
