package multicore_exp;

public class Param {

	public static boolean DebugMessage = false;
	
	public static int NumProcessors = 2;
//	public static int NumProcessors = 4;
	
//	public static double Period_MIN = 100;
//	public static double Period_MAX = 1000;
	public static double Period_MIN = 2;
	public static double Period_MAX = 20;
		
//	public static int NumSegments_MIN = 1;
//	public static int NumSegments_MAX = 5;
	public static int NumSegments_MIN = 1;
	public static int NumSegments_MAX = 1;
	
	public static int NumThreads_MIN = 1;
	public static int NumThreads_MAX = 2;
//	public static int NumThreads_MAX = (int)(3 * NumProcessors / 2);

	public static double WCET_MIN = 1;
	//public double WCET_MAX = T/s;
	
//	public static int NumTaskSets = 40000;
	public static int NumTaskSets = 10;
	public static int NumTaskInSet = 10;

	
	public static float ParallelTaskRatio_MIN = 0;
	public static float ParallelTaskRatio_MAX = 1;
	
/*	public static double alpha = 0.5; // parallelization overhead
	public static double beta  = 0.5; // deadline coefficient
*/	
	
	
	// 150225 수정된 generator용 parameters
	
	// # of segments
//	public static int kmin = 1;
//	public static int kmax = 1;
	
	// Execution Time of segment (when # of thread is 1)
	public static int scmin = 3;
	public static int scmax = 8;
	
	// max # of parallelization option
//	public static int omax = 2;
	
	
}
