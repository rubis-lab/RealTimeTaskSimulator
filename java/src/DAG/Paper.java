package DAG;

import multicore_exp.DAGTest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class Paper{

	public static int gen_mode = 2;
	public static int num_node = 0;
	public static int num_edge = 0;
	public static int group_mode = 2;
	public static boolean ng_print = false;
	public static int NUM_CORES = 1;

	public static void main(String args[]) {
		
		int seed = 0; //takes value 1 to 100;
		int TEST_RUN_NUMBER = 1000;
		double perSeedMin[] = new double[TEST_RUN_NUMBER];
		double perSeedMax[] = new double[TEST_RUN_NUMBER];
		double perDAGMin[] = new double[TEST_RUN_NUMBER];
		double perDAGMax[] = new double[TEST_RUN_NUMBER];
		boolean schedResultsClarvoyant[] = new boolean[TEST_RUN_NUMBER];
		boolean schedResultsWorst[] = new boolean[TEST_RUN_NUMBER];
		double avgNodeMin = 0.0;
		double avgNodeMax = 0.0;
		double recieveResult[] = new double[2];
		double minMaxAndIds[] = new double[4];
		ArrayList<Double> densities;

		parseArgs(args);
			
		if(gen_mode==1) System.out.println("by Gen1");
		else System.out.println("by Gen2");

		System.out.println("# of nodes : " + num_node);
		
		//ArrayList<NodeGroup> list;
		//ArrayList<ArrayList<NodeGroup>> lists = new ArrayList<ArrayList<NodeGroup>>();
		
		
		//List<Node> graphs = new ArrayList<Node>();
		

			//graphs.add(Dag.random_generate2(num_node, num_edge));
			
			//Node graph = gen_mode==1? Dag.random_generate(num_node):Dag.random_generate2(num_node, num_edge);
		
//		/***** Test 1 - This code runs test to calculate average of min and max across different 
//		 * seeded tasks sets, then calculates the average of min and max across a DAG generated for
//		 * the node number input, then reports the final min and max for that node.
//		 */
//		
//			Node[] graph = new Node[TEST_RUN_NUMBER]; 
//			ArrayList<ArrayList<NodeGroup>> list = new ArrayList<ArrayList<NodeGroup>>(TEST_RUN_NUMBER);
//			
//			
//			for(int i = 0; i<TEST_RUN_NUMBER; i++) {
//				graph[i] = Dag.random_generate2(num_node, num_edge);
//				list.add(Dag.getAllCase2(graph[i]));
//				for(seed = 1; seed < TEST_RUN_NUMBER + 1; seed++) {
//					//receive min and max for this particular seed
//					recieveResult = DensityTest(list.get(i), seed);
//					perSeedMin[seed-1] = recieveResult[0];
//					perSeedMax[seed-1] = recieveResult[1];
//				}
//				perDAGMin[i] = avgArray(perSeedMin);
//				perDAGMax[i] = avgArray(perSeedMax);
//			}
//			avgNodeMin = avgArray(perDAGMin);
//			avgNodeMax = avgArray(perDAGMax);
//			
//			System.out.println(avgNodeMin + " " + avgNodeMax);
//			
//			/***End of Test 1 ***/
			
//		/** Test 2 - This code looks at one DAG topology and then generates all of the
//		 * serializations associated with the DAG topology. Task sets are then 
//		 * run across each serialization. The density is reported for each task set
//		 * across each serialization as well as the ID for that serialization. The 
//		 * picture of the serialization is also provided for structural reference,
//		 * the min and max density across all serializations for the given task set
//		 * is also reported with the IDs of the serializations on which the min and max
//		 * occurred. 
//		 **/
//		
//		//One DAG topology is created - the starting topology is also drawn
//		Node topology = Dag.random_generate2(num_node, num_edge); 
//		Dag.DrawDag(topology);
//		System.out.println();
//		
//		//The serializations are created and stored in a list
//		ArrayList<NodeGroup> serialList = (Dag.getAllCase2(topology));
//		//Draw the serializations
//		DrawSerializations(serialList);
//		System.out.println();
//		
//		for(seed = 1; seed < TEST_RUN_NUMBER + 1; seed++) {
//			//receive min and max for this particular seed
//			minMaxAndIds = DensityTest(serialList, seed);
//			System.out.println("MinID/Min: " + minMaxAndIds[0] + " " + minMaxAndIds[1] + "; MaxID/Max: " + minMaxAndIds[2] + " " + minMaxAndIds[3]);
//			//System.out.println();
//			
//			//list densities for this seed on all the serializations, with serialization id
////			densities = DensityCapture(serialList, seed);
////			for(int j = 0; j <serialList.size(); j++) {
////				System.out.println(j + " " + densities.get(j));
////			}
////			System.out.println();
//			
//		}
//		
//		/*** End of Test 2 ***/
		
		
		
//		/*** Test 3 - Examine on a task by task basis ***/
//			
//		//One DAG topology is created - the starting topology is also drawn
//		Node topology = Dag.random_generate2(num_node, num_edge); 
//		Dag.DrawDag(topology);
//		System.out.println();
//		
//		//The serializations are created and stored in a list
//		ArrayList<NodeGroup> serialList = (Dag.getAllCase2(topology));
//		//Draw the serializations
//		DrawSerializations(serialList);
//		System.out.println();
//			
//		for(seed = 1; seed < TEST_RUN_NUMBER + 1; seed++) {
////			//receive min and max for this particular seed
////			minMaxAndIds = DensityTest(serialList, seed);
////			System.out.println("MinID/Min: " + minMaxAndIds[0] + " " + minMaxAndIds[1] + "; MaxID/Max: " + minMaxAndIds[2] + " " + minMaxAndIds[3]);
////			//System.out.println();
//			
//			//list densities for this seed on all the serializations, with serialization id
//			densities = DensityCapture(serialList, seed);
//			for(int j = 0; j <serialList.size(); j++) {
//				System.out.println(seed + " " + j + " " + densities.get(j));
//			}
//			System.out.println();
//			
//		}
//		
//		/*** End of Test 3 ***/
		
		
		/*** Test 4 - What percent of tasks are schedulable for most lengthy and shortest two serializations ***/
		
		//One DAG topology is created - the starting topology is also drawn
		Node topology = Dag.random_generate2(num_node, num_edge); 
		Dag.DrawDag(topology);
		System.out.println();
		
		//The serializations are created and stored in a list
		ArrayList<NodeGroup> serialList = (Dag.getAllCase2(topology));
		//Draw the serializations
		DrawSerializations(serialList);
		System.out.println();
			
		boolean schedResultsTemp[] = new boolean[2];
		for(seed = 1; seed < TEST_RUN_NUMBER + 1; seed++) {
			//receive min and max for this particular seed
			minMaxAndIds = DensityTest(serialList, seed);
			//System.out.println(seed + "MinID/Min: " + minMaxAndIds[0] + " " + minMaxAndIds[1] + "; MaxID/Max: " + minMaxAndIds[2] + " " + minMaxAndIds[3]);
			//System.out.println(seed + " " + minMaxAndIds[1] + " " + minMaxAndIds[3]);
			schedResultsTemp = SchedTest(minMaxAndIds);
			schedResultsClarvoyant[seed-1] = schedResultsTemp[0];
			schedResultsWorst[seed-1] = schedResultsTemp[1];	
			
//			//list densities for this seed on all the serializations, with serialization id
//			densities = DensityCapture(serialList, seed);
//			for(int j = 0; j <serialList.size(); j++) {
//				System.out.println(seed + " " + j + " " + densities.get(j));
//			}
//			System.out.println();
			
		}
		
		//calculate percent of tasks schedulable for clairvoyant and worst case
		double perSchedClar = 0.0;
		double perSchedWors = 0.0;
		perSchedClar = PercentSched(schedResultsClarvoyant);
		perSchedWors = PercentSched(schedResultsWorst);
		System.out.println("Clairvoyant: " + perSchedClar + " " + "Worst: " + perSchedWors);
		
		/*** End of Test 4 ***/
			
			//graph[0] = Dag.random_generate2(num_node, num_edge);
			//graph[1] = Dag.random_generate2(num_node, num_edge);
			
			

			//Dag.checkDepth(graphs.get(count), 0);
			
		
			
			
			/*if(group_mode == 2)
			{
				System.out.println("Get Groups with Dag!!!");
				//list contains combination of all the different grouping patterns
				list = Dag.getAllCase2(graph);
				//lists.add(Dag.getAllCase2(graphs.get(count)));
			}
			else
			{
				System.out.println("Get Groups with Same Depth!!!");
				NodeGroup group = Dag.makeGroup(graph);
				Dag.DrawNodeGroup(group);
				list = Dag.getAllCase(group);
			}*/
			
			
			
			
			
			//DensityTest(lists.get(count));
	
		

		//System.out.println("\n\n--------Draw Graph--------");

		//Dag.DrawDag(graph);

		//System.out.println("\n\n--------Draw Group--------");

		
		
		/*if (ng_print)
		{
			for(int i=0; i<list.size(); ++i)
			{
				System.out.println(i + "st NodeGroup");
				Dag.DrawNodeGroup(list.get(i));
				System.out.println();
			}
		}*/

		
	}
	
	public static double PercentSched(boolean array[]) {
		
		double percentSchedulable = 0.0;
		double length = (double)(array.length);
		double count = 0;
		
		for(int i = 0; i < array.length; i++) {
			if(array[i]) {
				//increase count when taskset is indicated as schedulable
				count++;
			}
		}
		
		percentSchedulable = (count/length)*100;
		
		return percentSchedulable;
	}
	
	//Schedulability Test - Clairvoyant and Worstcase
	
	public static boolean[] SchedTest(double array[]) {
		//min value at index 1 - represents density on clairvoyantly picked serialization
		//max value at index 3 - represents density for worst case
		boolean result[] = new boolean[2];
		
		//clarvoiyant result in result[0]
		if(array[1] < NUM_CORES) {
			result[0] = true;
		}
		else {
			result[0] = false;
		}
		
		//worst case result in result[1]
		if(array[3] < NUM_CORES) {
			result[1] = true;
		}
		else {
			result[1] = false;
		}
		
		return result;
		
	}

	//take average of double array
	public static double avgArray(double array[]) {
		double avg = 0.0;
		double sum = 0.0;
		int cnt = 0;
		
		for(int i=0; i<array.length; i++) {
			if(array[i] == Double.POSITIVE_INFINITY)
			{
				cnt++;
				i++;
			}
			sum += array[i];
		}
		
		avg = sum / (array.length - cnt);
		
		return avg;
	}
	
//{{{ Parse Args
	public static void parseArgs(String args[])
	{
		for(int i=0; i<args.length; i=i+2)
		{
			switch(args[i])
			{
				case "-n":
					num_node = Integer.parseInt(args[i+1]);
					break;
				case "-e":
					num_edge = Integer.parseInt(args[i+1]);
					break;
				case "-g":
					gen_mode = Integer.parseInt(args[i+1]);
					break;
				case "-gr":
					group_mode = Integer.parseInt(args[i+1]);
					break;
				case "-ng":
					ng_print = true;
			}
		}

		if(num_node == 0)
		{
			/**** Change Number of Nodes Here ******/
			num_node = 7;
			//num_node = ThreadLocalRandom.current().nextInt(5, 20);
			System.out.print("Random Generate ");
		}
		if(num_edge == 0)
			num_edge = (num_node-2)*(num_node-1)/6;

	}//}}}
	
	public static ArrayList<Double> DensityCapture(ArrayList<NodeGroup> seq, int seed)
	{
		ArrayList<Double> d_list = new ArrayList<Double>();

		DAGTest t = new DAGTest(num_node);

		for(int i=0; i<seq.size(); ++i)
		{
			ArrayList<ArrayList<Integer>> test = trans_dag(seq.get(i));
			double d = t.getDensity(seed, test);

			d_list.add(d);

		}
		
		
		//the index of this list will correspond to the id of the 'serialization/grouping'
		return d_list;


	}
	
	//Prints out all the serializations for the DAG topography
	public static void DrawSerializations(ArrayList<NodeGroup> seq) {
		for (int i=0; i<seq.size(); ++i) {
			System.out.println("Serialization id: " + i);
			Dag.DrawNodeGroup(seq.get(i));
		}
	}
	

//{{{ Density Test - return max and min and the id of the 'serialization/grouping' of which this occured
	public static double[] DensityTest(ArrayList<NodeGroup> seq, int seed)
	{
		ArrayList<Double> d_list = new ArrayList<Double>();
		double min = 16;
		int min_idx = 0;
		double max = 0;
		int max_idx = 0;
		double result[] = new double[4];

		
		//int seed = ThreadLocalRandom.current().nextInt(1, 100);
		//int seed = 3;

		DAGTest t = new DAGTest(num_node);

		for(int i=0; i<seq.size(); ++i)
		{
			ArrayList<ArrayList<Integer>> test = trans_dag(seq.get(i));
			double d = t.getDensity(seed, test);

			d_list.add(d);

			if(d < min)
			{
				min = d;
				min_idx = i;
			}
			else if (d > max)
			{
				max = d;
				max_idx = i;
			}
		}
		
		result[0] = min_idx;
		result[1] = min;
		result[2] = max_idx;
		result[3] = max;

		return result;

		/*System.out.println("!!!!!Print Density!!!!!");
		for(int i=0; i<d_list.size(); ++i)
			//System.out.println(String.format("%3dth : %f", i, d_list.get(i)));
			System.out.println(String.format("%f", d_list.get(i)));*/
		


		//System.out.println(String.format("\nSeed : %d\nMin idx : %3d, value : %f\nMax idx : %3d, value : %f",seed, min_idx, min, max_idx, max));

		
		/**************** Golden Statement Below *****************/
		//System.out.println(seed + " " + min + " " + max);
		
		
		/*System.out.println(String.format("%3dst Min", min_idx));
		Dag.DrawNodeGroup(seq.get(min_idx));
		System.out.println();

		System.out.println(String.format("%3dst Max", max_idx));
		Dag.DrawNodeGroup(seq.get(max_idx));
		System.out.println();*/


	}

	public static ArrayList<ArrayList<Integer>> trans_dag (NodeGroup dag)
	{
		ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
		
		NodeGroup cur = dag;
		while (cur != null)
		{
			ArrayList<Integer> t = new ArrayList<Integer>();
			ArrayList<Node> nodes = cur.getNodes();

			for(int i=0; i<nodes.size(); ++i)
				t.add(nodes.get(i).getId());

			temp.add(t);
			cur = cur.getNext();
		}

		return temp;
	}
//}}}
}
