package logic;

import generator.TaskGeneratorSingleSegment;

import java.util.ArrayList;
import java.util.Collections;

import multicore_exp.Param;
import multicore_exp.Util;
import data.SporadicTask;
import data.TaskSet;

public class EDFEventSimulation {
	
		
	protected class ProcessorInfo implements Comparable<ProcessorInfo>
	{
		public int processorId;
		public boolean isIdle;
		public long absTaskDeadline;
		public long absTaskStartTime;
		public JobInfo job;
		
		public ProcessorInfo(int processorId)
		{
			this.processorId = processorId;
			isIdle = true;
			job = null;
			release();
		}
		
		public JobInfo release()
		{
			JobInfo prevJob = job;
			if (job != null)
			{
				long runningTime = simulationTime - absTaskStartTime;
				job.executed(runningTime);
			}
				
			isIdle = true;
			absTaskDeadline = 0;
			absTaskStartTime = 0;
			job = null;
			
			return prevJob;
		}
		
		public void allocate(JobInfo job)
		{
			release();
//			System.out.println("\t\t" + simulationTime + "\tcpu" + processorId + " <- " + job.taskId);
			isIdle = false;
			this.job = job;
			absTaskDeadline = job.absDeadline;
			absTaskStartTime = simulationTime;
			job.processor = this;
			job.event.triggerFinish(simulationTime + job.remainingTime);
		}

		@Override
		public int compareTo(ProcessorInfo o) {
			if (isIdle)
			{
				if (o.isIdle) return 0;
				else return 1;
			}
			if (o.isIdle) return -1;
			
			if (absTaskDeadline > o.absTaskDeadline) return 1;
			if (absTaskDeadline < o.absTaskDeadline) return -1;
			return 0;
		}
	}
	
	protected class JobInfo
	{
		public long absReleaseTime;
		public long absDeadline;
		public long offset;
		public int taskId;			// task id
		public int jobId;			// job id
		public int remainingTime;
		public int period;
		public int deadline;
		public int executionTime;
		public EDFEvent event;
		public ProcessorInfo processor;
		
		public JobInfo(SporadicTask task, int offset)
		{
			period = (int)task.getPeriod();
			executionTime = (int)task.getExecutionTime();
			deadline = (int)task.getDeadline();
			absReleaseTime = offset;
			absDeadline = offset + deadline;
			taskId = task.taskID;
			jobId = 0;
			remainingTime = executionTime;
			
			processor = null;
		}
		
		protected void nextJob()
		{
			absReleaseTime += period;
			absDeadline += period;
			jobId ++;
			remainingTime = executionTime;
		}
		
		public void executed(long runningTime)
		{
			remainingTime -= runningTime;
			assert(remainingTime >= 0);
			processor = null;
			
			// deadline check
			if (simulationTime > absDeadline)
				scheduleFail = true;

			if (remainingTime == 0)
			{
				event.job.nextJob();
				event.triggerRelease(absReleaseTime);
			}
			else
			{
				event.triggerWait();
			}
			
		}
		

	}
		
	protected class EDFEvent implements Comparable<EDFEvent>
	{
		private final static int FINISH = 0;
		private final static int RELEASE = 1;
		private final static int WAIT = 2;
		protected long eventTime;			// event time
		protected int type;					// event type
		protected JobInfo job;
		
		public EDFEvent(JobInfo job)
		{
			this.job = job;
			job.event = this;
			
			triggerRelease(job.offset);
		}
		
		public void triggerRelease(long time)
		{
			type = RELEASE;
			eventTime = time;
		}
		
		public void triggerFinish(long time)
		{
			type = FINISH;
			eventTime = time;
		}
		
		public void triggerWait()
		{
			type = WAIT;
			// waiting job should be finished before its deadline.
			// if this job is scheduled as WAIT type, that means schedule fails.
			eventTime = job.absDeadline;
			waitList.add(this);
		}


		@Override
		public int compareTo(EDFEvent compareEvent)
		{
			long ret;
			
			ret = eventTime - compareEvent.eventTime;
			if (ret == 0) 
			{
				// FINISH event first, then START, then WAIT
				ret = type - compareEvent.type;
			}
			
			if (ret == 0)
			{
				ret = job.absDeadline - compareEvent.job.absDeadline;
			}
			
			if (ret == 0)
			{
				ret = compareEvent.job.remainingTime - job.remainingTime;
			}
				
			if (ret > 0) return 1;
			if (ret < 0) return -1;
			return 0;
		}
	}

	protected ArrayList<EDFEvent> eventList;
	protected ArrayList<ProcessorInfo> processorList;
	protected ArrayList<EDFEvent> waitList;
	protected ArrayList<JobInfo> jobList;
	protected long simulationTime;
	protected boolean scheduleFail;
	protected long LCM;

/* init simulator */		
	public void initSimulator(ArrayList<SporadicTask> taskSet, int nProcessors)
	{
		simulationTime = 0;
		scheduleFail = false;

		processorList = new ArrayList<ProcessorInfo>();
		jobList = new ArrayList<JobInfo>();
		eventList = new ArrayList<EDFEvent>();
		waitList = new ArrayList<EDFEvent>();
		
		for (int i = 0; i < nProcessors; i++)
			processorList.add(new ProcessorInfo(i));

		for (int i = 0; i < taskSet.size(); i++)
		{
			JobInfo job = new JobInfo(taskSet.get(i), 0);
			jobList.add(job);
			eventList.add(new EDFEvent(job));
		}
	}

	
	
