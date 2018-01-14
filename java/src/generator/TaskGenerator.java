package generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import multicore_exp.Param;
import multicore_exp.TaskSetPool;
import multicore_exp.Util;
import phase_shifting.ExhaustiveSearchGA;
import data.Task;
import data.TaskSet;

public class TaskGenerator {

	public static final String DirectoryName = "task_set_data/";
	
	protected enum Type
	{
		FIXED,
		UNIFORM,
		NORMAL,
		UUNIFAST_FIXED,
		UUNIFAST_UNIFORM,
	};
	protected double alpha;
	protected double alpha_from;
	protected double alpha_to;
	protected double alpha_interval ;
	protected double alpha_deviation;
	protected Type alpha_type;
	
	protected double beta;
	protected double beta_from;
	protected double beta_to;
	protected double beta_interval;
	protected double beta_deviation;
	protected Type beta_type;
	
	protected double gamma;
	protected double gamma_from;
	protected double gamma_to;
	protected double gamma_interval;
	protected double gamma_deviation;
	protected Type gamma_type;
	
	protected int taskNum;
	protected int taskNum_from;
	protected int taskNum_to;
	protected Type taskNum_type;
	
	protected int scmin	= Param.scmin;
	protected int scmax	= Param.scmax; 				
//	protected int omax	= Param.omax;
	protected int omax	= Param.NumThreads_MAX;
	protected int kmin	= Param.NumSegments_MIN;
	protected int kmax	= Param.NumSegments_MAX;
	
	protected int seedCounter = 0;
	
	public TaskGenerator()
	{
	}
	
	public double getAlpha_from()
	{
		return this.alpha_from;
	}
	public double getAlpha_to()
	{
		return this.alpha_to;
	}
	
	public void setFixedAlpha(double alpha)
	{
		this.alpha = alpha;
		alpha_type = Type.FIXED;
	}
	public void setRandomAlpha(double alpha_from, double alpha_to)
	{
		this.alpha = -1.0;
		this.alpha_from = alpha_from;
		this.alpha_to = alpha_to;
		alpha_type = Type.UNIFORM;
	}
	public void setFixedBeta(double beta)
	{
		this.beta = beta;
		beta_type = Type.FIXED;
	}
	public void setRandomBeta(double beta_from, double beta_to)
	{
		this.beta = -1.0;
		this.beta_from = beta_from;
		this.beta_to = beta_to;
		beta_type = Type.UNIFORM;
	}
	public void setFixedBetaSum(double betasum)
	{
		this.beta = betasum;
		beta_type = Type.UUNIFAST_FIXED;
	}
	public void setRandomBetaSum(double beta_from, double beta_to)
	{
		this.beta = -1.0;
		this.beta_from = beta_from;
		this.beta_to = beta_to;
		beta_type = Type.UUNIFAST_UNIFORM;
	}
	public void setNormalDistributionBeta(double beta, double deviation)
	{
		this.beta = beta;
		this.beta_deviation = deviation;
		beta_type = Type.NORMAL;
	}
	public void setFixedGamma(double gamma)
	{
		this.gamma = gamma;
		gamma_type = Type.FIXED;
	}
	public void setRandomGamma(double gamma_from, double gamma_to)
	{
		this.gamma = -1.0;
		this.gamma_from = gamma_from;
		this.gamma_to = gamma_to;
		gamma_type = Type.UNIFORM;
	}
	public void setNormalDistributionGamma(double gamma, double deviation)
	{
		this.gamma = gamma;
		this.gamma_deviation = deviation;
		gamma_type = Type.NORMAL;
	}
	public void setFixedTaskNum(int taskNum)
	{
		this.taskNum = taskNum;
	}
	public void setRandomTaskNum(int taskNum_from, int taskNum_to)
	{
		this.taskNum = -1;
		this.taskNum_from = taskNum_from;
		this.taskNum_to = taskNum_to;
	}

	public Task GenerateTaskWithInteger(double alpha, double beta, double gamma, int taskID, int seed)
	{
		Util util = new Util(seed);
		
		int numSegments = util.randomInt(kmin, kmax); 	
		double sumOneThreads = 0;
		
		Task task = new Task(numSegments);
		
		for(int j = 0; j < numSegments ; j++)
		{
			double wcetOneThread = util.randomDouble(scmin, scmax);
			sumOneThreads = sumOneThreads + wcetOneThread;

			for(int k = 0; k < omax ; k++)
			{
				int Oik = k + 1;
				double wcetEachThread = (alpha * wcetOneThread) +( (1 - alpha) * wcetOneThread / Oik);
				for(int x = 0; x < Oik ; x++)
				{
//					System.out.println(j + " " + k + " " + x + " " + wcetEachThread);
					task.setExecutionTime(j,  k,  x, (int)wcetEachThread);
				}
			}
		}
		
		task.setDeadline( (int)(sumOneThreads * beta) );
		task.setPeriod( (int)(task.getDeadline() * gamma) );
		task.setTaskID(taskID);
		task.setInformation(seed, scmin, scmax, omax, kmin, kmax);
		
		return task;
		
	}
	
