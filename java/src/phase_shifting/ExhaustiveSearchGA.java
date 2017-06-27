package phase_shifting;

import generator.TaskGenerator;
import generator.TaskGeneratorSingleSegment;
import logic.PhaseShiftingHarmonic;
import data.PhasedTask;
import data.SporadicTask;
import data.Task;
import data.TaskSet;
import multicore_exp.Param;
import multicore_exp.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.plaf.synth.SynthScrollBarUI;

import period_harmonizer.PeriodHarmonizer;
import period_harmonizer.TwoFivePeriod;

public class ExhaustiveSearchGA {
	public static int population_count;
	public static int generation_count;
	public static int selectedIndices[];

	public ExhaustiveSearchGA() {
		this.population_count = 6;
		this.generation_count = 100000;
		this.selectedIndices = new int[2];
	}
	
	public ExhaustiveSearchGA(int population_count, int generation_count) {
		this.population_count = population_count;
		this.generation_count = generation_count;
		this.selectedIndices = new int[2];
	}
	
	public static void main(String[] args) 
	{
		int pop = 6;
		int gen = 100000;
		
//		System.out.printf("\n\n>>Not harmonized\n");
//		test2(pop, gen);
		System.out.printf("\n\n>>Harmonized\n");
		test3(pop, gen);

		
//		Param.NumProcessors = 4;
//		Param.NumThreads_MAX = 4;
//		Param.scmin = 10;
//		Param.scmax = 50;
//		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
//		generator.setRandomAlpha(0, 1);
//		generator.setRandomBeta(0.1, 1.2);
//		generator.setRandomGamma(0.1, 1.0);
//		generator.setRandomTaskNum(3,10);
//		int seed = 11;
//		
//		ExhaustiveSearchGA ga = new ExhaustiveSearchGA();
//		System.out.println(ga.isSchedulableByGA(generator, seed));
		
	}
	
	
	public static boolean isSchedulableByGA(TaskGenerator generator, int seed)
	{
//		int pop_count = 6;
//		int gen_count = 100000;
//		ExhaustiveSearchGA ga = new ExhaustiveSearchGA(pop_count, gen_count);
		Util util = new Util(seed);
		Util util_ga = new Util(seed+1);
		return isSchedulable3(generator, seed, util, util_ga, true);
	}
	
	
	
	
	public static void test3(int pop_count, int gen_count) 
	{
		System.out.println("Exhaustive Search by GA Starts...");

		/* initialize util */
		Util util = new Util(0);
		Util util_ga = new Util(1);
		
		boolean changeDeadline = true;
		
		/* initialize GA parameters */
//		int pop_count = 6;
//		int gen_count = 1000;
		ExhaustiveSearchGA ga = new ExhaustiveSearchGA(pop_count, gen_count);

		/* initialize task generator parameters */
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		generator.setRandomAlpha(0, 1);
		generator.setRandomBeta(0.1, 1.2);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);

		int taskset_count = 100;
		double count = 0.0;
		double pts_count = 0.0;
		
		for (int cnt = 0; cnt < taskset_count; cnt++) 
		{		
			if (ga.isSchedulable3(generator, cnt, util, util_ga, changeDeadline) == true)
				count = count + 1.0;

			System.out.printf("\t\t\t\t%d / %d\n", (int)count, cnt + 1);
		}
		System.out.printf("%.2f\n", count/taskset_count);

