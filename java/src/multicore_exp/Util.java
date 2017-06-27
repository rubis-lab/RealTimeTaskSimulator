package multicore_exp;

import data.SporadicTask;

import java.util.ArrayList;
import java.util.Random;

import data.TaskSet;

public class Util {

	public Random random;
	
	public Util(int seed)
	{
		this.random = new Random(seed);
	}
	
	public long randomLong(long min, long max)
	{
		return random.nextLong();
	}
	public int randomInt(int min, int max)
	{
		return random.nextInt(max - min + 1) + min; 
	}
	
	public double randomDouble(double min, double max)
	{
		return random.nextDouble() * (max - min) + min;
	}
	
	public double randomDoubleNormalDistribution(double mean, double standardDeviation)
	{
		return random.nextGaussian() * standardDeviation + mean;
	}
	
	public static long getGCD(long a, long b)
	{
		while (b > 0)
		{
			long temp = b;
			b = a % b;
			a = temp;
		}		
		return a;
	}
	public static long getGCD(long[] input)
	{
		long result = input[0];
		for(int i=0; i<input.length; i++)
			result = getGCD(result, input[i]);
		return result;
	}
	public static long getLCM(long a, long b)
	{
		long c = b / getGCD(a, b);
		if (a != 0 && c > Long.MAX_VALUE / a)
		{
			throw new RuntimeException("LCM overflow");
	//		return Long.MAX_VALUE;
		}
		
		
		return a * c;
	}
	public static long getLCM(long[] input)
	{
		long result = input[0];
		for(int i=1; i<input.length; i++)
			result = getLCM(result, input[i]);
		return result;
	}
	public static long getTaskSetLCM(TaskSet taskSet)
	{
		long result = 1;
		for (int i = 0; i < taskSet.size(); i++)
		{
			result = getLCM(result, taskSet.get(i).getPeriod());
		}
		
		return result;
	}
	public static long getTaskSetLCM(ArrayList<? extends SporadicTask> taskSet)
	{
		long result = 1;
		for (int i = 0; i < taskSet.size(); i++)
		{
			result = getLCM(result, (long)taskSet.get(i).getPeriod());
		}
		return result;
	}
}