	public Task GenerateTask(double alpha, double beta, double gamma, int taskID, int seed)
	{
		boolean allOptionsSameIntermediateDeadline = true;
		Util util = new Util(seed);
		int numSegments = util.randomInt(kmin, kmax); 	
		
		int sumOneThreads = 0;
		Task task = new Task(numSegments);
		
		for(int j = 0; j < numSegments ; j++)
		{
			int wcetOneThread = util.randomInt(scmin, scmax);
			sumOneThreads = sumOneThreads + wcetOneThread;

			for(int k = 0; k<omax; k++)
			{
				int Oik = k + 1;
				int wcetEachThread = (int) ((alpha * wcetOneThread) +( (1 - alpha) * wcetOneThread / Oik));
				if(wcetEachThread == 0) wcetEachThread = 1;
				
				for(int x = 0; x < Oik ; x++)
					task.setExecutionTime(j,  k,  x, wcetEachThread);
			}
		}
		
		task.setDeadline((int) (sumOneThreads / beta));
		
		if(allOptionsSameIntermediateDeadline == true)
		{
			for(int i= 0 ; i < task.getNumSegments() ; i++)
			{
				task.setIntermediateDeadline(i, (int) (sumOneThreads / beta));
			}
		}
		
		
		//double deadline = (int)(sumOneThreads * beta / 10.0) * 10;
//		int deadline = (int)(sumOneThreads * beta / 5.0) * 10;
		
//		if (deadline == 0) deadline = 10;
//		task.setDeadline(deadline);
		task.setPeriod((int) (task.getDeadline() / gamma));
		task.setTaskID(taskID);
		task.setInformation(seed, scmin, scmax, omax, kmin, kmax);
		
		return task;
		
	}
	
	public Task GenerateTask(int taskID, int seed)
	{
		return GenerateTask(alpha, beta, gamma, taskID, seed);
	}
	
	protected TaskSet GenerateTaskSet(double alpha, double beta, double gamma, int taskNum, int taskSetID, int seed)
	{
		Util baseutil = new Util(seed);
		int baseIter = baseutil.randomInt(0, 1000);
		int tempVal = 0;
		for (int i = 0; i < baseIter; i++)
		{
			tempVal += baseutil.randomInt(0, Integer.MAX_VALUE / 2);
		}
		tempVal = baseutil.randomInt(0, Integer.MAX_VALUE / 2);
		Util util = new Util(tempVal);
		TaskSet taskSet = new TaskSet(taskSetID, alpha, beta, gamma);
		int baseSeed = util.randomInt(0, Integer.MAX_VALUE / 2);
		
		if(taskNum < 0)
			taskNum = util.randomInt(taskNum_from, taskNum_to);
		
//		System.out.printf("%d ", taskNum);
		
		double alpha_ = 0, beta_ = 0, gamma_ = 0;

		double[] betas = new double[taskNum];
		
		if (beta_type == Type.UUNIFAST_FIXED || beta_type == Type.UUNIFAST_UNIFORM)
		{
			if (beta_type == Type.UUNIFAST_UNIFORM)
				beta = util.randomDouble(beta_from, beta_to);
			
			betas = util.unifastProportional(beta, taskNum);
		}

		for (int i = 0; i < taskNum; i++)
		{
			switch (alpha_type)
			{
			case FIXED:		alpha_ = alpha;	break;
			case UNIFORM:	alpha_ = util.randomDouble(alpha_from, alpha_to);	break;
			case NORMAL:	alpha_ = util.randomDoubleNormalDistribution(alpha, alpha_deviation);	break;
			default:		System.out.println("undefined alpha_type");
			}
				
			
			switch (beta_type)
			{
			case FIXED: 	beta_ = beta;	break;
			case UNIFORM:	beta_ = util.randomDouble(beta_from, beta_to);	break;
			case NORMAL:	beta_ = util.randomDoubleNormalDistribution(beta, beta_deviation);	break;
			case UUNIFAST_FIXED:
			case UUNIFAST_UNIFORM:
				beta_ = betas[i];
				break;
			default: 		System.out.println("undefined beta_type\n");
			}
			
			switch (gamma_type)
			{
			case FIXED:		gamma_ = gamma;	break;
			case UNIFORM:	gamma_ = util.randomDouble(gamma_from, gamma_to);	break;
			case NORMAL:	gamma_ = util.randomDoubleNormalDistribution(gamma, gamma_deviation);	break;
			default:		System.out.println("undefined gamma_type");
			}
			
			
			int taskID = i + 1;
			
			taskSet.add(GenerateTask(alpha_, beta_, gamma_, taskID, baseSeed + i));
			//taskSet.add(GenerateTaskWithInteger(alpha_, beta_, gamma_, taskID, baseSeed + i));	
		}	
		
		return taskSet;
	}
	
