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

std::vector<Thread> TaskParallelizer::parallelizeTask(Task baseTask, int pcs, double meanOverhead, double variance)
{
	// apply overhead
	// overhead = 0 --> Ck
	// 			= 1 --> Opt * Ck
	// Ck * [1 + (m - 1) * overhead]
	double overheadExecTime = baseTask.getExecTime() * (1.0 + (pcs - 1.0) * meanOverhead);

	// /m per thread
	overheadExecTime = overheadExecTime / pcs;

	// normalize variance
	// variance = 0 --> max 0 difference 
	//				a = Ck' .. b = Ck'
	// variance = 1 --> max 2Ck' difference
	//				a = 0 	.. b = 2Ck'
	
	// intervalLength = variance * Ck'
	double intervalLength = variance * overheadExecTime;

	// b should not exceed Ck. (maximum of Ck - Ck')
	double execTimeDifference = baseTask.getExecTime() - overheadExecTime;
	if(intervalLength > execTimeDifference) {
		intervalLength = execTimeDifference;
	}

	// uniform distribution
	// (a, b) = Ck' +- variance * Ck'
	double a = overheadExecTime - intervalLength;
	double b = overheadExecTime + intervalLength;

	std::vector<Thread> retThreadList;
	for(int i = 0; i < pcs; i++) {
		Thread thr = Thread();
		thr.setExecTime(std::round(cr->uniform(a, b)));

		// D, P --> same
		thr.setDeadline(baseTask.getDeadline());
		thr.setPeriod(baseTask.getPeriod());
		retThreadList.push_back(thr);
	}

	return retThreadList;
}

// this is same as parallelizeTask, when meanOverhead = 0 & variance ~ 0
std::vector<Thread> TaskParallelizer::splitTaskUniformly(Task baseTask, int pcs)
{
	std::vector<Thread> retThreadList;
	int idx = std::remainder((int)baseTask.getExecTime(), pcs);

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
		retThreadList.push_back(thr);
	}

	return retThreadList;
}