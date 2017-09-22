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
	
    /*

     */
    public double[] unifast(double tot, int pcs) {
        if(tot > 1.0) {
            throw new RuntimeException("unifast: Use unifastProportional or randfixedsum for total sum over 1.0.");
        }
        if(tot < 0.0) {
            throw new RuntimeException("unifast: tot less than 0.0.");
        }
        if(pcs <= 0) {
            throw new RuntimeException("unifast: pcs should be positive number.");
        }

        double[] retList = new double[pcs];
        //Random rnd = new Random();
        double sum = tot;
        for(int i = 0; i < pcs - 1; i++) {
            double tmp = sum * Math.pow(random.nextDouble(), (1.0 / (pcs - i)));
            retList[i] = sum - tmp;
            sum = tmp;
        }
        retList[pcs - 1] = sum;

        return retList;
    }
    /*
        Divide tot into pcs, each pcs may be over 1.0.
     */

    public double[] unifastProportional(double tot, int pcs) {
        if(tot < 0.0) {
            throw new RuntimeException("unifastProportional: tot less than 0.0.");
        }
        if(pcs <= 0) {
            throw new RuntimeException("unifastProportional: pcs should be positive number.");
        }

        double[] retList = unifast(1.0, pcs);

        for(int i = 0; i < pcs; i++) {
            retList[i] *= tot; 
        }
        return retList;
    }
}
