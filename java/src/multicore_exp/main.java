package multicore_exp;

import generator.TaskGenerator;
import generator.TaskGeneratorSingleSegment;
import generator.TaskGeneratorSporadic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import phase_shifting.ExhaustiveSearchGA;
import data.SporadicTask;
import data.TaskSet;
import logic.*;
import logic.NBG.Type;

public class main {


	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//rtas();				
		//shin();		
		//gedfComparePOptions();
		//gedfCompareMetric();
		//gedfSample();
		//gedfSample2();
		starlab2015();
	}
	
	public static void starlab2015()
	{
		int iterations = 100;

		// Generate Task
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setFixedAlpha(0.0);
		taskGenerator.setRandomBeta(0.0, 15);
		taskGenerator.setFixedGamma(1.0);
		taskGenerator.setFixedTaskNum(50);
		int seed = 0;

				
		/*
		System.out.println("RTAS alpha experiment");
		System.out.println(String.format("alpha\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
		for (double i = 0; i <= 1.0; i += 0.2)
		{
			taskGenerator.setFixedAlpha(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet taskSet = taskGenerator.GenerateTaskSet(j,  j);
				//taskGenerator.WriteToFile(taskSet);
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
				
				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;
			
			
			//System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
			//		i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));
			
		}
		 */

//		taskGenerator.setRandomAlpha(0, 1);
//		System.out.println("\nRTAS beta experiment");		
//		System.out.println(String.format("beta\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
//		
//		Param.DebugMessage = false;
//		//double i = 2.0;
//		for (double i = 0; i <= 3; i += 0.1)
//		{
//			taskGenerator.setFixedBeta(i);
//			double nSchedulable = 0;
//			double nSchedulable_single = 0;
//			double nSchedulable_random = 0;
//			double nSchedulable_max = 0;
//			double nSchedulable_shin = 0;		
//			double nSchedulable_shin_max = 0;
//			double nSchedulable_shin_min = 0;
//	
//			
//			for (int j = 0; j < iterations; j++)
//			{
//				//System.out.println("Iteration " + j);
//
//				TaskSet taskSet = taskGenerator.GenerateTaskSet(j, seed++);
//				//TaskSet taskSet = taskGenerator.GenerateTaskSetWithInteger(j,  seed++);
//				//TaskSet taskSet = SHIN.SampleTaskGenerate2();
//				
//				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
//				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
//				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
//				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
//				
//				//nSchedulable += NBG.getNumSchedulableTasks(taskSet, NBG.Type.CUSTOM);
//
//				
//				SHIN.SelectOptionRandom(taskSet);
//				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
//				SHIN.SelectOptionMax(taskSet);
//				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
//				SHIN.SelectOptionSingle(taskSet);
//				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
//			}
//			nSchedulable /= iterations;
//			nSchedulable_single /= iterations;
//			nSchedulable_random /= iterations;
//			nSchedulable_max /= iterations;
//			nSchedulable_shin /= iterations;		
//			nSchedulable_shin_max /= iterations;
//			nSchedulable_shin_min /= iterations;	
//			
//			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
//					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));
//			
//		}
//

		System.out.println("\nRTAS gamma experiment");		
		System.out.println(String.format("gamma\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));

		taskGenerator.setRandomAlpha(0, 0.7);
		taskGenerator.setRandomBeta(0.1, 1.3);
		taskGenerator.setRandomTaskNum(3,3);
		taskGenerator.setRandomGamma(0.1,0.2);
		
		Param.scmin = 10;
		Param.scmin = 15;
		iterations = 1000;
		Param.NumProcessors = 2;
		Param.NumThreads_MAX = 2;
		
		
		int pop_count = 3;
		int gen_count = 1000;
		ExhaustiveSearchGA ga = new ExhaustiveSearchGA(pop_count, gen_count);
		Util util = new Util(0);
		Util util_ga = new Util(1);
		
		Param.DebugMessage = false;
		//double i = 2.0;
//		for (double i = 0.1; i <= 1.0; i += 0.1)
		{
			double i = 0.1;
//			taskGenerator.setFixedGamma(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;		
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
			double nSchedulable_GA = 0;
	
			
			for (int j = 0; j < iterations; j++)
			{
				System.out.println("Iteration " + j);

				TaskSet taskSet = taskGenerator.GenerateTaskSet(j, j);
				//TaskSet taskSet = taskGenerator.GenerateTaskSetWithInteger(j,  seed++);
				//TaskSet taskSet = SHIN.SampleTaskGenerate2();
				
				nSchedulable += RTAS.getPeakDensity(taskSet);
				nSchedulable_single += NBG.getPeakDensity(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getPeakDensity(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getPeakDensity(taskSet, NBG.Type.MAX);
				nSchedulable_GA += ga.getPeakDensity(taskGenerator, j, util, util_ga, true);
				
				//nSchedulable += NBG.getNumSchedulableTasks(taskSet, NBG.Type.CUSTOM);

				
				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;		
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;	
			nSchedulable_GA /= iterations;
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, 
					nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min, nSchedulable_GA));
			
		}
		
		
	}
	
	
	public static void gedfSample2()
	{	
		SporadicTask t1 = new SporadicTask(6, 1, 3);
		SporadicTask t2 = new SporadicTask(2, 1, 1);

		ArrayList<SporadicTask> sporadicSet = new ArrayList<>();
		sporadicSet.add(t1);
		sporadicSet.add(t2);

		System.out.println("Fluid\t" + Fluid.isSchedulable(sporadicSet));		
		System.out.println("GEDF.s\t" + EDF_simulation.isSchedulable(sporadicSet));		
		System.out.println("GFB\t" + GFB.isSchedulable(sporadicSet));		
		System.out.println("BAK\t" + BAK.isSchedulable(sporadicSet));
		System.out.println("BCL\t" + BCL.isSchedulable(sporadicSet));
		System.out.println("RTA\t" + RTA.isSchedulable(sporadicSet));
	}
	public static void gedfSample()
	{
		//TaskSet taskSet = SHIN.SampleTaskGenerate3();
		//System.out.println(SHIN.getNumSchedulableTasks(taskSet));
		//System.out.println(NBG.getNumSchedulableTasks(taskSet, Type.CUSTOM));
		
		
		// Representative sample tasks
		SporadicTask t1 = new SporadicTask(4, 2, 3);
		SporadicTask t2 = new SporadicTask(4, 2, 3);
		SporadicTask t3 = new SporadicTask(4, 2, 4);
		SporadicTask t4 = new SporadicTask(4, 2, 4);

		ArrayList<SporadicTask> sporadicSet = new ArrayList<>();
		sporadicSet.add(t1);
		sporadicSet.add(t2);
		sporadicSet.add(t3);
		sporadicSet.add(t4);

		System.out.println("Fluid\t" + Fluid.isSchedulable(sporadicSet));		
		System.out.println("GEDF.s\t" + EDF_simulation.isSchedulable(sporadicSet));		
		System.out.println("GFB\t" + GFB.isSchedulable(sporadicSet));		
		System.out.println("BAK\t" + BAK.isSchedulable(sporadicSet));
		System.out.println("BCL\t" + BCL.isSchedulable(sporadicSet));
		System.out.println("RTA\t" + RTA.isSchedulable(sporadicSet));
		
		/*
		SporadicTask t1 = new SporadicTask(10, 1, 1);
		SporadicTask t2 = new SporadicTask(4, 1, 1);
		SporadicTask t3 = new SporadicTask(10, 1, 1);
		SporadicTask t4 = new SporadicTask(4, 1, 1);

		ArrayList<SporadicTask> sporadicSet = new ArrayList<>();
		sporadicSet.add(t1);
		sporadicSet.add(t2);
		sporadicSet.add(t3);
		sporadicSet.add(t4);
		
		
		System.out.println("Fluid\t" + Fluid.getNumSchedulableTasks(sporadicSet));		
		System.out.println("GEDF.s\t" + EDF_simulation.getNumSchedulableTasks(sporadicSet));		
		*/
	}
	
	public static void gedfComparePOptions()
	{
		System.out.println("\nGamma experiment");		
		System.out.println(String.format("gamma\tFluid\tGEDF.S\tGFB\tBAK\tBCL\tRTA\tS.S\tS.M\tS.R"));

		int iterations = 100;
		int usefulSet = 100;
		int seed = 0;

	    Param.NumProcessors = 2;
	    Param.NumThreads_MAX = 2;
	    TaskGeneratorSingleSegment generator = new TaskGeneratorSingleSegment();
	    generator.setRandomBeta(0.0, 1.0);
	    generator.setRandomAlpha(0, 1);
	    generator.setRandomTaskNum(3,10);
		
		for (double i = 0.1; i <= 1.0; i += 0.1)
		{
			String filename = String.format("gamma_%.2f.txt", i);
			File f = new File(filename);
			f.delete();
			
			WriteFileHeader(filename, usefulSet);
			
			generator.setFixedGamma(i);
			double nSchedulable_fluid = 0;
			double nSchedulable_gedfs = 0;
			double nSchedulable_gfb = 0;
			double nSchedulable_bak = 0;		
			double nSchedulable_bcl = 0;				
			double nSchedulable_rta = 0;
			double nSchedulable_s_min = 0;
			double nSchedulable_s_rnd = 0;
			double nSchedulable_s_max = 0;
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet randomTasks = generator.GenerateTaskSetWithLimitedLCM(j, j, 10000);
	            ArrayList<SporadicTask> taskSet = randomTasks.toSporadicTask(j, TaskSet.ParallelizedOption.SINGLE);
				
				if (j >= usefulSet)
					continue;
				
				if (Fluid.isSchedulable(taskSet))
				{
					nSchedulable_fluid++;
					for (SporadicTask t : taskSet)
						t.schedulableFluid = true;
				}
				
				if (EDFEventSimulation.isSchedulable(taskSet))
				{
					nSchedulable_gedfs++;
					for (SporadicTask t : taskSet)
						t.schedulableGEDF =true;
				}
				
				if (GFB.isSchedulable(taskSet))
				{
					nSchedulable_gfb++;
					for (SporadicTask t : taskSet)
						t.schedulableGFB =true;
				}
				
				if (BAK.isSchedulable(taskSet))
				{
					nSchedulable_bak++;
					for (SporadicTask t : taskSet)
						t.schedulableBAK =true;
				}
				if (BCL.isSchedulable(taskSet))
				{
					nSchedulable_bcl++;
					for (SporadicTask t : taskSet)
						t.schedulableBCL =true;
				}
				if (RTA.isSchedulable3(taskSet))
				{
					nSchedulable_rta++;
					for (SporadicTask t : taskSet)
						t.schedulableRTA =true;
				}
				SHIN.SelectOptionSingle(randomTasks);
				if (SHIN.isSchedulable(randomTasks))
				{
					nSchedulable_s_min++;
				}
				SHIN.SelectOptionMax(randomTasks);
				if (SHIN.isSchedulable(randomTasks))
				{
					nSchedulable_s_max++;
				}
				SHIN.SelectOptionRandom(randomTasks);
				if (SHIN.isSchedulable(randomTasks))
				{
					nSchedulable_s_rnd++;
				}
				
				WriteSporadicTasks(filename, taskSet);
			}
			nSchedulable_fluid /= usefulSet;
			nSchedulable_gedfs /= usefulSet;
			nSchedulable_gfb /= usefulSet;
			nSchedulable_bak /= usefulSet;		
			nSchedulable_bcl /= usefulSet;
			nSchedulable_rta /= usefulSet;
			nSchedulable_s_min /= usefulSet;
			nSchedulable_s_max /= usefulSet;
			nSchedulable_s_rnd /= usefulSet;
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable_fluid, 
					nSchedulable_gedfs, 
					nSchedulable_gfb, 
					nSchedulable_bak, 
					nSchedulable_bcl, 
					nSchedulable_rta,
					nSchedulable_s_min,
					nSchedulable_s_max,
					nSchedulable_s_rnd));			
		}		
	}
		
	public static void gedfCompareMetric()
	{
		System.out.println("\nGamma experiment");		
		System.out.println(String.format("gamma\tFluid\tGEDF.S\tGFB\tBAK\tBCL\tRTA"));

		int iterations = 100;
		int usefulSet = 100;
		int seed = 0;

		TaskGeneratorSporadic generator = new TaskGeneratorSporadic();
		generator.setFixedTaskNum(10);
		generator.setRandomAlpha(0.0, 1.0);
		generator.setRandomBeta(0.0, 1.0);
		
		for (double i = 0.1; i <= 1.0; i += 0.1)
		{
			String filename = String.format("gamma_%.2f.txt", i);
			File f = new File(filename);
			f.delete();
			
			WriteFileHeader(filename, usefulSet);
			
			generator.setFixedGamma(i);
			double nSchedulable_fluid = 0;
			double nSchedulable_gedfs = 0;
			double nSchedulable_gfb = 0;
			double nSchedulable_bak = 0;		
			double nSchedulable_bcl = 0;									
			double nSchedulable_rta = 0;
			
			for (int j = 0; j < iterations; j++)
			{
				ArrayList<SporadicTask> taskSet = generator.RandomGenerateSporadic(j, seed++);
				
				if (j >= usefulSet)
					continue;
				
				if (Fluid.isSchedulable(taskSet))
				{
					nSchedulable_fluid++;
					for (SporadicTask t : taskSet)
						t.schedulableFluid = true;
				}
				
				if (EDF_simulation.isSchedulable(taskSet))
				{
					nSchedulable_gedfs++;
					for (SporadicTask t : taskSet)
						t.schedulableGEDF =true;
				}
				
				if (GFB.isSchedulable(taskSet))
				{
					nSchedulable_gfb++;
					for (SporadicTask t : taskSet)
						t.schedulableGFB =true;
				}
				
				if (BAK.isSchedulable(taskSet))
				{
					nSchedulable_bak++;
					for (SporadicTask t : taskSet)
						t.schedulableBAK =true;
				}
				if (BCL.isSchedulable(taskSet))
				{
					nSchedulable_bcl++;
					for (SporadicTask t : taskSet)
						t.schedulableBCL =true;
				}
				if (RTA.isSchedulable3(taskSet))
				{
					nSchedulable_rta++;
					for (SporadicTask t : taskSet)
						t.schedulableRTA =true;
				}

				
				WriteSporadicTasks(filename, taskSet);
			}
			nSchedulable_fluid /= usefulSet;
			nSchedulable_gedfs /= usefulSet;
			nSchedulable_gfb /= usefulSet;
			nSchedulable_bak /= usefulSet;		
			nSchedulable_bcl /= usefulSet;
			nSchedulable_rta /= usefulSet;
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable_fluid, 
					nSchedulable_gedfs, 
					nSchedulable_gfb, 
					nSchedulable_bak, 
					nSchedulable_bcl, 
					nSchedulable_rta));			
		}		
	}
	
	public static void gedfCompare()
	{
		System.out.println("\nGamma experiment");		
		System.out.println(String.format("gamma\tFluid\tGEDF.S\tGFB\tBAK\tBCL"));

		int iterations = 100;
		int seed = 0;

		TaskGeneratorSporadic generator = new TaskGeneratorSporadic();
		generator.setFixedTaskNum(10);
		generator.setRandomAlpha(0.0, 1.0);
		generator.setRandomBeta(0.0, 1.0);
		//generator.setFixedAlpha(1.0);
		//generator.setFixedBeta(1.0);
		
		for (double i = 0.1; i <= 1; i += 0.1)
		{
			String filename = String.format("gamma_%.2f.txt", i);
			File f = new File(filename);
			f.delete();
			
			generator.setFixedGamma(i);
			double nSchedulable_fluid = 0;
			double nSchedulable_gedfs = 0;
			double nSchedulable_gfb = 0;
			double nSchedulable_bak = 0;		
			double nSchedulable_bcl = 0;	
			
			for (int j = 0; j < iterations; j++)
			{
				//System.out.println("Iteration " + j);

				ArrayList<SporadicTask> taskSet = generator.RandomGenerateSporadic(j, seed++);
				nSchedulable_fluid += Fluid.getNumSchedulableTasks(taskSet);
				nSchedulable_gedfs += EDF_simulation.getNumSchedulableTasks(taskSet);
				nSchedulable_gfb += GFB.getNumSchedulableTasks(taskSet);
				nSchedulable_bak += BAK.getNumSchedulableTasks(taskSet);
				nSchedulable_bcl += BCL.getNumSchedulableTasks(taskSet);				

				WriteSporadicTasks(filename, taskSet);
			}
			nSchedulable_fluid /= iterations;
			nSchedulable_gedfs /= iterations;
			nSchedulable_gfb /= iterations;
			nSchedulable_bak /= iterations;		
			nSchedulable_bcl /= iterations;
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable_fluid, nSchedulable_gedfs, nSchedulable_gfb, nSchedulable_bak, nSchedulable_bcl));
			
		}		
	}

	public static void WriteFileHeader(String filename, int iterations)
	{		
		File f = new File(filename);
		
		try {						
			BufferedWriter bw  = new BufferedWriter(new FileWriter(f, true));
			
			bw.write(String.format("# number of task sets = %d", iterations));
			bw.newLine();
			bw.write(String.format("#set\t#task\tp\te\td\tFluid\tGEDF.S\tGFB\tBAK\tBCL\tBCL2\tRTA"));
			bw.newLine();			
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void WriteSporadicTasks(String filename, ArrayList<SporadicTask> taskSet)
	{		
		File f = new File(filename);
		
		try {						
			BufferedWriter bw  = new BufferedWriter(new FileWriter(f, true));
						
			for (int i = 0; i < taskSet.size(); i++) 
			{
				bw.write(taskSet.get(i).toString());
				bw.newLine();
			}
			
			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void shin()
	{

		System.out.println(String.format("P.R.") + "\tGFB\tBAK\tBCL\tSHIN\tNBG");
		
		for (double i = 0.1; i<Param.NumProcessors; i += 0.1)
		{
			TaskSetPool pool = new TaskSetPool();
			pool.RandomGenerateSHIN(0.2, i);
			
			int outSHIN = 0;
			int outNBG = 0;
			int outGFB = 0;
			int outBAK = 0;
			int outBCL = 0;
			
			for(int j=0; j<pool.listTaskSet.size(); j++)
			{
				if (SHIN.isSchedulable(pool.listTaskSet.get(j)))
					outSHIN++;
								
				if(NBG.isSchedulable(pool.listTaskSet.get(j), Type.CUSTOM))
					outNBG++;

				ArrayList<SporadicTask> transformedTaskSet = TaskSetPool.TransformThreadsToTasks(pool.listTaskSet.get(j));
				
				if(GFB.isSchedulable(transformedTaskSet))
					outGFB++;

				if(BAK.isSchedulable(transformedTaskSet))
					outBAK++;
				
				if(BCL.isSchedulable(transformedTaskSet))
					outBCL++;
			}
			System.out.println(String.format("%.1f", i) + "\t" + outGFB + "\t" + outBAK + "\t" + outBCL+ "\t" + outSHIN + "\t" + outNBG);
		}
	}
	
	public static void rtas()
	{
		int iterations = 1000;

		// Generate Task
		TaskGenerator taskGenerator = new TaskGenerator();
		taskGenerator.setFixedAlpha(0.0);
		taskGenerator.setRandomBeta(0.0, 15);
		taskGenerator.setFixedGamma(1.0);
		taskGenerator.setFixedTaskNum(50);
		int seed = 0;
				
		/*
		System.out.println("RTAS alpha experiment");
		System.out.println(String.format("alpha\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
		for (double i = 0; i <= 1.0; i += 0.2)
		{
			taskGenerator.setFixedAlpha(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
			
			for (int j = 0; j < iterations; j++)
			{
				TaskSet taskSet = taskGenerator.GenerateTaskSet(j,  j);
				//taskGenerator.WriteToFile(taskSet);
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
				
				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;
			
			
			//System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
			//		i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));
			
		}
		 */

		taskGenerator.setRandomAlpha(0, 1);
		System.out.println("\nRTAS beta experiment");		
		System.out.println(String.format("beta\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));
		
		Param.DebugMessage = false;
		//double i = 2.0;
		for (double i = 0; i <= 3; i += 0.1)
		{
			taskGenerator.setFixedBeta(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;		
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
	
			
			for (int j = 0; j < iterations; j++)
			{
				//System.out.println("Iteration " + j);

				TaskSet taskSet = taskGenerator.GenerateTaskSet(j, seed++);
				//TaskSet taskSet = taskGenerator.GenerateTaskSetWithInteger(j,  seed++);
				//TaskSet taskSet = SHIN.SampleTaskGenerate2();
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
				
				//nSchedulable += NBG.getNumSchedulableTasks(taskSet, NBG.Type.CUSTOM);

				
				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;		
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;	
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));
			
		}


		System.out.println("\nRTAS gamma experiment");		
		System.out.println(String.format("gamma\tours\tN_S\tN_R\tN_M\tS\tS_M\tS_S"));

		taskGenerator.setRandomAlpha(0, 1);
		taskGenerator.setRandomBeta(0.0, 15);
		
		Param.DebugMessage = false;
		//double i = 2.0;
		for (double i = 0; i <= 1.5; i += 0.1)
		{
			taskGenerator.setFixedGamma(i);
			double nSchedulable = 0;
			double nSchedulable_single = 0;
			double nSchedulable_random = 0;
			double nSchedulable_max = 0;
			double nSchedulable_shin = 0;		
			double nSchedulable_shin_max = 0;
			double nSchedulable_shin_min = 0;
	
			
			for (int j = 0; j < iterations; j++)
			{
				//System.out.println("Iteration " + j);

				TaskSet taskSet = taskGenerator.GenerateTaskSet(j, seed++);
				//TaskSet taskSet = taskGenerator.GenerateTaskSetWithInteger(j,  seed++);
				//TaskSet taskSet = SHIN.SampleTaskGenerate2();
				
				nSchedulable += RTAS.getNumSchedulableTasks(taskSet);
				nSchedulable_single += NBG.getNumSchedulableTasks(taskSet, NBG.Type.SINGLE);
				nSchedulable_random += NBG.getNumSchedulableTasks(taskSet, NBG.Type.RANDOM);
				nSchedulable_max += NBG.getNumSchedulableTasks(taskSet, NBG.Type.MAX);
				
				//nSchedulable += NBG.getNumSchedulableTasks(taskSet, NBG.Type.CUSTOM);

				
				SHIN.SelectOptionRandom(taskSet);
				nSchedulable_shin += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionMax(taskSet);
				nSchedulable_shin_max += SHIN.getNumSchedulableTasks(taskSet);
				SHIN.SelectOptionSingle(taskSet);
				nSchedulable_shin_min += SHIN.getNumSchedulableTasks(taskSet);
			}
			nSchedulable /= iterations;
			nSchedulable_single /= iterations;
			nSchedulable_random /= iterations;
			nSchedulable_max /= iterations;
			nSchedulable_shin /= iterations;		
			nSchedulable_shin_max /= iterations;
			nSchedulable_shin_min /= iterations;	
			
			System.out.println(String.format("%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f", 
					i, nSchedulable, nSchedulable_single, nSchedulable_random, nSchedulable_max, nSchedulable_shin, nSchedulable_shin_max, nSchedulable_shin_min));
			
		}
		
		
	}


}
