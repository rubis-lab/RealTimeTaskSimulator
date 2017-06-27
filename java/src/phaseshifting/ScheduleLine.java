package phaseshifting;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class ScheduleLine {
	
	protected int windowSize = 1;
	protected ArrayList<ScheduleBlock> blocks;
//	protected SortedSet<Integer> releasePoints, deadlinePoints;
	protected double densityMap[];
	protected double peakDensity = 0;
	
	
	public ScheduleLine(int windowSize)
	{
		blocks = new ArrayList<ScheduleBlock>();
//		releasePoints = new TreeSet<Integer>();
//		deadlinePoints = new TreeSet<Integer>();
		
		this.windowSize = windowSize;
//		System.out.println("---------------------------------------------------Window Size = " + windowSize);
		densityMap = new double[windowSize];		
	}
	
	public double getPeakDensity()
	{
		return peakDensity;
	}
	
	public void addBlock(ScheduleBlock block)
	{			
//		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + windowSize);
		int phase = findOptimalPhase(block);
		
				
		addBlock(phase, block);
	}
		
	protected void addBlock(int phase, ScheduleBlock block)
	{		
		phase = block.setPhase(phase);
		
		int multiplier = 0;
		int period = block.getPeriod();
		int deadline = block.getDeadline();
		
		while(period * multiplier + phase < windowSize)
		{
			int absoluteReleaseTime = period * multiplier + phase;	
			int absoluteDeadline = absoluteReleaseTime + deadline;

//			releasePoints.add(absoluteReleaseTime);
//			deadlinePoints.add(absoluteDeadline);
			
			for (int i = 0; i < deadline; i++)
			{
				if (i + absoluteReleaseTime >= windowSize)
					break;
				densityMap[i + absoluteReleaseTime] += block.getDensity();
				double densitySum = getDensity(i + absoluteReleaseTime);
				if (densitySum > peakDensity)
					peakDensity = densitySum;
			}			
			multiplier ++;
		}
		blocks.add(block);
	}

	protected int findOptimalPhase(ScheduleBlock block)
	{		
		int minPeakDensityPhase = 0;
		double minPeakDensity = -1;
		int period = block.getPeriod();
//		SortedSet<Integer> phaseSet = new TreeSet<Integer>();
		 
		
		for (int phase = 0; phase < period; phase ++)
		{
//			System.out.println("phase = " + phase + " / " + windowSize );
			double tempPeakDensity = examinePeakDensity(phase, block);
			if (minPeakDensity < 0 || tempPeakDensity < minPeakDensity)
			{
				minPeakDensity = tempPeakDensity;
				minPeakDensityPhase = phase;
			}			
		}
/*
		for (Integer elem : deadlinePoints)
		{
			int deadline = elem.intValue();
			// We will check given block's peak density when it starts from other blocks' deadline points,
			// but only phases ranged in 0~period are needed.
			// Using additional Set class to remove duplicated phase values. 
			phaseSet.add(deadline % period);			
		}

		for (Integer elem : phaseSet)
		{
			int phase = elem.intValue();
			double tempPeakDensity = examinePeakDensity(phase, block);
			if (tempPeakDensity < minPeakDensity)
			{
				minPeakDensity = tempPeakDensity;
				minPeakDensityPhase = phase;
			}
		}
*/		
		
		return minPeakDensityPhase;
	}
	
	protected double examinePeakDensity(int phase, ScheduleBlock block)
	{
		int period = block.getPeriod();
		int deadline = block.getDeadline();
		double density = block.getDensity();
		phase %= period;						// for the case when phase is larger than period

		int multiplier = 0;
		int absoluteReleaseTime;
		double tempPeakDensity = peakDensity;	// initialize with current peak density
		do
		{
			absoluteReleaseTime = phase + period * multiplier;
			if (absoluteReleaseTime >= windowSize) break;
			
			for (int i = 0; i < deadline; i++)
			{
				int currentTime = absoluteReleaseTime + i;
				if (currentTime >= windowSize) break;
				double currentDensity = getDensity(currentTime);				
				
				if (currentDensity + density > tempPeakDensity)
					tempPeakDensity = currentDensity + density;
			}
			multiplier++;
			
		} while(true);
		
		return tempPeakDensity;
	}
	
	protected double getDensity(int time)
	{
		return densityMap[time];
/*		
		double density = 0;
		for (int i = 0; i < blocks.size(); i++)
		{
			density += blocks.get(i).getDensity(time);
		}
		return density;
*/		
	}

}
