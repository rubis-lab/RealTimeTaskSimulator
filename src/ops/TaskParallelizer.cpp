#include "TaskParallelizer.h"

TaskParallelizer::TaskParallelizer()
{
	init();
}

TaskParallelizer::TaskParallelizer(Param* paramExt, CRand* crExt)
{
	pr = paramExt;
	cr = crExt;
	init();
}

TaskParallelizer::~TaskParallelizer()
{
	
}

int TaskParallelizer::init()
{
	return 1;
}

// Sorts threads in execution time descending order.
std::vector<Thread> TaskParallelizer::sortThreadsDescending(std::vector<Thread> thrList)
{
	//std::vector<Thread> ret;
	std::sort(thrList.begin(), thrList.end(), \
		[] (Thread t1, Thread t2) {return t1.getExecTime() > t2.getExecTime();});
	return thrList;
}

std::vector<Thread> TaskParallelizer::parallelizeTask(Task baseTask, int pcs, double meanOverhead, double variance)
{
	// apply overhead
	// overhead = 0 --> Ck
	// 			= 1 --> Opt * Ck
	// Ck * [1 + (m - 1) * overhead]
	double overheadExecTime = baseTask.getExecTime() * (1.0 + (pcs - 1.0) * meanOverhead);

	// /m per thread
	overheadExecTime = overheadExecTime / pcs;

/*
	// normalize variance
	// variance = 0 --> max 0 difference 
	//				a = Ck' .. b = Ck'
	// variance = 1 --> max 2Ck' difference
	//				a = 0 	.. b = 2Ck'
	
	// intervalLength = variance * Ck'
	double intervalDiff = variance * overheadExecTime;

	// b should not exceed Ck. (maximum of Ck - Ck')
	double execTimeDifference = baseTask.getExecTime() - overheadExecTime;
	if(intervalDiff > execTimeDifference) {
		intervalDiff = execTimeDifference;
	}

	// uniform distribution
	// (a, b) = Ck' +- variance * Ck'
	double a = overheadExecTime - intervalDiff;
	double b = overheadExecTime + intervalDiff;
*/

	// normalize variance
	// variance = 0 --> max 0 difference 
	//				a = Ck' .. b = Ck'
	// variance = 1 --> max Ck difference
	//				a = 0 	.. b = Ck
	// a = Ck'(1 - variance)
	// b = Ck'(1 + (Ck - Ck') * variance)

	double a = overheadExecTime * (1.0 - variance);
	double b = overheadExecTime * (1.0 + (baseTask.getExecTime() - overheadExecTime) * variance);
	
	std::vector<Thread> tmpThreadList;
	for(int i = 0; i < pcs; i++) {
		Thread thr = Thread();
		thr.setExecTime(std::round(cr->uniform(a, b)));

		// D, P --> same
		thr.setDeadline(baseTask.getDeadline());
		thr.setPeriod(baseTask.getPeriod());

		// parent ID
		thr.setID(baseTask.getID());

		tmpThreadList.push_back(thr);
	}

	// sort threads
	std::vector<Thread> retThreadList = sortThreadsDescending(tmpThreadList);
	for(int i = 0; i < pcs; i++) {
		retThreadList[i].setTID(i);
	}

	return retThreadList;
}

// this is same as parallelizeTask, when meanOverhead = 0 & variance ~ 0
std::vector<Thread> TaskParallelizer::splitTaskUniformly(Task baseTask, int pcs)
{
	std::vector<Thread> retThreadList;
	int idx = std::fmod((int)baseTask.getExecTime(), pcs);

	for(int i = 0; i < pcs; i++) {
		Thread thr = Thread();

		// (0..idx-1) --> C/pcs + 1
		// (idx..pcs) --> C/pcs
		if(i < idx) {
			thr.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs) + 1.0);
		} else {
			thr.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs));	
		}

		// D, P --> same
		thr.setDeadline(baseTask.getDeadline());
		thr.setPeriod(baseTask.getPeriod());

		// ID
		thr.setID(baseTask.getID());
		thr.setTID(i);
		
		retThreadList.push_back(thr);
	}

	return retThreadList;
}
