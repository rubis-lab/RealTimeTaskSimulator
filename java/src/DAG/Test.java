package DAG;
import multicore_exp.DAGTest;
import java.util.ArrayList;

public class Test
{
	public static void main (String args[])
	{
		ArrayList<ArrayList<Integer>> nodeinfo = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> grp1 = new ArrayList<Integer>();
		ArrayList<Integer> grp2 = new ArrayList<Integer>();
		ArrayList<Integer> grp3 = new ArrayList<Integer>();
		ArrayList<Integer> grp4 = new ArrayList<Integer>();
		ArrayList<Integer> grp5 = new ArrayList<Integer>();
		
		grp1.add(0);
		grp2.add(1);
		grp2.add(2);
		grp2.add(3);
		grp1.add(4);
		grp1.add(5);
		grp2.add(6);
		grp2.add(7);
		grp2.add(8);
		grp1.add(9);
		
		
		nodeinfo.add(grp1);
		nodeinfo.add(grp2);
//		nodeinfo.add(grp3);
//		nodeinfo.add(grp4);
		//nodeinfo.add(grp5);

		DAGTest dagtest = new DAGTest(10);

		double density = dagtest.getDensity(0 ,nodeinfo);
		
		System.out.println(density);

	}
}