	public TaskSet GenerateTaskSet(int taskSetID, int seed)
	{		
		return GenerateTaskSet(alpha, beta, gamma, taskNum, taskSetID, seed);
	}
	
	
	protected TaskSetPool GenerateTaskPool(double alpha, double beta, double gamma, int taskSetNum, int seed)
	{
		Util util = new Util(seed);
		TaskSetPool taskPool = new TaskSetPool();
		int baseSeed = util.randomInt(0, Integer.MAX_VALUE / 2);
		
		for (int i = 0; i < taskSetNum; i++)
		{
			int taskNum = this.taskNum;			
			if (taskNum < 0)
				taskNum = util.randomInt(taskNum_from, taskNum_to);
			
			int taskSetID = i + 1;
			TaskSet taskSet = GenerateTaskSet(alpha, beta, gamma, taskNum, taskSetID, baseSeed + i);
			taskPool.add(taskSet);;
		}
		
		return taskPool;		
	}
	public TaskSetPool GenerateTaskPool(int taskSetNum, int seed)
	{
		return GenerateTaskPool(alpha, beta, gamma, taskSetNum, seed);
	}
	public TaskSetPool GenerateTaskPool(int taskSetNum)
	{
		return GenerateTaskPool(taskSetNum, 0);	
	}
	
