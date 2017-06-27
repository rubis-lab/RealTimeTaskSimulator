package data;

import multicore_exp.Param;


public class SegmentList {
	
	private Segment[] segments;
	private int selectedOptionIndex = -1;
	
	
	// segments with all number of options 
	public SegmentList(int maxOptions)
	{
		this.maxOptions = maxOptions;

		segments = new Segment[maxOptions];
		for (int i = 0; i < maxOptions; i++)
			segments[i] = new Segment(i + 1);		
	}
	
	public void setExecutionTime(int optionIndex, int threadIndex, int executionTime)
	{
		segments[optionIndex].setExecutionTime(threadIndex, executionTime);
	}
	
	public void setDeadline(int deadline)
	{
		for (int i = 0; i < segments.length; i++)
		{
			segments[i].setDeadline(deadline);
		}
	}
	
	public int getDeadline()
	{
		return segments[0].getDeadline();
	}
	
	
	public Segment get(int optionIndex)
	{
		return segments[optionIndex];
	}
	
	public void select(int optionIndex)
	{
		selectedOptionIndex = optionIndex;
	}
	
	public int selectedOption()
	{
		if (selectedOptionIndex < 0)
			throw new RuntimeException("Segment option is not selected");
		return selectedOptionIndex;		
	}
	
	public Segment selected()
	{
		if (selectedOptionIndex < 0)
			throw new RuntimeException("Segment option is not selected");		
		return get(selectedOptionIndex);
	}
	
	public String toString()
	{
		String buffer;
		String nl = System.lineSeparator();
		
		buffer = "[Segment]" + nl;
		buffer += "#Number of options : " + segments.length + nl + nl;
		for (int i = 0; i < segments.length; i++)
		{
			buffer += segments[i].toString() + nl;
		}
		
		return buffer;	
	}
	
	public int getMaxOptions()
	{
		return maxOptions;
	}
	
	private int maxOptions;

}