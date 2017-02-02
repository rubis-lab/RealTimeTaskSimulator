#ifndef __TPARALLEL__
#define __TPARALLEL__

#include <vector>
#include <cmath>

#include "../container/Param.h"
#include "../container/Thread.h"
#include "../../tools/FileIO.h"
#include "../../tools/CRand.h"

class TaskParallelizer
{
private:
	Param* pr;
	CRand* cr;
	int init();
public:
	TaskParallelizer();
	TaskParallelizer(Param* paramExt, CRand* crExt);
	~TaskParallelizer();

	std::vector<Thread> parallelizeTask(Task baseTask, int pcs, double meanOverhead, double variance);
	std::vector<Thread> splitTaskUniformly(Task baseTask, int pcs);
};

#endif