package tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import multicore_exp.Util;
import data.PhasedTask;
import data.TaskSet;

public class Plot {

	public int xLimit = 60000;
	public void writeFile(String filename, TaskSet taskSet)
	{
		writeFile(filename, taskSet.toPhasedTask(TaskSet.ParallelizedOption.SELECTED));
	}
	
	public void writeFile(String filename, ArrayList<PhasedTask> taskSet)
	{
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			long LCM = multicore_exp.Util.getTaskSetLCM(taskSet);
			String initData = "";
			for (int i = 0; i < taskSet.size() ;i++)
				initData += String.format("%.4f\t", 0f);

			writer.printf("%10d\t%.4f\t%s\n", -1, 0f, initData);
			double prevDensity = 0;
			String prevString = initData;
			for (long t = 0; t < Math.min(LCM, xLimit); t++)
			{
				double densitySum1 = 0;
				double densitySum2 = 0;
				String taskDensityString1 = "";
				String taskDensityString2 = "";
				for (int i = 0; i < taskSet.size(); i++)
				{
					double density1 = taskSet.get(i).getInstanceDensity(t);
//					double density2 = taskSet.get(i).getInstanceDensityFallingEdge(t);
					double density2 = density1;

					densitySum1 += density1;
					densitySum2 += density2;
					taskDensityString1 += String.format("%.4f\t", density1);
					taskDensityString2 += String.format("%.4f\t", density2);
				}

				if (prevDensity != densitySum1)
				{
					writer.printf("%10d\t%.4f\t%s\n", t, prevDensity, prevString);
					writer.printf("%10d\t%.4f\t%s\n", t, densitySum1, taskDensityString1);
				}
				prevDensity = densitySum1;
				prevString = taskDensityString1;
//				if (!taskDensityString1.equals(taskDensityString2))
//					writer.printf("%10d\t%.4f\t%s\n", t, densitySum2, taskDensityString2);
//				writer.printf("%10d\t%.4f\t%s\n", t+1, densitySum2, taskDensityString2);
			}
			writer.printf("%10d\t%.4f\t%s\n", Math.min(LCM, xLimit), prevDensity, prevString);
			writer.printf("%10d\t%.4f\t%s\n", Math.min(LCM, xLimit), 0f, initData);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void writeGNUPlotScript(	String scriptFilename, 
									String dataFilename,
									String outputFilename, 
									TaskSet taskSet)
	{
		writeGNUPlotScript(scriptFilename, dataFilename, outputFilename, 
				taskSet.toPhasedTask(TaskSet.ParallelizedOption.SELECTED));
	}
	
	public void writeGNUPlotScript(	String scriptFilename, 
									String dataFilename,
									String outputFilename, 
									ArrayList<PhasedTask> taskSet)
	{
		String header = "";
		header += String.format("##########\n");
		header += String.format("#Task Information\n");
		header += String.format("#Number of tasks = $d\n", taskSet.size());
		header += String.format("#task id  (p    , e    , d    ) + phase\n", taskSet.size());
		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			header += String.format("#task %3d (%5d, %5d, %5d) + %d\n",
					i + 1, (int)task.getPeriod(), 
					(int)task.getExecutionTime(), 
					(int)task.getDeadline(), 
					task.getPhase());
		}
		header += String.format("##########\n");
		
		try {
			PrintWriter writer;
			writer = new PrintWriter(scriptFilename);
			String script = getGNUPlotScript(dataFilename, outputFilename, taskSet);
			script = script.replace(";", ";\n");
			script = script.replace(",", ",\\\n");
			writer.print(header + script);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getGNUPlotScript(String dataFilename, String outputFilename, ArrayList<PhasedTask> taskSet)
	{
		String script = "";
		script = String.format("set term post enhanced color eps;");
		script += String.format("set output '%s';", outputFilename);
		script += String.format("set xlabel 'time';");
		script += String.format("set ylabel 'density';");
		script += String.format("set xrange [0:%d];", 
				Math.min(Util.getTaskSetLCM(taskSet), xLimit));
		script += String.format("set yrange [0:];");
		script += String.format("set size 1.5, 1;");
		script += String.format("set key outside right reverse;");
		
		
		script += String.format("plot '%s' u 1:2 w l not", dataFilename);
		String columnInformation = "";
		String boxScript = "";
		String lineScript = "";

		for (int i = 0; i < taskSet.size(); i++)
		{
			PhasedTask task = taskSet.get(i);
			int p = (int) task.getPeriod();
			int e = (int) task.getExecutionTime();
			int d = (int) task.getDeadline();
			int phase = task.getPhase();
			String title = String.format("%3d + (%3d, %3d, %3d)", phase, e, d, p); 

			if (i > 0) columnInformation += "+";
			columnInformation += String.format("$%d", i + 3);

			boxScript = String.format(",'' u 1:(%s) w filledcurves t '%s'", 
					columnInformation, title) + boxScript; 
			lineScript = String.format(",'' u 1:(%s) w l not lw 2 lc 'black'", columnInformation) + lineScript; 
		}
		script += boxScript + lineScript;
		return script;
	}
	
	public void drawGNUPlot(String dataFilename, String outputFilename, ArrayList<PhasedTask> taskSet)
	{
		writeFile(dataFilename, taskSet);
		
		String script = getGNUPlotScript(dataFilename, outputFilename, taskSet);
		
		try {
			Process process = new ProcessBuilder("gnuplot","-e", script).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void drawGNUPlotWithScript(	String scriptFilename, 
										String dataFilename, 
										String outputFilename, 
										ArrayList<PhasedTask> taskSet_ori)
	{
		ArrayList<PhasedTask> taskSet = new ArrayList<PhasedTask>(taskSet_ori);
		for (int i = 0; i < taskSet.size() - 1; i++)
		{
			for (int j = i + 1; j < taskSet.size(); j++)
			{
				if (taskSet.get(i).getDeadline() < taskSet.get(j).getDeadline())
				{
					PhasedTask temp = taskSet.get(i);
					taskSet.set(i, taskSet.get(j));
					taskSet.set(j, temp);
				}
			}
		}
		writeFile(dataFilename, taskSet);
		
		writeGNUPlotScript(scriptFilename, dataFilename, outputFilename, taskSet);
		
		try {
			Process process = new ProcessBuilder("gnuplot", scriptFilename).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
