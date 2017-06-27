package data;

import java.util.ArrayList;
import java.util.Vector;

import multicore_exp.Param;

public class Task implements Cloneable{
	
	private int nSegments;
	private int period;
	private int realPeriod;
	private int realDeadline; 
	private int deadline;
	private int taskID = -1;
	private int nOptions;
	
	private int phase;
	
	// additional information
	private int seed;
	private int scmin;
	private int scmax;
	private int omax;
	private int kmin;
	private int kmax;
	
	public SegmentList[] segments;
	
	public ArrayList<OptionDeadline> optionDeadline;
	
	
	public Task(int nSegments, int period, int deadline)
	{
		this.nSegments = nSegments;
		this.nOptions = Param.NumThreads_MAX;
		this.period = period;
		this.deadline = deadline;	
		
		segments = new SegmentList[nSegments];
		
		for (int i = 0; i < nSegments; i++)
		{
			segments[i] = new SegmentList(nOptions);
		}			
	}

	public Task(int nSegments)
	{
		this.nSegments = nSegments;
		this.nOptions = Param.NumThreads_MAX;
	
		segments = new SegmentList[nSegments];
		
		for (int i = 0; i < nSegments; i++)
		{
			segments[i] = new SegmentList(nOptions);
		}			
	}
	
	public Task copyTask()
	{
		Task newtask = new Task(this.nSegments, this.period, this.deadline);
		newtask.setTaskID(-1);
		newtask.setNumOptions(this.nOptions);
		newtask.setPhase(this.phase);
		newtask.setInformation(this.seed, this.scmin, this.scmax, this.omax, this.kmin, this.kmax);
		newtask.setSegmentList(this.segments);
		
		return newtask;
	}
	
	public void selectOption(int segmentIndex, int optionIndex)
	{
		segments[segmentIndex].select(optionIndex);
	}	
	
	/**
	 * @return the total sum of all the execution time of threads in the task (C of task)
	 */
	public int getTotalExecutionTime()
	{
		int totalExecutionTime = 0;
		
		for (int i = 0; i < nSegments; i++)
		{
			totalExecutionTime += segments[i].selected().getTotalExecutionTime();
		}
		return totalExecutionTime;
	}
	
	/**
	 * @param segmentIndex
	 * @return the sum of all the execution time of the threads in the segment (C of segment)
	 */
	public int getTotalExecutionTime(int segmentIndex)
	{
		return segments[segmentIndex].selected().getTotalExecutionTime();
	}
	
	/**
	 * @param segmentIndex 
	 * @param optionIndex
	 * @return the sum of all the execution time of the threads in the segment with given option. C^{total}_ik(O_ik)   
	 */
	public int getTotalExecutionTime(int segmentIndex, int optionIndex)
	{
		return segments[segmentIndex].get(optionIndex).getTotalExecutionTime();
	}
	
	// before calling getPeakDensity and getDensity, 
	// task should select segment options through selectOption function	
	public double getDensity(int segmentIndex)
	{
		return segments[segmentIndex].selected().getDensity();
	}
	
	public double getPeakDensity()
	{		
		double maxDensity = 0;
		
		for (int i = 0; i < nSegments; i++)
		{
			if (getDensity(i) > maxDensity)
				maxDensity = getDensity(i);				
		}
		return maxDensity;
	}
	

	/**
	 * @return the maximum execution time of the task. The sum of all the max execution time of segments. (LC of task)
	 */
	public int getMaxExecutionTime()
	{
		int sumOfMaxExecutionTime = 0;
		
		for (int i = 0; i < nSegments; i++)
		{
			sumOfMaxExecutionTime += segments[i].selected().getMaxExecutionTime();
		}
		
		return sumOfMaxExecutionTime;
	}
	
	/**
	 * @param segmentIndex
	 * @return the maximum length of execution time of the segment (LC of segment)
	 */
	public int getMaxExecutionTimeOfSegment(int segmentIndex)
	{
		return segments[segmentIndex].selected().getMaxExecutionTime();
	}

	
	/**
	 * @param segmentIndex
	 * @param optionIndex
	 * @return the maximum execution time of the thread in the given segment with given option. C^{max}_ik(O_ik)
	 */
	public int getMaxExecutionTimeOfSegment(int segmentIndex, int optionIndex)
	{
		return segments[segmentIndex].get(optionIndex).getMaxExecutionTime();
	}
	