	protected void eventHandler(EDFEvent event)
	{
		JobInfo job = event.job;
		switch (event.type)
		{
		case EDFEvent.FINISH:
			job.processor.release();
			break;
		case EDFEvent.RELEASE:
/*
			//find proper processor(idle first, then largest deadline task)
			ProcessorInfo processor = Collections.max(processorList);
			
			if (processor.isIdle || processor.job.absDeadline > job.absDeadline)
			{
				// release the processor whether it is idle or not
				processor.release();
				// allocate this job to the processor
				processor.allocate(job);
			}
			else
			{
				event.triggerWait();
			}
*/
			event.triggerWait();
			break;
		case EDFEvent.WAIT:
			// This job waits until its deadline. schedule fails
			scheduleFail = true;
			break;
		}
	}
/* process event in event list */
		
	protected void scheduleWaitingJob()
	{
		Collections.sort(waitList);
		for (int i = 0; i < waitList.size(); i++)
		{
			ProcessorInfo processor = Collections.max(processorList);
			if (processor.isIdle || 
					processor.job.absDeadline > waitList.get(i).job.absDeadline)
			{
				
				if (DEBUG)
				{
					EDFEvent event = waitList.get(i);
					System.out.print(event.job.taskId + "(!)\t");
				}
				
				processor.allocate(waitList.get(i).job);
				waitList.remove(0);
				i--;
			}
			else
				break;
		}
	}
	protected void processEvent()
	{
		Collections.sort(eventList);
		simulationTime = eventList.get(0).eventTime;
		
		
		if (DEBUG)
		{
			System.out.print(simulationTime + "\t");
			for (int i = 0; i < eventList.size(); i++)
			{
				long eventTime = eventList.get(i).eventTime;
				if (eventTime != simulationTime)
					break;
				EDFEvent event = eventList.get(i);
				System.out.print(event.job.taskId + "(" + event.type + ")\t");
			}
		}
			
		for (int i = 0; i < eventList.size(); i++)
		{
			long eventTime = eventList.get(i).eventTime;
			assert(eventTime >= simulationTime);
			if (eventTime != simulationTime)
				break;

			// event handler only processes FINISH and RELEASE events.
			// after that, waiting job will be allocated to idle processors 
			eventHandler(eventList.get(i));
		}

		scheduleWaitingJob();

		if (DEBUG)
		{
			System.out.println();
			System.out.print("\t");
			for (int i = 0; i < processorList.size(); i++)
			{
				if (processorList.get(i).isIdle) System.out.print("i\t");
				else System.out.print(processorList.get(i).job.taskId + "(" + 
						(simulationTime + processorList.get(i).job.remainingTime) + ")\t");
			}
			System.out.println();
			for (int i = 0; i < eventList.size(); i++)
			{
				EDFEvent event = eventList.get(i);
				JobInfo job = event.job;
				System.out.print(job.taskId + " r:" + (simulationTime + job.remainingTime) +
						" d:" + job.absDeadline + "\t");
			}
			System.out.println();
			if (simulationTime == 2762) System.exit(0);
		}
	}
	
	public boolean checkSimulationEnd()
	{
		return (LCM < simulationTime);
	}
	
	public boolean run(ArrayList<SporadicTask> taskSet, int nProcessors)
	{
		LCM = Util.getTaskSetLCM(taskSet);
		initSimulator(taskSet, nProcessors);
		
		while (!checkSimulationEnd())
		{
			processEvent();
			if (scheduleFail) return false;
		}
		return true;
	}
	
	public static boolean isSchedulable(ArrayList<SporadicTask> taskSet)
	{
		EDFEventSimulation simulator = new EDFEventSimulation();
		return simulator.run(taskSet, Param.NumProcessors);
	}
	
	protected final static boolean DEBUG = false;
	public static void main(String args[])
	{
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 2;

		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomGamma(0.1, 1);
		generator.setRandomTaskNum(3,10);
		generator.SetMaxNumThreads(2);
		
		int iter = 10000;
		int succ1 = 0,  succ2= 0;
		
		generator.setFixedGamma(0.1);
		ArrayList<Integer> fail = new ArrayList<Integer>();
		long tot1 = 0, tot2 = 0;
		for (int i = 0; i < iter; i++)
		{
			TaskSet taskSet = generator.GenerateTaskSetWithLimitedLCM(i, i, 10000);
			ArrayList<SporadicTask> sporadicTaskSet = taskSet.toSporadicTask(i, TaskSet.ParallelizedOption.SINGLE);
//			if (i != 51) continue;
			if (DEBUG)
			{
				for (int j = 0; j < sporadicTaskSet.size(); j++)
				{
					System.out.println("(" + sporadicTaskSet.get(j).getPeriod() + ", " +
							sporadicTaskSet.get(j).getExecutionTime() + ", " + 
							sporadicTaskSet.get(j).getDeadline() + ")");
				}
				System.out.println("LCM : " + Util.getTaskSetLCM(sporadicTaskSet));
			}
				
			long startTime, endTime;
			startTime = System.currentTimeMillis();
			boolean ret1 = EDFEventSimulation.isSchedulable(sporadicTaskSet);
			endTime = System.currentTimeMillis();
			tot1 += endTime - startTime;
			startTime = System.currentTimeMillis();
			boolean ret2 = EDF_simulation.isSchedulable(sporadicTaskSet);
			endTime = System.currentTimeMillis();
			tot2 += endTime - startTime;

			if (ret1) succ1 ++;
			if (ret2) succ2 ++;

			System.out.println(i + " " + ret1 + " " + ret2);  
			if (ret1 != ret2)
			{
				fail.add(i);
			}
		}
		System.out.println("-------------");
		for (int i = 0; i < fail.size(); i++)
		{
			System.out.println(fail.get(i));
		}
		System.out.println("Difference : " + fail.size() + " / " + iter);
		System.out.println("Time : " + tot1 + "\t" + tot2);
		System.out.println("Result :" + (succ1 / (double)iter) + "\t" + (succ2 / (double)iter));
	}

}