	public void WriteToFile(TaskSet taskSet)
	{
		double alpha = taskSet.getAlpha();
		double beta = taskSet.getBeta();
		double gamma = taskSet.getGamma();
		int taskSetID = taskSet.getTaskSetID();
		
		String result_alpha = String.format("%03d", (int) (alpha*100));
		String result_beta = String.format("%03d", (int) (beta*100));
		String result_gamma = String.format("%03d", (int) (gamma*100));
		String result_taskSetID = String.format("%03d", taskSetID);
		

		if (alpha < 0) result_alpha = "rnd";
		if (beta < 0) result_beta = "rnd";
		if (gamma < 0) result_gamma = "rnd";
				
		String filename;
		
		String directory = DirectoryName + "/" + result_alpha + "_" + result_beta + "_" + result_gamma + "/";
		File dir = new File(directory);
		dir.mkdirs();
		filename = directory + result_taskSetID + ".txt";

		File f = new File(filename);
		
		try {						
			BufferedWriter bw  = new BufferedWriter(new FileWriter(f));
			
			bw.write(String.format("# number of tasks = %d", taskSet.size()));
			bw.newLine();

			for (int i = 0; i < taskSet.size(); i++) 
			{
				bw.write(taskSet.get(i).toString());
			}
			
			bw.close();

					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void WriteToFile(TaskSetPool taskPool)
	{
		for (int i = 0; i < taskPool.size(); i++)
		{
			WriteToFile(taskPool.get(i));
		}	
	}

	public void GeneratorCombinations()
	{
		new File("./" + DirectoryName).mkdir();
		for(double i = alpha_from ; i <= alpha_to ; i = i + alpha_interval)
		{
			for(double j = beta_from ; j <= beta_to ; j = j + beta_interval)
			{
				for(double k = gamma_from ; k <= gamma_to ; k = k + gamma_interval)
				{
					//String result_alpha = String.format("%03d", (int) (i*100));
					//String result_beta = String.format("%03d", (int) (j*100));
					//String result_gamma = String.format("%03d", (int) (k*100));
					//String directoryName = result_alpha + "_" + result_beta + "_" + result_gamma;
					
					//new File("./" + directoryName).mkdir();
					
					int taskPoolNum = 100;
					int seed = 0;
					
					TaskSetPool taskPool = GenerateTaskPool(i, j, k, taskPoolNum, seed);
					WriteToFile(taskPool);
				}
			}
		}		
	}
/*
	

	public void GeneratorCombinationsMultiSet()
	{
		taskNum = 50;
		
		alpha_from 	= 0.0;
		alpha_to 	= 1.0;
		alpha_interval = 0.2;
		
		beta_from 	= 1.0;
		beta_to 		= 3.0;
		beta_interval = 0.5;
		
		gamma_from 	= 1.0;
		gamma_to 	= 3.0;
		gamma_interval = 0.5;
	
		new File("./" + DirectoryName).mkdir();
		for(double i = alpha_from ; i <= alpha_to ; i = i + alpha_interval)
		{
			for(double j = beta_from ; j <= beta_to ; j = j + beta_interval)
			{
				for(double k = gamma_from ; k <= gamma_to ; k = k + gamma_interval)
				{
					//String result_alpha = String.format("%03d", (int) (i*100));
					//String result_beta = String.format("%03d", (int) (j*100));
					//String result_gamma = String.format("%03d", (int) (k*100));
					//String directoryName = result_alpha + "_" + result_beta + "_" + result_gamma;
					
					//new File("./" + directoryName).mkdir();
					for (int multi=1; multi <= 100; multi++)
					{					
						Generator(i, j, k, taskNum, multi);
						System.out.println(multi);
					}
				}
			}
		}		
	}
	
	
	public void Generator(double alpha, double beta, double gamma, int taskNum, int multiSetNumber)
	{	
		taskSet = new TaskSet();
		for(int i=0; i<taskNum ; i++)
		{
			int seed = i + multiSetNumber;					// multiSetNumber is zero if not the case of multiSet generation
			Util util = new Util(seed);
			

			int numSegments = util.randomInt(kmin, kmax); 	//
			double sumOneThreads = 0;
			
			Task task = new Task(numSegments);
			
			for(int j = 0; j < numSegments ; j++)
			{
				double wcetOneThread = util.randomDouble(scmin, scmax);
				sumOneThreads = sumOneThreads + wcetOneThread;

				for(int k = 0; k < omax ; k++)
				{
					int Oik = k+1;
					double wcetEachThread = (alpha * wcetOneThread) +( (1 - alpha) * wcetOneThread / Oik);
					for(int x = 0; x < Oik ; x++)
					{
//						System.out.println(j + " " + k + " " + x + " " + wcetEachThread);
						task.setExecutionTime(j,  k,  x, wcetEachThread);
					}
				}
			}
			
			task.setDeadline(sumOneThreads * beta);
			task.setPeriod(task.getDeadline() * gamma);
			task.setTaskID(i);
			task.setInformation(seed, scmin, scmax, omax, kmin, kmax);

			this.taskSet.add(task);
			
		}
		WCETTableToText(alpha, beta, gamma, taskNum, multiSetNumber);		
		
		return taskSet;
	}

	public void WCETTableToText(double alpha, double beta, double gamma, int taskNum, int multiSetNumber) 
	{
		String result_alpha = String.format("%03d", (int) (alpha*100));
		String result_beta = String.format("%03d", (int) (beta*100));
		String result_gamma = String.format("%03d", (int) (gamma*100));
		String filename;
		if (multiSetNumber < 0) {
			filename = DirectoryName + result_alpha + "_" + result_beta + "_" + result_gamma + ".txt";			
		} else {
			String result_multiSetNumber = String.format("%03d", multiSetNumber);
			String directory = DirectoryName + "/" + result_alpha + "_" + result_beta + "_" + result_gamma + "/";
			File dir = new File(directory);
			dir.mkdirs();
			filename = directory + result_multiSetNumber + ".txt";
		}
		
		File f = new File(filename);
		
		try {						
			BufferedWriter bw  = new BufferedWriter(new FileWriter(f));
			
			bw.write(String.format("# number of tasks = %d", taskSet.size()));
			bw.newLine();

			for (int i = 0; i < taskSet.size(); i++) 
			{
				bw.write(taskSet.get(i).toString());
			}
			
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	
	public static void main(String[] args) 
	{
		int seed = 0;
		int population_count = 100;
		int generation_count = 1000;
		TaskGenerator generator_multi_segment = new TaskGenerator();
		generator_multi_segment.setFixedTaskNum(5);
		generator_multi_segment.setRandomAlpha(0.0, 1.0);
		generator_multi_segment.setRandomBeta(0.0, 1.0);
		generator_multi_segment.setFixedGamma(0.1);
		TaskSet taskSet_multi_segment = generator_multi_segment.GenerateTaskSet(1,seed);
		
		// what is it?
		//ExhaustiveSearchGA.gaMain(seed, taskSet_multi_segment, population_count, generation_count);

	}		
}