		System.out.println("Exhaustive Search by GA Ends...");
	}
	
	
	public static void test2(int pop_count, int gen_count) 
	{
		System.out.println("Exhaustive Search by GA Starts...");

		/* initialize util */
		Util util = new Util(0);
		Util util_ga = new Util(1);
		
		boolean changeDeadline = true;
		
		/* initialize GA parameters */
//		int pop_count = 6;
//		int gen_count = 1000;
		ExhaustiveSearchGA ga = new ExhaustiveSearchGA(pop_count, gen_count);

		/* initialize task generator parameters */
		Param.NumProcessors = 4;
		Param.NumThreads_MAX = 4;
		Param.scmin = 10;
		Param.scmax = 50;
		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		generator.setRandomAlpha(0, 1);
//		generator.setFixedAlpha(0);
		generator.setRandomBeta(0.1, 1.2);
		generator.setRandomGamma(0.1, 1.0);
		generator.setRandomTaskNum(3,10);

		int taskset_count = 100;
		double count = 0.0;
		double pts_count = 0.0;
		
		for (int cnt = 0; cnt < taskset_count; cnt++) 
		{			
			
			
			if (ga.isSchedulable2(generator, cnt, util, util_ga, changeDeadline) == true)
				count = count + 1.0;

			System.out.printf("\t\t\t\t%d / %d\n", (int)count, cnt + 1);
		}
		System.out.printf("%.2f\n", count/taskset_count);
		
		System.out.println("Exhaustive Search by GA Ends...");
	}
	
	
	public static void test1() 
	{
		System.out.println("Exhaustive Search by GA Starts...");

		/* initialize util */
		Util util = new Util(0);
		Util util_ga = new Util(1);
		
		boolean changeDeadline = true;
		
		
		/* initialize GA parameters */
		int pop_count = 6;
		int gen_count = 0;
		ExhaustiveSearchGA ga = new ExhaustiveSearchGA(pop_count, gen_count);

		/* initialize task generator parameters */
		Param.NumProcessors = 2;
		Param.NumThreads_MAX = 4;
		TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
		generator.setRandomBeta(0.0, 1.0);
		generator.setRandomAlpha(0, 1);
		generator.setRandomTaskNum(3, 10);

		
//		for (double gamma = 0.6; gamma <= 0.6; gamma += 0.1)
		for (double gamma = 0.1; gamma <= 1.0; gamma += 0.1)
		{
			generator.setFixedGamma(gamma);

			int taskset_count = 100;
			double count = 0.0;
			double pts_count = 0.0;
			
			for (int cnt = 0; cnt < taskset_count; cnt++) 
			{
//				System.out.printf("[%.1f\t%d]\n", gamma, cnt);
//				ArrayList<PhasedTask> phasedTaskSet = generator.GenerateTaskSetWithLimitedLCM(cnt, cnt, 10000).toPhasedTask(TaskSet.ParallelizedOption.SINGLE);
//				if(PhaseShiftingHarmonic.isSchedulable(phasedTaskSet, Param.NumProcessors))
//					pts_count = pts_count + 1.0;
								
				if (ga.isSchedulable(generator, cnt, util, util_ga, changeDeadline) == true) 
					count = count + 1.0;

//				System.out.printf("\n");
//				gaWriteTaskSetInformation(tasksetChromosome[gaMinTotalPeakDensityIndex(tasksetChromosome)]);
			}
//			System.out.printf("%.1f\t%.2f\t\n", gamma, pts_count/taskset_count);
			System.out.printf("%.1f\t%.2f\n", gamma, count/taskset_count);
		}
		System.out.println("Exhaustive Search by GA Ends...");
	}
	
	public boolean isSchedulable(TaskGeneratorSingleSegment generator, int cnt, Util util, Util util_ga, boolean changeDeadline) 
	{
		TaskSet[] tasksetChromosome = new TaskSet[population_count];
		for (int pop = 0; pop < population_count; pop++)
			tasksetChromosome[pop] = generator.GenerateTaskSetWithLimitedLCM(cnt, cnt, 10000);
		
		return ga_main(cnt, util, util_ga, tasksetChromosome, changeDeadline);
	}
	
	public boolean isSchedulable2(TaskGeneratorSingleSegment generator, int cnt, Util util, Util util_ga, boolean changeDeadline) 
	{	
		//TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		TaskSet[] tasksetChromosome = new TaskSet[population_count];
			
		for (int pop = 0; pop < population_count; pop++)
		{
			tasksetChromosome[pop] = generator.GenerateTaskSet(cnt, cnt);
			//harmonizer25.modify(tasksetChromosome[pop]);
			
			if(!tasksetChromosome[pop].isValid())
				return false;
			
		}
		return ga_main(cnt, util, util_ga, tasksetChromosome, changeDeadline);
	}
	
	// period harmonize 됐을때 GA 성능 
	public static boolean isSchedulable3(TaskGenerator generator, int cnt, Util util, Util util_ga, boolean changeDeadline) 
	{
		//TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		TaskSet[] tasksetChromosome = new TaskSet[population_count];
		boolean ret;
		for (int pop = 0; pop < population_count; pop++)
		{
			tasksetChromosome[pop] = generator.GenerateTaskSet(cnt, cnt);
			//harmonizer25.modify(tasksetChromosome[pop]);
//			PeriodHarmonizer harmonizer = new PeriodHarmonizer();
//			ret = harmonizer.harmonize(tasksetChromosome[pop], TaskSet.ParallelizedOption.BEST_EFFORT);
//			if (!ret) return false;
			if(!tasksetChromosome[pop].isValid())
				return false;
			
		}
		return ga_main(cnt, util, util_ga, tasksetChromosome, changeDeadline);
	}
	// period harmonize 됐을때 GA 성능 
	public static double getPeakDensity(TaskGenerator generator, int cnt, Util util, Util util_ga, boolean changeDeadline) 
	{
		//TwoFivePeriod harmonizer25 = new TwoFivePeriod();
		TaskSet[] tasksetChromosome = new TaskSet[population_count];
		boolean ret;
		for (int pop = 0; pop < population_count; pop++)
		{
			tasksetChromosome[pop] = generator.GenerateTaskSet(cnt, cnt);
			//harmonizer25.modify(tasksetChromosome[pop]);
//			PeriodHarmonizer harmonizer = new PeriodHarmonizer();
//			ret = harmonizer.harmonize(tasksetChromosome[pop], TaskSet.ParallelizedOption.BEST_EFFORT);
//			if (!ret) return false;
			if(!tasksetChromosome[pop].isValid())
				return Param.NumProcessors * 2;
			
		}
		ga_main(cnt, util, util_ga, tasksetChromosome, changeDeadline);
		
		double minPeak = -1;
		for (int i = 0; i < tasksetChromosome.length; i++)
		{
			if (minPeak < 0 || minPeak > tasksetChromosome[i].getTotalPeakDensity())
				minPeak = tasksetChromosome[i].getTotalPeakDensity();
		}
		
		return minPeak;
	}
	
	public static boolean ga_main(int taskset_ID, Util util, Util util_ga, TaskSet[] tasksetChromosome, boolean changeDeadline) 
	{
		/* Perturb the chromosome set */
		if(gaInit_Initialize(tasksetChromosome, util)==false)
		{
			System.out.println("harmonize failed\n");
			return false;
		}
		
		for (int p = 0; p < population_count; p++)
			tasksetChromosome[p].setTotalPeakDensity(calculateTotalPeakDensity(tasksetChromosome[p]));

//		gaPrintPopulationTotalPeakDensity(tasksetChromosome);
		
		for (int gc = 0; gc < generation_count; gc++) 
		{
//			System.out.printf("[%d]\n", gc);
			/* GA Selection, selected index as global variable */
			gaSelection(util_ga);
			
			/* GA Crossover */
			TaskSet newtaskset = gaCrossover(util_ga, tasksetChromosome);
			newtaskset.setTotalPeakDensity(calculateTotalPeakDensity(newtaskset));
//			System.out.printf("after Crossover : %.2f\n", newtaskset.getTotalPeakDensity());
			
			/* GA Mutation */
			gaMutateTask(util_ga, newtaskset, changeDeadline);
			newtaskset.setTotalPeakDensity(calculateTotalPeakDensity(newtaskset));
//			System.out.printf("after Mutation : %.2f\n", newtaskset.getTotalPeakDensity());
			
			/*GA Replacement */
			int max_index = getMaxIndex(tasksetChromosome);
			if(newtaskset.getTotalPeakDensity() < tasksetChromosome[max_index].getTotalPeakDensity())
				tasksetChromosome[max_index] = newtaskset;
			
//			System.out.print(">>> after replace\t"); gaPrintPopulationTotalPeakDensity(tasksetChromosome);
			
			double min_t_p_d = gaMinTotalPeakDensity(tasksetChromosome);
			if (min_t_p_d <= Param.NumProcessors) {
				break;
			}

			if(gc % 4 == 0)
			{
				/*GA Local Optimization */
				gaLocalOptimization(tasksetChromosome);
				
				min_t_p_d = gaMinTotalPeakDensity(tasksetChromosome);
				if (min_t_p_d <= Param.NumProcessors) {
					break;
				}
			}
			
//			System.out.print(">>>>> after local optimization\t"); gaPrintPopulationTotalPeakDensity(tasksetChromosome);
			
//			System.out.printf("\n");
		}

		if(hasSchedulable(tasksetChromosome) == true){
			//writeSchedulableTasksetToFileFromPopulation(tasksetChromosome);
			return true;
		}
		else
			return false;
	}

	
	public static void writeSchedulableTasksetToFileFromPopulation(TaskSet[] tasksetChromosome) {
		int schedulable_index = -1;
		
		for (int p = 0; p < population_count; p++) {
			if (tasksetChromosome[p].getTotalPeakDensity() <= Param.NumProcessors){
				schedulable_index = p;
				break;
			}
		}
		writeSchedulableTasksetToFile(tasksetChromosome[schedulable_index]);
	}
	
	public static void writeSchedulableTasksetToFile(TaskSet taskset) {
		
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("GAResult.txt", true));
		
			int lcmOfTasks = 1;
			double min_tpd = taskset.getTotalPeakDensity();
			for (int tn = 0; tn < taskset.getTaskNum(); tn++)
				lcmOfTasks = lcm(lcmOfTasks, (int) (taskset.get(tn).getPeriod()));
			
