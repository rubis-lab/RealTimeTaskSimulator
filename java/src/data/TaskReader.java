package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TaskReader {
	public TaskReader()
	{
		
	}

	public static Task readTask(String taskString)
	{
		taskString = taskString.replaceAll("#.*","").replaceAll("^[ \t]+|\\n[ \t]+","");
		String[] segmentString = taskString.split("\\[Segment\\]");
		
		String[] taskInfoPart = segmentString[0].split("\\r?\\n");
		
		
		ArrayList<String> taskInfo = new ArrayList<String>();
		
		// remove empty line
		for (int i = 0; i < taskInfoPart.length; i++)
		{
			if (taskInfoPart[i].trim().length() == 0)
				continue;
			
			taskInfo.add(taskInfoPart[i]);
		}
		if (taskInfo.size() != 4) 
			throw new RuntimeException("task information is not correct");
		
		//Task ID, nSegments, period, deadline
		int taskID = Integer.parseInt(taskInfo.get(0));
		int nSegments = Integer.parseInt(taskInfo.get(1));
		double period = Double.parseDouble(taskInfo.get(2));
		double deadline = Double.parseDouble(taskInfo.get(3));
		Task task = new Task(nSegments, (int)period, (int)deadline);
		task.setTaskID (taskID);
		
		for (int i = 1; i < segmentString.length; i++)
		{
			int segmentIndex = i - 1;
			String[] segmentInfoPart = segmentString[i].split("\\r?\\n");
			
			// remove comments			
			int optionIndex = 0;
			for (int j = 0; j < segmentInfoPart.length; j++)
			{
				if (segmentInfoPart[j].trim().length() == 0) 
					continue;
				String[] threadString = segmentInfoPart[j].split(" |\\t");
				for (int k = 0; k < threadString.length; k++)
				{
					int threadIndex = k;
					double executionTime = Double.parseDouble(threadString[k]);
					
					task.setExecutionTime(segmentIndex, optionIndex, threadIndex, (int)executionTime);
				}
				optionIndex++;
			}			
		}
		return task;
	}
	
	public static TaskSet readTaskSet(String filename) throws IOException
	{
		TaskSet taskSet = new TaskSet();
		FileReader fileReader;
		BufferedReader bufferedReader;
		
		String buffer = "";
		
		fileReader = new FileReader(filename);
		bufferedReader = new BufferedReader(fileReader);
		
		String buf;
		while ((buf = bufferedReader.readLine()) != null)
		{
			buffer += buf + "\n";				
		}
		
		bufferedReader.close();

		
		String[] taskString = buffer.split("\\[Task\\]");
		
		//taskString[0] contains information about task set
		
		for (int i = 1; i < taskString.length; i++)
		{
			taskSet.add(readTask(taskString[i]));
		}
		
		return taskSet;
	}
}
