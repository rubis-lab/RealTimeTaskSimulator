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
	CRand cr;
	int init();
public:
	TaskParallelizer();
	TaskParallelizer(Param* paramExt);
	~TaskParallelizer();
};

#endif