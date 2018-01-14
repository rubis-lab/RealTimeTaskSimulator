package tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumSet;

import multicore_exp.Param;
import multicore_exp.PhaseShiftTest.Option;
import data.Task;
import data.TaskSet;

public class TimeDensityDistribution {
	public class TimeDensityPair
	{
		protected double timeRatio;
		protected double timeRatio2;
		protected double maxTimeRatio;
		protected double densityRatio;
		public boolean schedulable;
		public boolean draw;
		public String toString()
		{
			return String.format("%5.3f(%5.3f) - %(5.3f)\n", 
					timeRatio, timeRatio2, densityRatio);
		}
	}
	public ArrayList<TimeDensityPair> pairs;
	protected EnumSet<Option> options;
	public TimeDensityDistribution(EnumSet<Option> options)
	{
		this.options = options;
		pairs = new ArrayList<TimeDensityPair>();
	}
	protected boolean checkTaskOption(TaskSet taskset)
	{
		for (int i = 0; i < taskset.size(); i++)
		{
			try{
				taskset.get(i).selectedOption(0);
			}catch(Exception e)
			{
				return false;
			}
		}
		return true;
	}
	
	public void addResult(TaskSet taskset, double peakDensity, boolean schedulable)
	{
		if (!checkTaskOption(taskset))
			setTaskOption(taskset);
		TimeDensityPair pair = new TimeDensityPair();
		double densitySum = 0;
		double timeRatioSum = 0;
		double timeRatioSum2 = 0;
		double maxTimeRatio = 0;
		for (int i = 0; i < taskset.size(); i++)
		{
			Task task = taskset.get(i);
			int maxExecutionTime = task.getExecutionTimeOfThread(0, 0);
			int totalExecutionTime = task.getTotalExecutionTime(0);
			int deadline = task.getRealDeadline();
			int artificialDeadline = task.getIntermediateDeadline(0);
			
			double timeRatio = maxExecutionTime / (double)deadline;
			densitySum += totalExecutionTime / (double)deadline;
			timeRatioSum += timeRatio;
			timeRatioSum2 += maxExecutionTime / (double)artificialDeadline;
			if (maxTimeRatio < timeRatio)
				maxTimeRatio = timeRatio;
		}
		if (peakDensity > 0)
			pair.densityRatio = peakDensity / Param.NumProcessors;
		else
			pair.densityRatio = densitySum / Param.NumProcessors;

		pair.timeRatio = timeRatioSum / taskset.size();
		pair.timeRatio2 = timeRatioSum2 / taskset.size();
		pair.maxTimeRatio = maxTimeRatio;
		pair.schedulable = schedulable;
		pair.draw = true;
		
		pairs.add(pair);
		System.out.printf("%s %s\n",options.toString(), pair.toString()); 
	}
	
	protected void setTaskOption(TaskSet taskset)
	{
		if (options.size() == 0 || options.contains(Option.SINGLE))
		{
			for (int i = 0; i < taskset.size(); i++)
				taskset.get(i).selectOption(0, 0);
			return;
		}
		if (options.contains(Option.MAXPAL))
		{
			for (int i = 0; i < taskset.size(); i++)
				taskset.get(i).selectOption(0, taskset.get(i).getNumOptions() - 1); 
			return;
		}
		if (options.contains(Option.DC_SP) || options.contains(Option.DC_H4))
		{
			for (int i = 0; i < taskset.size(); i++)
			{
				Task task = taskset.get(i);
				int oMax = task.getNumOptions();
				task.selectOption(0, oMax - 1);
				for (int j = 0; j < oMax; j++)
				{
					if (task.getExecutionTimeOfThread(0, j, 0) < task.getDeadline())
					{
						task.selectOption(0, j);
						break;
					}
				}
			}
			return;
		}
	}

	public void draw(String path) {
		String filename = path + "/dist" + (options.toString().replace("[","_").replace("]",""));
		String dataname = filename + ".txt";
		String scriptname = filename + ".plt";
		String outputname = filename + ".eps";
		
		String script ="";
		script += "set term post eps solid enhanced monochrome font 'Times-roman, 20' size 3,3;\n";
		script += "set xlabel 'Time bound ratio';\n";
		script += "set ylabel 'Density bound ratio';\n";
		script += "set xrange [0:4];\n";
		script += "set yrange [0:4];\n";
		script += "set xtics 0,1,1\n";
		script += "set ytics 0,1,1\n";
		script += "set arrow from 1,0 to 1,1 nohead\n";
		script += "set arrow from 0,1 to 1,1 nohead\n";
		script += "set output '" + outputname + "'\n";
		script += "plot '" + dataname + "' u 3:4 w p not\n";

		if (options.contains(Option.DC_H4))
		{
			script += "set output '" + filename + "2.eps'\n";
			script += "plot '" + dataname + "' u 2:3 w p not\n";
			
		}

		PrintWriter writer;
		try {
			writer = new PrintWriter(dataname);
			for (int i = 0; i < pairs.size(); i++)
			{				
				if (!pairs.get(i).draw) continue;
				writer.printf("%f\t%f\t%f\t%f\n", 
						pairs.get(i).timeRatio,
						pairs.get(i).timeRatio2,
						pairs.get(i).maxTimeRatio,
						pairs.get(i).densityRatio);
			}
			writer.close();
			writer = new PrintWriter(scriptname);
			writer.println(script);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Process process = new ProcessBuilder("gnuplot", scriptname).start();
			ProcessBuilder pb = new ProcessBuilder("gnuplot", "graph.plt");
			pb.directory(new File("plot/distribution"));
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
