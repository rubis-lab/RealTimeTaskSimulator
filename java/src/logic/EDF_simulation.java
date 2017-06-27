package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import data.Job;
import data.SporadicTask;
import multicore_exp.Param;
import multicore_exp.Util;

public class EDF_simulation {

	public static long maxLCM = 0;

	public static long getLCM(ArrayList<SporadicTask> taskSet)
	{
		long lcm = (long)taskSet.get(0).getPeriod();
		
		for(int i=1; i<taskSet.size(); i++)
		{
			long b = (long) taskSet.get(i).getPeriod();
			lcm = Util.getLCM(lcm, b);
		}
		return lcm;
	}
	
	public static int getNumSchedulableTasks(ArrayList<SporadicTask>  taskSet) {
		
		ArrayList<SporadicTask>   newSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			newSet.add(taskSet.get(i));
			if (!isSchedulable(newSet))
				return i;
			
			taskSet.get(i).schedulableGEDF = true;
		}
		
		return taskSet.size();
	}
	

	private static LinkedList<Job> getNewArrivals(Queue<Job> entireJobs, long timeline) {
		LinkedList<Job> arrivals = new LinkedList<Job>();
		
		for (Job j : entireJobs)
		{
			if (j.arrivalTime == timeline)
				arrivals.add(j);
		}
		
		Collections.sort(arrivals, new EDFCompare());		
		return arrivals;
	}

	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{
		long timeline = 0;
		long LCM = getLCM(taskSet);
		
		/*
		if (LCM > maxLCM)
		{
			maxLCM = LCM;
			System.out.println("LCM = " + LCM);			
		}
		*/
		
		// Queue
		Queue<Job> entireJobs = new LinkedList<Job>();
		
		for(SporadicTask t : taskSet)
		{
			long numJobs = LCM / (long)t.getPeriod();
			
			for (int i=0; i<numJobs; i++)
			{
				Job j = new Job();
				j.id = i;
				j.arrivalTime = i * (long)t.getPeriod();
				j.executionTime = t.getExecutionTime();
				j.remainingTime = t.getExecutionTime();
				j.absDeadline = j.arrivalTime + t.getDeadline();				
				entireJobs.add(j);
			}			
		}
		
		/*
		System.out.println("Entire jobs ----------");
		for (Job j : entireJobs)
		{
			System.out.println(j.id + "\tarr:" + j.arrivalTime + "\tdead:" + j.absDeadline);
		}
		*/
		
		LinkedList<Job> runningQueue = new LinkedList<Job>();
		LinkedList<Job> suspendedQueue = new LinkedList<Job>();
		LinkedList<Job> finishedQueue = new LinkedList<Job>();
		LinkedList<Job> missedQueue = new LinkedList<Job>();
		
		//System.out.println("time\tglob.\trun\tsus\tfin\tmiss");
		while (timeline < LCM)
		{		
			// new jobs arrival in the queue, EDF order
			LinkedList<Job> globalQueue = getNewArrivals(entireJobs, timeline);
			globalQueue.addAll(suspendedQueue);
			suspendedQueue.clear();			
					
			// now we have all jobs for this tick
			{
				Collections.sort(globalQueue, new EDFCompare());			
				Job j = null;
				while ( null != (j = globalQueue.poll()) )
				{
					runningQueue.add(j);
				}
				
				while (runningQueue.size() > Param.NumProcessors)
				{
					// we must preempt a job which has latest deadline
					Collections.sort(runningQueue, new EDFCompare());

					Job preemptedJob = runningQueue.removeLast();
					suspendedQueue.add(preemptedJob);
				}
			}
						
			// now time goes..
			// TODO: time overflow?
			timeline++;
						
			// handling runningQueue
			{
				Job j = null;
				while ( null != (j = runningQueue.poll()) )
				{
					j.remainingTime--;
					if (j.remainingTime <= 0)
					{
						j.finishTime = timeline;
						finishedQueue.add(j);
					}
					else if (j.absDeadline <= timeline)
					{
						// check if there is a deadline miss		
						missedQueue.add(j);
						return false;
						//System.out.println("Not Schedulable at " + timeline);						
					}
					else
					{
						suspendedQueue.add(j);
					}
				}
				
				for (Job susJob : suspendedQueue)
				{
					if (susJob.absDeadline <= timeline)
					{
						missedQueue.add(j);
						return false;
					}					
				}
			}
						
			//System.out.println("at " + timeline + ":\t" + globalQueue.size() + "\t" + runningQueue.size() + "\t" + suspendedQueue.size() + "\t" + finishedQueue.size() + "\t" + missedQueue.size());			
		}
		
		return missedQueue.size() == 0;
	}
}
