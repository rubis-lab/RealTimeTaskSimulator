package multicore_exp;

import generator.TaskGenerator;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.GroupLayout.ParallelGroup;

import data.SporadicTask;
import data.Task;
import data.TaskReader;
import data.TaskSet;

public class TaskSetPool {
		
	protected ArrayList<TaskSet> listTaskSet = new ArrayList<TaskSet>();
	
	public TaskSet taskSet;
	
	public TaskSetPool()
	{
		this.taskSet = new TaskSet();
	}
	
	public TaskSet get(int index)
	{
		return listTaskSet.get(index);
	}
	
	public void add(TaskSet taskSet)
	{
		listTaskSet.add(taskSet);
	}
	
	public int size()
	{
		return listTaskSet.size();
	}
	
	public void SampleTaskGenerate()
	{
		Task task = new Task(4);
		task.setTaskID(0);		
		task.setPeriod(8);
		task.setDeadline(8);
		
		task.selectOption(0, 1-1);
		task.selectOption(1, 3-1);
		task.selectOption(2, 2-1);
		task.selectOption(3, 1-1);

		task.setExecutionTime(0, 0, 0, 1);

		task.setExecutionTime(1, 2, 0, 2);
		task.setExecutionTime(1, 2, 1, 2);
		task.setExecutionTime(1, 2, 2, 2);

		task.setExecutionTime(2, 1, 0, 1);
		task.setExecutionTime(2, 1, 1, 1);

		task.setExecutionTime(3, 0, 0, 2);

		this.taskSet.add(task);
		this.taskSet.add(task);

	}

	public void readTasksFromFiles(double alpha, double beta, double gamma)
	{
		String result_alpha = String.format("%03d", (int) (alpha*100));
		String result_beta = String.format("%03d", (int) (beta*100));
		String result_gamma = String.format("%03d", (int) (gamma*100));
		
		String result_multiSetNumber;
		String filename = null;
		for(int multi=1; multi<=10; multi++)
		{
			result_multiSetNumber = String.format("%03d", multi);
			String directory = TaskGenerator.DirectoryName + "/" + result_alpha + "_" + result_beta + "_" + result_gamma + "/";
			filename = directory + result_multiSetNumber + ".txt";
			
			try {
				TaskSet set = TaskReader.readTaskSet(filename);
				listTaskSet.add(set);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static ArrayList<SporadicTask> TransformThreadsToTasks(TaskSet taskSet)
	{
		ArrayList<SporadicTask> newTaskSet = new ArrayList<SporadicTask>();
		for(int i=0; i<taskSet.size(); i++)
		{
			Task task = taskSet.get(i);
			
			for(int seg=0; seg<task.getNumSegments(); seg++)
			{
				for (int thr=0; thr<task.getNumThreadsOfSegment(seg); thr++)
				{
					double wcet = task.getExecutionTimeOfThread(seg, thr);
					SporadicTask newTask = new SporadicTask(task.getPeriod(), wcet, task.getDeadline());
					newTaskSet.add(newTask);
				}
			}
		}
		
		return newTaskSet;
	}
	
	
	public void RandomGenerateSHIN(double RatioParallelTasks, double U) {
		listTaskSet.clear();
		Util util = new Util(0);

		int parallelTask = 0;
		double avgUsys = 0;
		double avgNumberOfTasks = 0;
		double avgPF = 0;
		
		for (int i=0; i<Param.NumTaskSets; i++)
		{
			TaskSet set = new TaskSet();
			
			int j=0;
			double utilization = 0;
			double PF = 0;
			while (j<Param.NumProcessors || utilization < U)
			{				
				// For a task
				int period = (int)util.randomDouble(Param.Period_MIN, Param.Period_MAX);
				int nSegments = util.randomInt(Param.NumSegments_MIN, Param.NumSegments_MAX);
				
				Task task;

				if (j < Param.NumProcessors * RatioParallelTasks)
				{				
					parallelTask++;
					
					// if it is a parallel task
					task = new Task(nSegments);
					task.setTaskID(j);		
					task.setPeriod(period);
					task.setDeadline(period);

					for (int seg=0; seg<nSegments; seg++)
					{
						int nThreads = 1;		
						{
							// TODO: how about more than m?
							nThreads = util.randomInt(Param.NumThreads_MIN, Param.NumThreads_MAX);
							//nThreads = util.randomInt(1, Param.NumProcessors);										
						} 

						int exec = (int)util.randomDouble(Param.WCET_MIN, period/nSegments);
						
						int optionIndex = nThreads-1;
						task.selectOption(seg, optionIndex);
						
						for (int thr=0; thr <= optionIndex; thr++)
						{
							task.setExecutionTime(seg, optionIndex, thr, exec);
						}
					}					
				}
				else	// if it is not a parallel task, it is a sequential task
				{
					task = new Task(1);	// only 1 segment
					task.setTaskID(j);		
					task.setPeriod(period);
					task.setDeadline(period);			

					int exec = (int)util.randomDouble(Param.WCET_MIN, period);

					task.selectOption(0, 0);
					task.setExecutionTime(0, 0, 0, exec);
				}
								
				utilization += task.getTotalExecutionTime() / task.getDeadline();				
				set.add(task);
				j++;

				avgUsys += ( task.getTotalExecutionTime() / task.getDeadline() ) / Param.NumTaskSets;
				PF += ( task.getTotalExecutionTime() / task.getMaxExecutionTime() );
				//System.out.println(task.toString());
			}

			avgNumberOfTasks +=  (double)set.size() / Param.NumTaskSets;
			avgPF += PF / set.size() / Param.NumTaskSets;
			//System.out.println(set.size());
			listTaskSet.add(set);
		}
		
		//System.out.println("n=" + avgNumberOfTasks + "(" + (parallelTask/Param.NumTaskSets) + ")\tUsys=" + avgUsys + "\tPF=" + avgPF);
	}
	

	

}