//			for(int tn =0; tn<taskset.getTaskNum() ; tn++)
//			{
//				System.out.printf("\tT%d", tn);
//				out.write("\tT");
//				out.write(tn);
//			}
//			System.out.printf("\n");
//			out.newLine();
			
			for(double t = 0 ; t<lcmOfTasks ; t=t+1.0){
				
				System.out.printf("%.2f\t", t);
				out.write(Double.toString(t));
				out.write("\t");
				
				double tpd = 0; 
				for(int tn =0; tn<taskset.getTaskNum() ; tn++){
					tpd = tpd + taskset.get(tn).getDensityAtT(t, (double)taskset.get(tn).getPhase());
				}
				System.out.printf("%.2f\t", tpd);
				out.write(Double.toString(tpd));
				out.write("\t");
				
				for(int tn =0; tn<taskset.getTaskNum() ; tn++){
					System.out.printf("%.2f\t", taskset.get(tn).getDensityAtT(t, (double)taskset.get(tn).getPhase()) );
					out.write(Double.toString(taskset.get(tn).getDensityAtT(t, (double)taskset.get(tn).getPhase())));
					out.write("\t");
				}
				System.out.printf("\n");
				out.newLine();
			}
			
			System.out.println("=====\n");
			out.write("=====");
			out.newLine();
			out.newLine();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		int lcmOfTasks = 1;
