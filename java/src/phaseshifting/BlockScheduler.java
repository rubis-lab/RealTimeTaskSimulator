package phaseshifting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BlockScheduler {
	
	public enum Algorithm {LDF	/*Largest Density First*/};
	
	public static double getPeakDensity(ArrayList<ScheduleBlock> blocks, Algorithm algorithm)
	{
		switch(algorithm)
		{
		case LDF	:	return getPeakDensityLDF(blocks);
		
		default :
			throw new RuntimeException("block packing algorithm is not defined yet");
		}				
	}
	
	protected static double getPeakDensityLDF(ArrayList<ScheduleBlock> blocks)
	{
		int windowSize = getWindowSize(blocks);		
//		System.out.println("window size = " + windowSize);
		ScheduleLine scheduleLine = new ScheduleLine(windowSize);
		
					
		Collections.sort(blocks, new Comparator<ScheduleBlock>() {
	        @Override
	        public int compare(ScheduleBlock  block1, ScheduleBlock block2)
	        {	        	
	        	return Double.compare(block2.getDensity(), block1.getDensity());
	        }
	    });

		
		
		for (int i = 0; i < blocks.size(); i++)
		{
//			System.out.print(blocks.get(i).getDensity() + " ");
			scheduleLine.addBlock(blocks.get(i));			
		}
//		System.out.println();
		
//		System.out.println(scheduleLine.getPeakDensity());
		return scheduleLine.getPeakDensity();
		
	}
	
	protected static int getWindowSize(ArrayList<ScheduleBlock> blocks)
	{
		int windowSize = 1;
		int maxPeriod = 0;
		for (int i = 0; i < blocks.size(); i++)
		{			
			ScheduleBlock block = blocks.get(i);
			windowSize = lcm(windowSize, block.getPeriod());
			if (block.getPeriod() > maxPeriod)
				maxPeriod = block.getPeriod();
		}	
		windowSize += maxPeriod;		// worst case window size : lcm of all period + largest period(by phase shifting)
		
		assert(windowSize >= 0);
		return windowSize;		
	}

	
	protected static int gcd(int a, int b)
	{
		while (b > 0)
		{
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}
	
	protected static int lcm(int a, int b)
	{
		return a * (b / gcd(a, b));
	}	
}
