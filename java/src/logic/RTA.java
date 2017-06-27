package logic;

import java.util.ArrayList;

import data.SporadicTask;
import multicore_exp.Param;

public class RTA {
	
	static int NumberOfJobs(SporadicTask t, double L)
	{
		double D = t.getDeadline();
		double C = t.getExecutionTime();
		double T = t.getPeriod();		
		return (int)Math.floor( (L+D-C)/T );		
	}
	static double M_BoundOnTheWorkload(SporadicTask t, double L)
	{
		double C = t.getExecutionTime();
		double D = t.getDeadline();
		double T = t.getPeriod();		
		double left = NumberOfJobs(t, L) * C;
		double right = Math.min(C, L + D - C - NumberOfJobs(t, L) * T);		
		return left + right;
	}
	static double DBF(SporadicTask t_k, SporadicTask t_i)
	{
		double D_k = t_k.getDeadline();
		double D_i = t_i.getDeadline();
		double T_i = t_i.getPeriod();
		double C_i = t_i.getExecutionTime();
		
		return ( Math.floor( (D_k - D_i)/T_i ) + 1 ) * C_i;
	}
	static double J_Interference(SporadicTask t_k, SporadicTask t_i)
	{
		double D_k = t_k.getDeadline();
		double C_i = t_i.getExecutionTime();
		double T_i = t_i.getPeriod();		
		double dbf = DBF(t_k, t_i);		
		return dbf + Math.min(C_i, Math.max(0, D_k - dbf * T_i / C_i));
	}
	static double I_Interference(SporadicTask t_k, SporadicTask t_i)
	{
		double C_k = t_k.getExecutionTime();
		return Math.min(M_BoundOnTheWorkload(t_i, t_k.R_ub), Math.min(J_Interference(t_k, t_i), t_k.R_ub - C_k + 1) );		
	}	
	public static boolean isSchedulable3(ArrayList<SporadicTask> taskSet)
	{
		for (int k=0; k<taskSet.size(); k++)
		{
			SporadicTask t = taskSet.get(k);
			t.R_ub = taskSet.get(k).getExecutionTime();
		}
			
		boolean updated = true;		
		while (updated)
		{
			updated = false;
			for (int k=0; k<taskSet.size(); k++)
			{
				SporadicTask t_k = taskSet.get(k);
				
				double C_k = t_k.getExecutionTime();
				
				double sum = 0;
				for (int i=0; i<taskSet.size(); i++)
				{
					if (i == k)
						continue;				
					sum += I_Interference(t_k, taskSet.get(i));
				}
				
				double newR = C_k + Math.floor(sum/Param.NumProcessors);
				
				if (newR > t_k.getDeadline())
					return false; 
				
				if (t_k.R_ub != newR)
				{
					t_k.R_ub = newR;
					updated = true;
				}
			}	
		}				
		return true;
	}
		
	public static boolean isSchedulable2(ArrayList<SporadicTask> taskSet)
	{
		boolean updated = true;
			
		for (int k=0; k<taskSet.size(); k++)
		{
			SporadicTask t = taskSet.get(k);
			t.R_ub = taskSet.get(k).getExecutionTime();
		}
		
		while (updated)
		{
			updated = false;
			
			for (int k=0; k < taskSet.size(); k++)
			{
				SporadicTask t_k = taskSet.get(k);
				
				double value = 0;
				for (int i=0; i<taskSet.size(); i++)
				{
					if (i == k)
						continue;
					
					SporadicTask t_i = taskSet.get(i);
					value += Math.min( Math.min(M(t_i, t_k.R_ub),  J(t_i, t_k)), t_k.R_ub - t_k.getExecutionTime() + 1 );
				}
				
				double newBound = t_k.getExecutionTime() + Math.floor( value / Param.NumProcessors );

				if (newBound > t_k.getDeadline())
					return false;
				
				if (newBound > t_k.R_ub)
				{
					t_k.R_ub = newBound;
					updated = true;
				}				
			}			
		}
		
		return true;
	}
		
	static double M(SporadicTask t_i, double L)
	{
		double D_i = t_i.getDeadline();
		double C_i = t_i.getExecutionTime();
		double S_i = t_i.getDeadline() - t_i.R_ub;
		double T_i = t_i.getPeriod();
		
		double left = Math.floor( (L + D_i - C_i - S_i) / T_i ) * C_i;
		double right = Math.min(C_i,  (L+D_i-C_i-S_i) % T_i );
		return left + right;
	}
	static double J(SporadicTask t_i, SporadicTask t_k)
	{
		double D_k = t_k.getDeadline();
		double T_i = t_i.getPeriod();
		double S_i = t_i.getDeadline() - t_i.R_ub;
		double C_i = t_i.getExecutionTime();
		
		double left = Math.floor(D_k / T_i) * C_i;
		double right = Math.min(C_i, (D_k % T_i - S_i));
		return left + right;
	}
	
	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{		
		boolean updated = true;
						
		for (int k=0; k<taskSet.size(); k++)
		{
			SporadicTask t = taskSet.get(k);
			t.R_ub = taskSet.get(k).getExecutionTime();
		}
		
		while (updated)
		{
			updated = false;
			
			for (int k=0; k < taskSet.size(); k++)
			{
				SporadicTask t_k = taskSet.get(k);
				
				double newBound = computeResponse(k, taskSet);
				if (newBound > t_k.getDeadline())
					return false;
				
				if (newBound > t_k.R_ub)
				{
					t_k.R_ub = newBound;
					updated = true;
				}				
			}			
		}
		
		return true;
	}
	
	static double computeResponse(int k, ArrayList<SporadicTask> taskSet)
	{
		SporadicTask t_k = taskSet.get(k);
		
		double rightTerm = 0;
		for (int i=0; i<taskSet.size(); i++)
		{
			if (i==k)
				continue;
			
			rightTerm += Math.min(W_hat(i, t_k.R_ub, taskSet.get(i)), t_k.R_ub - t_k.getExecutionTime() + 1); 
		}
		
		return t_k.getExecutionTime() + Math.floor( rightTerm / Param.NumProcessors );		
	}
	
	static double W_hat(int i, double R_ub_k, SporadicTask t)
	{
		double L = R_ub_k;
		
		double R = t.R_ub;		
		double C = t.getExecutionTime();
		double T = t.getPeriod();
		
		double leftTerm = Math.floor( (L + R - C) / T  ) * C;
		double rightTerm = Math.min(C, (L+R-C) % T);
		
		return leftTerm + rightTerm;				
	}
}