//		double min_tpd = taskset.getTotalPeakDensity();
//		for (int tn = 0; tn < taskset.getTaskNum(); tn++)
//			lcmOfTasks = lcm(lcmOfTasks, (int) (taskset.get(tn).getPeriod()));
//		
//		for(int tn =0; tn<taskset.getTaskNum() ; tn++)
//			System.out.printf("\tT%d", tn);
//		System.out.printf("\n");
//		
//		for(double t = 0 ; t<lcmOfTasks ; t=t+1.0){
//			for(int tn =0; tn<taskset.getTaskNum() ; tn++){
//				System.out.printf("%.2f\t", taskset.get(tn).getDensityAtT(t, (double)taskset.get(tn).getPhase()) );
//			}
//			System.out.printf("\n");
//		}
//		
//		System.out.println("=====\n");
		
		
		
	}
	
	public static void gaLocalOptimization(TaskSet[] tasksetChromosome) 
	{
		int min_index = getMinIndex(tasksetChromosome);
		
//		setBestPhase(tasksetChromosome[min_index]);
//		setBestOption(tasksetChromosome[min_index]);
//		setBestPhaseAndDeadline(tasksetChromosome[min_index]);
//		setBestMinDeadline(tasksetChromosome[min_index]);
//		setBestMaxDeadline(tasksetChromosome[min_index]);
		setRandomDeadlineFitOption(tasksetChromosome[min_index], 3);
	}
	
	
	public static void setRandomDeadlineFitOption(TaskSet taskset, int count)
	{
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++){
			double prev_tpd = taskset.getTotalPeakDensity();
			int max_deadline = taskset.get(tn).getDeadline();
			
			for(int i = 1; i < count+1 ; i++){
				int prev_d = taskset.get(tn).getIntermediateDeadline(0);
				int prev_o = taskset.get(tn).selectedOption(0);
				
				int d = (int)(max_deadline / count) * i;
				if(d < taskset.get(tn).getMaxExecutionTimeOfSegment(0, Param.NumThreads_MAX-1) )
					continue;
				
				taskset.get(tn).setIntermediateDeadline(0, d);
				
				for(int option = 0 ; option < Param.NumThreads_MAX ; option++){
					if(taskset.get(tn).getMaxExecutionTimeOfSegment(0, option) > d)
						continue;
					else{
						taskset.get(tn).selectOption(0, option);
						break;
					}
				}
				
				double new_tpd = calculateTotalPeakDensity(taskset);	
				if(new_tpd < prev_tpd)
					taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
				else{
					taskset.get(tn).setIntermediateDeadline(0, prev_d);
					taskset.get(tn).selectOption(0, prev_o);
				}
			}
		}
	}
	
	public static void setBestMaxDeadline(TaskSet taskset)
	{
		
		double min_tpd = taskset.getTotalPeakDensity();
		
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++)
		{
			for(int option = 0 ; option < Param.NumThreads_MAX ; option++)
			{
				int orignal_option = taskset.get(tn).selectedOption(0);
				int orignal_deadline = taskset.get(tn).getIntermediateDeadline(0);
				
				taskset.get(tn).selectOption(0, option);
				int max_deadline = taskset.get(tn).getPeriod();
				taskset.get(tn).setIntermediateDeadline(0, max_deadline);
				double new_tpd = calculateTotalPeakDensity(taskset);
				if(new_tpd < min_tpd)
				{
					min_tpd = new_tpd;
					taskset.setTotalPeakDensity(new_tpd);
				}
				else
				{
					taskset.get(tn).selectOption(0, orignal_option);
					taskset.get(tn).setIntermediateDeadline(0, orignal_deadline);
				}
			}
		}
	}
	
	public static void setBestMinDeadline(TaskSet taskset)
	{
		double min_tpd = taskset.getTotalPeakDensity();
		
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++)
		{
			for(int option = 0 ; option < Param.NumThreads_MAX ; option++)
			{
				int orignal_option = taskset.get(tn).selectedOption(0);
				int orignal_deadline = taskset.get(tn).getIntermediateDeadline(0);
				
				taskset.get(tn).selectOption(0, option);
				int min_deadline = taskset.get(tn).getMaxExecutionTimeOfSegment(0, option);
				taskset.get(tn).setIntermediateDeadline(0, min_deadline);
				double new_tpd = calculateTotalPeakDensity(taskset);
				if(new_tpd < min_tpd)
				{
					min_tpd = new_tpd;
					taskset.setTotalPeakDensity(new_tpd);
				}
				else
				{
					taskset.get(tn).selectOption(0, orignal_option);
					taskset.get(tn).setIntermediateDeadline(0, orignal_deadline);
				}
			}
		}
	}
	
	public static void setBestPhaseAndDeadline(TaskSet taskset)
	{
		int lcmOfTasks = 1;
		double min_tpd = taskset.getTotalPeakDensity();
		
		for (int tn = 0; tn < taskset.getTaskNum(); tn++)
			lcmOfTasks = lcm(lcmOfTasks, (int) (taskset.get(tn).getPeriod()));
		
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++)
		{
//			int minDeadline = taskset.get(tn).getMaxExecutionTimeOfSegment(0, 0);
//			int maxDeadline = taskset.get(tn).getDeadline();
			
			
			
			int best_phase = -1;
			for(int ph = 0; ph< lcmOfTasks ; ph++)
			{
				//int prevPh = taskset.get(tn).getPhase();
				taskset.get(tn).setPhase(ph);
				double cur_tpd = calculateTotalPeakDensity(taskset);
				//taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
				
				if(cur_tpd < min_tpd)
				{
					min_tpd = cur_tpd;
					best_phase = ph;
				}
			}
			taskset.get(tn).setPhase(best_phase);
			taskset.setTotalPeakDensity(min_tpd);
//			System.out.printf("%d\t%.2f\n", lcmOfTasks , min_tpd);
			
			
		
//			for(int d = minDeadline; d <= maxDeadline ; d++)
//			{
//				int option = 0;
//				
//				for(int thn = 0 ; thn < Param.NumThreads_MAX ; thn++)
//				{
//					if(d >= taskset.get(tn).getMaxExecutionTimeOfSegment(0, thn))
//					{
//						option = thn;
//						break;
//					}
//				}
//				int prevD = taskset.get(tn).getIntermediateDeadline(0);
//				int prevO = taskset.get(tn).selectedOption(0);
//				int prevPh = taskset.get(tn).getPhase();
//				
//				taskset.get(tn).setIntermediateDeadline(0, d);
//				taskset.get(tn).selectOption(0, option);
//				
//				for(int ph = 0; ph< lcmOfTasks ; ph++)
//				{
//					taskset.get(tn).setPhase(ph);
//					taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
//					
//					if(taskset.getTotalPeakDensity() < prevTPD)
//					{
//						prevTPD = taskset.getTotalPeakDensity();
//					}
//					else
//					{
//						taskset.get(tn).setIntermediateDeadline(0, prevD);
//						taskset.get(tn).selectOption(0, prevO);
//						taskset.get(tn).setPhase(prevPh);
//						taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
//					}
//				}
//			}
		}
		
	}

	public static void setBestPhase(TaskSet taskset)
	{
		int lcmOfTasks = 1;
		for (int tn = 0; tn < taskset.getTaskNum(); tn++)
			lcmOfTasks = lcm(lcmOfTasks, (int) (taskset.get(tn).getPeriod()));
		
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++)
		{
			int bestPhase = -1;
			double minDensity = Double.POSITIVE_INFINITY;
			for(int t = 0; t< lcmOfTasks ; t++)
			{
				taskset.get(tn).setPhase(t);
				if(minDensity > calculateTotalPeakDensity(taskset))
				{
					minDensity = calculateTotalPeakDensity(taskset);
					bestPhase = t;
				}
			}
			
			taskset.get(tn).setPhase(bestPhase);
			
		}
		taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
	}
	
	public static void setBestOption(TaskSet taskset)
	{
		double minDensity = Double.POSITIVE_INFINITY;
		
		for(int tn = 0; tn<taskset.getTaskNum() ; tn++)
		{
			for(int sn = 0; sn<taskset.get(tn).getNumSegments() ; sn++)
			{
				for(int o = 0 ; o < Param.NumThreads_MAX ; o++)
				{
					int prevO = taskset.get(tn).selectedOption(sn);
					double prevDensity = calculateTotalPeakDensity(taskset);
					
					taskset.setSelectOption(tn, sn, o);
					if(prevDensity > calculateTotalPeakDensity(taskset))
					{
//						taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
					}
					else
					{
						taskset.setSelectOption(tn, sn, prevO);
					}
				}
			}
		}
		taskset.setTotalPeakDensity(calculateTotalPeakDensity(taskset));
	}
	
	public static int getMaxIndex(TaskSet[] tasksetChromosome) 
	{
		double maxTotalPeakDensityTaskset = -1;
		int maxTotalPeakDensityTasksetIndex = -1;
		for (int pc = 0; pc < population_count; pc++) {
			if (maxTotalPeakDensityTaskset < tasksetChromosome[pc].getTotalPeakDensity()) 
			{
				maxTotalPeakDensityTaskset = tasksetChromosome[pc].getTotalPeakDensity();
				maxTotalPeakDensityTasksetIndex = pc;
			}
		}
		return maxTotalPeakDensityTasksetIndex;
	}
	
	public static int getMinIndex(TaskSet[] tasksetChromosome) 
	{
		double minTotalPeakDensityTaskset = Double.POSITIVE_INFINITY;
		int minTotalPeakDensityTasksetIndex = Integer.MAX_VALUE;
		for (int pc = 0; pc < population_count; pc++) {
			if (minTotalPeakDensityTaskset > tasksetChromosome[pc].getTotalPeakDensity()) 
			{
				minTotalPeakDensityTaskset = tasksetChromosome[pc].getTotalPeakDensity();
				minTotalPeakDensityTasksetIndex = pc;
			}
		}
		return minTotalPeakDensityTasksetIndex;
	}
	
	public static boolean gaInit_Initialize(TaskSet[] tasksetChromosome, Util util) {

		for (int pc = 0; pc < population_count; pc++) 
		{
			for (int tn = 0; tn < tasksetChromosome[pc].getTaskNum(); tn++) 
			{
				if(tasksetChromosome[pc].get(tn).getPeriod() == 0)
					return false;
			}
			
			// 1. taskset의 task의 segment별 intermediate deadline은 task의 deadline과 동일함
			
			// 2. taskset의 task마다 [0~period] random한 phase를 set
			for (int tn = 0; tn < tasksetChromosome[pc].getTaskNum(); tn++) 
			{
				int randomPhase = util.randomInt(0, tasksetChromosome[pc].get(tn).getPeriod());
				tasksetChromosome[pc].get(tn).setPhase(randomPhase);
			}

			// 3. taskset의 task의 segment마다 [0~option#] cmax가 intermdeidate deadline보다는 작지만 가장 긴 option을 select
			for (int tn = 0; tn < tasksetChromosome[pc].getTaskNum(); tn++) {
				for (int sn = 0; sn < tasksetChromosome[pc].get(tn).getNumSegments(); sn++) {
					int option_selected = -1;
					for(int on = 0; on <tasksetChromosome[pc].get(tn).getNumOptions() ; on++){
						
						if(tasksetChromosome[pc].get(tn).getMaxExecutionTimeOfSegment(sn, on) > tasksetChromosome[pc].get(tn).getIntermediateDeadline(sn))
							continue;
						else
						{
							option_selected = on;
							break;
						}
					}
					tasksetChromosome[pc].get(tn).selectOption(sn, option_selected);
				}
			}
			
		}
		
		return true;
	}

	public static void gaWriteTaskSetInformation(TaskSet ts) {

		try {
			////////////////////////////////////////////////////////////////
			BufferedWriter out = new BufferedWriter(new FileWriter("GAResult.txt", true));

			for (int i = 0; i < ts.getTaskNum(); i++) {
				// print period
				out.write(Integer.toString(ts.get(i).getPeriod()));
				out.write("\t");
				// print e
				for (int j = 0; j < ts.get(i).getNumSegments(); j++) {
					out.write(Integer.toString((ts.get(i).getTotalExecutionTime(j, ts.get(i).selectedOption(j)))));
					out.write(" ");
				}
				out.write("\t");
				// print deadline
				for (int j = 0; j < ts.get(i).getNumSegments(); j++) {
					out.write(Integer.toString(ts.get(i).getIntermediateDeadline(j)));
					out.write(" ");
				}
				out.write("\t");
				// print selected option
				for (int j = 0; j < ts.get(i).getNumSegments(); j++) {
					out.write(Integer.toString(ts.get(i).selectedOption(j)));
					out.write(" ");
				}
				out.write("\t");
				out.write(Integer.toString(ts.get(i).getPhase()));
				out.newLine();
			}
			if (ts.getTotalPeakDensity() <= Param.NumProcessors) {
				out.write("--True");
			} else {
				out.write("--False");
			}
			out.newLine();
			out.newLine();

			out.close();
		} catch (IOException e) {
			System.err.println(e); // 에러가 있다면 메시지 출력
			System.exit(1);
		}

	}

	public static void gaPrintTotalPeakDensity(TaskSet ts) {
		int i;

		System.out.println();
		System.out.println();

		int lcmOfTasks = 1;
		for (int tn = 0; tn < ts.getTaskNum(); tn++)
			lcmOfTasks = lcm(lcmOfTasks, (int) (ts.get(tn).getPeriod()));

		// System.out.println("lcmOfTasks2 " + lcmOfTasks);

		for (int tn = 0; tn < ts.getTaskNum(); tn++) {
			for (double t = 0; t < (double) lcmOfTasks; t = t + 1.0)
				System.out.print(ts.get(tn).getDensityAtT(t, ts.get(tn).getPhase()) + " ");
			System.out.println();
		}
	}

	public static void gaPrintPopulationTotalPeakDensity(TaskSet[] tasksetChromosome) {
		for (int i = 0; i < population_count; i++)
			System.out.printf("%.2f ", tasksetChromosome[i].getTotalPeakDensity());
		System.out.println();
	}

	public static int gaMinTotalPeakDensityIndex(TaskSet[] tasksetChromosome) {
		int min_index = -1;

		double min = Double.POSITIVE_INFINITY;
		for (int pc = 0; pc < population_count; pc++) {
			if (min > tasksetChromosome[pc].getTotalPeakDensity()) {
				min = tasksetChromosome[pc].getTotalPeakDensity();
				min_index = pc;
			}
		}
		return min_index;
	}

	public static double gaMinTotalPeakDensity(TaskSet[] tasksetChromosome) 
	{
		double min = Double.POSITIVE_INFINITY;
		for (int pc = 0; pc < population_count; pc++) {
			if (min > tasksetChromosome[pc].getTotalPeakDensity())
				min = tasksetChromosome[pc].getTotalPeakDensity();
		}
		return min;
	}

	public static double gaMaxTotalPeakDensity(TaskSet[] tasksetChromosome) 
	{
		double max = Double.NEGATIVE_INFINITY;
		for (int pc = 0; pc < population_count; pc++) {
			if (max < tasksetChromosome[pc].getTotalPeakDensity())
				max = tasksetChromosome[pc].getTotalPeakDensity();
		}
		return max;
	}

	public static void gaSelection(Util util) {
		while (true) {
			selectedIndices[0] = util.randomInt(0, population_count - 1);
			selectedIndices[1] = util.randomInt(0, population_count - 1);

			if (selectedIndices[0] != selectedIndices[1])
				break;
		}

	}

	public static void gaSelectionWorst(TaskSet[] tasksetChromosome) {
		Double fitnesses[] = new Double[population_count];
		Double fitness_function[] = new Double[population_count];
		for (int pc = 0; pc < population_count; pc++)
			fitnesses[pc] = calculateTotalPeakDensity(tasksetChromosome[pc]);
		double min = (double) Collections.min(Arrays.asList(fitnesses));
		double max = (double) Collections.max(Arrays.asList(fitnesses));

		int indexMax = -1;
		for (int pc = 0; pc < population_count; pc++) {
			if (fitnesses[pc] == max) {
				indexMax = pc;
				break;
			}
		}
		selectedIndices[0] = indexMax;
		selectedIndices[1] = (indexMax + 1) % population_count;

	}

	public static int gaRHSelection(Util util, TaskSet[] tasksetChromosome) {

		Double fi[] = new Double[population_count];
		double Cw = gaMaxTotalPeakDensity(tasksetChromosome);
		double Cb = gaMinTotalPeakDensity(tasksetChromosome);
		int k = 4;
		double sumOfFitnesses = 0;
		double point;
		double sum = 0;
		int index = -1;

		for (int pc = 0; pc < population_count; pc++)
			fi[pc] = (Cw - tasksetChromosome[pc].getTotalPeakDensity()) + (Cw - Cb) / (k - 1);

		for (int pc = 0; pc < population_count; pc++)
			sumOfFitnesses = sumOfFitnesses + fi[pc];

		point = (double) util.randomInt(0, (int) sumOfFitnesses);
		
		for (int pc = 0; pc < population_count; pc++) {
			sum = sum + fi[pc];
			if (point < sum) {
				index = pc;
				break;
			}
		}

		// TODO: 여기 고칠것
		if (index == -1) {
			// System.out.println("!!!!");
			return util.randomInt(0, population_count - 1);
		}
		return index;
	}

	public static TaskSet copy(TaskSet oldtaskset) {
		TaskSet newtaskset = new TaskSet(oldtaskset.getTaskSetID(), oldtaskset.getAlpha(), oldtaskset.getBeta(),
				oldtaskset.getGamma());
		newtaskset.setListTasks(oldtaskset.getListTasks());
		newtaskset.setTaskNum(oldtaskset.getTaskNum());
		newtaskset.setTotalPeakDensity(oldtaskset.getTotalPeakDensity());

		return newtaskset;
	}

	// TODO: Better method
	public static TaskSet gaCrossover(Util util, TaskSet[] tasksetChromosome) {

		TaskSet newTaskset = copy(tasksetChromosome[selectedIndices[0]]);

		int crossPoint = util.randomInt(0, newTaskset.getTaskNum() - 1);
		ArrayList<Task> tasklist = new ArrayList<Task>();

		for (int tn = 0; tn < crossPoint; tn++)
			tasklist.add(tasksetChromosome[selectedIndices[0]].get(tn).copyTask());

		for (int tn = crossPoint; tn < newTaskset.getTaskNum(); tn++)
			tasklist.add(tasksetChromosome[selectedIndices[1]].get(tn).copyTask());

		newTaskset.setListTasks(tasklist);
		return newTaskset;
	}

	public static void gaMutateTask(Util util, TaskSet mutation_taskset, boolean changeDeadline) 
	{	
		// 1. phase 는 random하게 
		for (int tn = 0; tn < mutation_taskset.getTaskNum(); tn++) {
			int phase = util.randomInt(0, mutation_taskset.get(tn).getPeriod() - 1);
			mutation_taskset.get(tn).setPhase(phase);
		}
		
		// 2. taskset의 task의 segment마다 [0~option#] cmax가 intermdeidate deadline보다는 작게 하는 option들 중 random하게
		for (int tn = 0; tn < mutation_taskset.getTaskNum(); tn++) {
			for (int sn = 0; sn < mutation_taskset.get(tn).getNumSegments(); sn++) {
				int min_option_available = -1;
				for(int on = 0; on <mutation_taskset.get(tn).getNumOptions() ; on++){
					
					if(mutation_taskset.get(tn).getMaxExecutionTimeOfSegment(sn, on) > mutation_taskset.get(tn).getIntermediateDeadline(sn))
						continue;
					else
					{
						min_option_available = on;
						break;
					}
				}
				int random_option = util.randomInt(min_option_available, mutation_taskset.get(tn).getNumOptions()-1);
				mutation_taskset.get(tn).selectOption(sn, random_option);
			}
		}
			
		// 3. deadline은 2에서 select된 option의 cmax보다는 크고 task의 deadline보다는 작은 수 들 중 random하게
		// segment 개수는 1개라고 fix하고 구현함
		if (changeDeadline == true) {
			for (int tn = 0; tn < mutation_taskset.getTaskNum(); tn++) {
				int random_deadline = -1;
				random_deadline = util.randomInt(mutation_taskset.get(tn).getMaxExecutionTimeOfSegment(0), mutation_taskset.get(tn).getDeadline()); 
				mutation_taskset.get(tn).setIntermediateDeadline(0, random_deadline);
			}
		}	
	}

	public static void gaReplacement(TaskSet newtaskset, TaskSet[] tasksetChromosome) {
		double maxTotalPeakDensityTaskset = Double.NEGATIVE_INFINITY;
		int maxTotalPeakDensityTasksetIndex = 0;

		for (int pc = 0; pc < population_count; pc++) {
			if (maxTotalPeakDensityTaskset < tasksetChromosome[pc].getTotalPeakDensity()) {
				maxTotalPeakDensityTaskset = tasksetChromosome[pc].getTotalPeakDensity();
				maxTotalPeakDensityTasksetIndex = pc;
			}
		}

		try 
		{
			tasksetChromosome[maxTotalPeakDensityTasksetIndex] = (TaskSet) newtaskset.clone();

			ArrayList<Task> tasklist = new ArrayList<Task>();
			for (int tn = 0; tn < tasksetChromosome[maxTotalPeakDensityTasksetIndex].getTaskNum(); tn++)
				tasklist.add((Task) newtaskset.get(tn).clone());

			tasksetChromosome[maxTotalPeakDensityTasksetIndex].setListTasks(tasklist);
		} 
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public static double calculateTotalPeakDensity(TaskSet tasksetCalc) {
		
//		int lcmOfTasks = 1;
//		for (int tn = 0; tn < tasksetCalc.getTaskNum(); tn++)
//			lcmOfTasks = lcm(lcmOfTasks, (int) (tasksetCalc.get(tn).getPeriod()));
//
//		for (int tn = 0; tn < tasksetCalc.getTaskNum(); tn++)
//		{	
//			System.out.printf("%d ", tasksetCalc.get(tn).getPeriod());
//		}
//		System.out.println();
//	
////		System.out.println(lcmOfTasks);
//		
//		double totalPeakDensity = Double.NEGATIVE_INFINITY;
//		
//		for (int i = 0; i < tasksetCalc.size(); i++)
//		{
//			int period = tasksetCalc.get(i).getPeriod();
//			for (int j = 0; j < lcmOfTasks / period; j++)
//			{
//				double t = j * period;
//				double sum = 0.0;
//				for (int tn = 0; tn < tasksetCalc.getTaskNum(); tn ++)
//					sum += tasksetCalc.get(tn).getDensityAtT(t, tasksetCalc.get(tn).getPhase());
//				if (sum > totalPeakDensity)
//					totalPeakDensity = sum;
//			}
//		}
//		
////		System.out.printf("%.2f\n", totalPeakDensity);
//		return totalPeakDensity;


		
		long lcmOfTasks = 1;
		lcmOfTasks = Util.getTaskSetLCM(tasksetCalc);
//		for (int tn = 0; tn < tasksetCalc.getTaskNum(); tn++)
//		{	
//			System.out.printf("%d ", tasksetCalc.get(tn).getPeriod());
//		}
//		System.out.println();
//		
//		System.out.println(lcmOfTasks);
		
		double totalPeakDensity = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < tasksetCalc.size(); i++)
		{
			long period = (long)tasksetCalc.get(i).getPeriod();
			for (long j = 0; j < lcmOfTasks / period; j++)
			{
				double t = (double)(j * period);
				double sum = 0.0;
				for (int tn = 0; tn < tasksetCalc.getTaskNum(); tn ++)
				{
					sum += tasksetCalc.get(tn).getDensityAtT(t, tasksetCalc.get(tn).getPhase());
				}
				if (sum > totalPeakDensity)
					totalPeakDensity = sum;
			}
		}
		
//		System.out.printf("%.2f\n", totalPeakDensity);
		return totalPeakDensity;
		
		
	}

	public static int lcm(int a, int b) {
		int max, min, x;
		int lcm = 1;

		if (a > b) {
			max = a;
			min = b;
		} else {
			max = b;
			min = a;
		}
		for (int i = 1; i <= min; i++) {
			x = max * i; // finding multiples of the maximum number
			if (x % min == 0) // Finding the multiple of maximum number which is
								// divisible by the minimum number.
			{
				lcm = x; // making the 1st multiple of maximum number as lcm,
							// which is divisible by the minimum number
				break; // exiting from the loop, as we don’t need anymore
						// checking after getting the LCM
			}
		}
		return lcm;
	}

	public static ArrayList<TaskSet> readFileTaskset(String filename, int taskset_count) throws IOException {
		boolean isLastTaskset = false;
		int taskset_taskcount[] = new int[taskset_count];
		for (int i = 0; i < taskset_count; i++)
			taskset_taskcount[i] = 0;

		ArrayList<TaskSet> taskpool = new ArrayList<>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String s;
			while ((s = in.readLine()) != null) {
				StringTokenizer s_tk = new StringTokenizer(s, "\n");
				String each_line;
				while (s_tk.hasMoreElements()) {
					each_line = (String) s_tk.nextElement();
					StringTokenizer each_line_tk = new StringTokenizer(each_line);
					int nth = 0;
					while (each_line_tk.hasMoreElements()) {
						String each_word = (String) each_line_tk.nextElement();

						if (nth == 0) {
							int taskset_no = Integer.parseInt(each_word);
							taskset_taskcount[taskset_no - 1]++;
						}
						nth++;
					}
				}
			}

			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < taskset_count; i++) {
			TaskGenerator generator_multi_segment = new TaskGenerator();
			generator_multi_segment.setFixedTaskNum(taskset_taskcount[i]);
			generator_multi_segment.setFixedAlpha(0.0);
			generator_multi_segment.setFixedBeta(0.0);
			generator_multi_segment.setFixedGamma(0.0);
			taskpool.add(generator_multi_segment.GenerateTaskSet(i, i));
		}

		int taskset_index = 0;
		isLastTaskset = false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String s;
			while ((s = in.readLine()) != null) {
				StringTokenizer s_tk = new StringTokenizer(s, "\n");
				String each_line;
				while (s_tk.hasMoreElements()) {
					each_line = (String) s_tk.nextElement();
					StringTokenizer each_line_tk = new StringTokenizer(each_line);
					int nth = 0;
					int taskset_no = -1;
					int task_no = -1;
					while (each_line_tk.hasMoreElements()) {
						String each_word = (String) each_line_tk.nextElement();

						if (nth == 0) {
							// System.out.println(taskset_no + " " +
							// taskset_count);
							taskset_no = Integer.parseInt(each_word);
							// if(taskset_no == taskset_count)
							// {
							// isLastTaskset = true;
							// break;
							// }
						}
						if (nth == 1)
							task_no = Integer.parseInt(each_word);

						// Period
						if (nth == 2) {
							double temp_period = Double.parseDouble(each_word);
							taskpool.get(taskset_no - 1).get(task_no - 1).setPeriod((int) temp_period);
							// System.out.println((int)temp_period);
						}
						// Execution time
						if (nth == 3) {
							double temp_executiontime = Double.parseDouble(each_word);
							taskpool.get(taskset_no - 1).get(task_no - 1).setExecutionTime(0, 0, 0,
									(int) temp_executiontime);
							// System.out.println((int)temp_executiontime);
						}
						// Deadline
						if (nth == 4) {
							double temp_deadline = Double.parseDouble(each_word);
							taskpool.get(taskset_no - 1).get(task_no - 1).setDeadline((int) temp_deadline);
							taskpool.get(taskset_no - 1).get(task_no - 1).setIntermediateDeadline(0,
									(int) temp_deadline);
							// System.out.println((int)temp_deadline);
						}

						nth++;
					}
					// if(isLastTaskset)
					// {
					// break;
					// }
				}
				// if(isLastTaskset)
				// {
				// break;
				// }
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return taskpool;

	}

	public static boolean hasSchedulable(TaskSet[] tasksetChromosome) 
	{
		for (int p = 0; p < population_count; p++) {
			if (tasksetChromosome[p].getTotalPeakDensity() <= Param.NumProcessors)
				return true;
		}
		return false;
	}

}
