#include "TaskModifier.h"

// should delete this later on.
std::vector<Task> TaskModifier::splitTaskUniformly(Task baseTask, int pcs)
{
	std::vector<Task> retTasks;
	int idx = std::remainder((int)baseTask.getExecTime(), pcs);

	for(int i = 0; i < pcs; i++) {
		Task t = Task();

		// (0..idx-1) --> C/pcs + 1
		// (idx..pcs) --> C/pcs
		if(i < idx) {
			t.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs) + 1.0);
		} else {
			t.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs));	
		}

		// D, P --> same
		t.setDeadline(baseTask.getDeadline());
		t.setPeriod(baseTask.getPeriod());
		retTasks.push_back(t);
	}

	return retTasks;
}
/*
std::vector<Thread> TaskModifier::parallelizeTask(Task baseTask, int pcs, int nProc, double meanOverhead, double variance)
{
	std::vector<Thread> retThreadList;
	
	// apply overhead
	// overhead = 0 --> Ck
	// 			= 1 --> m * Ck
	// Ck * [1 + (m - 1) * overhead]
	// /m per thread
	double overheadExecTime = baseTask.getExecTime() * (1.0 + ((double)nProc - 1.0) * meanOverhead);
	overheadExecTime = std::round(overheadExecTime / nProc);

	// normalize variance
	// variance = 0 --> max 0 difference 
	//				a = Val .. b = Val
	// variance = 1 --> max Ck differenc
	//				a = Val - Ck / 2 .. b = Val + Ck / 2
	// uniform distribution
	// a, b = Val +- (Ck / 2) * variance
	double a = overheadExecTime - variance * baseTask.getExecTime() / 2;
	double b = overheadExecTime + variance * baseTask.getExecTime() / 2;
	


}
*/