	public void setExecutionTime(int segmentIndex, int optionIndex, int threadIndex, int executionTime)
	{
		try
		{
			segments[segmentIndex].setExecutionTime(optionIndex, threadIndex, executionTime);			
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	
	public void setIntermediateDeadline(int segmentIndex, int deadline)
	{
		segments[segmentIndex].setDeadline(deadline);
	}
	
	public int getIntermediateDeadline(int segmentIndex)
	{
		return segments[segmentIndex].getDeadline();
	}
	
	/**
	 * @param segmentIndex
	 * @param optionIndex
	 * @param threadIndex
	 * @return the execution time of the thread
	 */
	public int getExecutionTimeOfThread(int segmentIndex, int optionIndex, int threadIndex)
	{
		return segments[segmentIndex].get(optionIndex).getExecutionTime(threadIndex);
	}
	
	/**
	 * @param segmentIndex
	 * @param threadIndex
	 * @return the execution time of the thread of which the selected segment
	 */
	public int getExecutionTimeOfThread(int segmentIndex, int threadIndex)
	{
		return segments[segmentIndex].selected().getExecutionTime(threadIndex);
	}
	
	/**
	 * @param segmentIndex
	 * @return the number of threads of given segment index
	 */
	public int getNumThreadsOfSegment(int segmentIndex)
	{
		return segments[segmentIndex].selected().getThreadSize();
	}
	
	/**
	 * @return the maximum number of threads among the segments in the task
	 */
	public int getMaxNumOfThreads()
	{
		int maxNumThreads = 1;
		for(int i=0; i<getNumSegments(); i++)
		{
			maxNumThreads = Math.max(maxNumThreads, getNumThreadsOfSegment(i));
		}
		return maxNumThreads;
	}
	
	
	
	public void setSegmentList(SegmentList[] segments) 
	{
		for(int i =0; i<segments.length ; i++)
			this.segments[i] = segments[i];
	}
	public void setTaskID(int taskID) 			{this.taskID = taskID;}
	public void setDeadline(int deadline)	{this.deadline = deadline;}
	public void setPeriod(int period)		{this.period = period;}	
	public void setNumOptions(int nOptions)		{this.nOptions = nOptions;}
	public int getNumSegments() 				{return nSegments;}	
	public int getPeriod()					{return period;}	
	public int getDeadline()					{return deadline;}
	public int getTaskID()						{return taskID;}
	public int getNumOptions()					{return nOptions;}
	public int getNumOptions(int segmentIndex)					{return segments[segmentIndex].getMaxOptions();}
	public void setPhase(int phase) {this.phase = phase;} 
	public int getPhase() 					{return this.phase;}
	
	public void setInformation(int seed, int scmin, int scmax, int omax, int kmin, int kmax)
	{
		this.seed = seed;
		this.scmin = scmin;
		this.scmax = scmax;
		this.omax = omax;
		this.kmin = kmin;
		this.kmax = kmax;		
	}
	
	public String toString()
	{
		String buffer;
		String nl = System.lineSeparator();
		String nlnl = nl + nl;
		
		buffer = "[Task]" + nl;
		buffer += "#Task ID" + nl  + taskID + nlnl;		
		buffer += "#Number of segments" + nl + nSegments + nlnl;
		buffer += "#Task period" + nl + period + nlnl;
		buffer += "#Task deadline" + nl + deadline + nlnl;
		buffer += "#seed: " + seed + ", sc: " + scmin + "~" + scmax + ", omax: " + omax + ", k: " + kmin + "~" + kmax + nlnl;
		
		for (int i = 0; i < nSegments; i++)
			buffer += segments[i].toString() + nlnl;				
		
		return buffer;	
	}
	
	
	
	
	
	public double getDesityAtTAndOffset(int optionDeadlineIndex, int InterDeadlineIndex, int offset, int t)
	{		
		int[] option = optionDeadline.get(optionDeadlineIndex).optionComb;
		//double[] interDeadlines = optionDeadline.get(optionDeadlineIndex).intrDeadlineComb.get(InterDeadlineIndex);
		int[] interDeadlines = optionDeadline.get(optionDeadlineIndex).intrDeadlineComb.get(InterDeadlineIndex);
		int segmentNum = getNumSegments();
		double[] density = new double[segmentNum];
		double result = 0.0;
		
		for(int i = 0; i < segmentNum;i++)
		{
			double total = getTotalExecutionTimeOfSegment(i, option[i]);
			density[i] = total / interDeadlines[i];	
		}
		
		if(offset == t)
		{
			result = density[0];
		}
		else if(offset% getPeriod() > t% getPeriod())
		{
			//result = getDesityAt(density, (getPeriod() - (offset - t) ), interDeadlines);
			result = getDesityAt(density, (getPeriod() - (offset % getPeriod() - t % getPeriod()) ), interDeadlines);
		}
		else
		{
			result =getDesityAt(density, (t% getPeriod()- offset% getPeriod()), interDeadlines);
		}
		
		/*int deadlineSum = offset;
		for(int i=0; i < segmentNum ; i++)
		{
			deadlineSum += interDeadlines[i];
			if( t % this.period < deadlineSum)
			{
				result = density[i];
				break;
			}		
		}*/
		
		
		
		return result;
	}
	
	
	// density of the task at a specific time t, with selected intermediate deadlines + selected options
		// t is relative to task
		public double getDesityAt(double[] density, double t, int[] interDeadlines)
		{
			double[] densityK = new double[getNumSegments()]; // 각 segment들의...
			double result;
			int currentT = 0;
			result = densityK[0];
			
			for(int i=0; i < getNumSegments(); i++)
			{
				currentT += interDeadlines[i];
						
				if(currentT > t % getPeriod())
				{
					result = density[i];
					break;
				}
			}
			
			return result;
		}
		
		public double getTotalExecutionTimeOfSegment(int segmentIndex, int optionIndex)
		{
			return segments[segmentIndex].get(optionIndex).getTotalExecutionTime();
		}

		
		public double getDesityWithOffsetAtT(int[] selectedOptions, double[] selectedIntermediateDeadlines, double offset, double t)
		{
			double[] densityK = null; // 각 segment들의...
			double densityAtT;
			double currentT = 0.0;
			double repeatedT = t % this.period;
			
			for(int i=0; i< this.nSegments ; i++)
				densityK[i] = getTotalExecutionTimeOfSegment(i, selectedOptions[i]) / selectedIntermediateDeadlines[i];
			
			densityAtT = 0; // initial value of density
			
			
			if(currentT == repeatedT) 
				densityAtT = 0;
			
			currentT += offset;
				
			if(currentT > repeatedT)
				densityAtT = 0;
			
			for(int i=0; i < this.nSegments  ; i++)
			{
				currentT += selectedIntermediateDeadlines[i];
				densityAtT = densityK[i];
				if(currentT > repeatedT)
					break;
			}
			
			if(currentT < repeatedT)
				densityAtT = 0;
			
			return densityAtT;
		}
		
		
		
		public double getDensityAtT(double t, double phase)
		{
			double density = (double)getTotalExecutionTime(0) / (double)getIntermediateDeadline(0);
			double ret  = -1.0;
			
 			if(  (t+phase)%this.period >= (double)getIntermediateDeadline(0) )
 				ret = 0.0;
 			else
 				ret = density;
 				
 			return ret;
			
//			if(getSegmentIndexAtT(t, phase) == -1)
//			{
//				return 0.0;
//			}
//			else
//			{
//				double totalExecutionTimeAtT = (double)getTotalExecutionTime(getSegmentIndexAtT(t, phase));
//				double intermediateDeadlineAtT = (double)getIntermediateDeadline(getSegmentIndexAtT(t, phase));
//				
//				return totalExecutionTimeAtT / intermediateDeadlineAtT;
//			}
			
		}

		public int getSegmentIndexAtT(double t, double phase) 
		{			
			int ret = -1;
						
			if(phase < t)
				t= t-phase;
			else
				t = getPeriod() - (phase - t);

			if(t > getPeriod())
				t = t % getPeriod();
			
			double sum = 0;
			for(int i = 0; i<getNumSegments() ; i++)
			{
				sum += getIntermediateDeadline(i);
				if(t < sum)
				{
					ret = i;
					break;
				}
			}
			
			return ret;
		}

	
		public int selectedOption(int segmentIndex)
		{
			return segments[segmentIndex].selectedOption();
		}
		

		public Object clone() throws CloneNotSupportedException 
		{
			return super.clone();
		}

		public void setRealPeriod(int period)
		{
			if (realPeriod == 0)
				realPeriod = period;
		}
		public int getRealPeriod() {
			return realPeriod;
		}

		public void setRealDeadline(int deadline)
		{
			if (realDeadline == 0)
				realDeadline = deadline;			
		}
		public int getRealDeadline()
		{
			return realDeadline;
		}

}

