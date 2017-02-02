#include "TaskParallelizer.h"

TaskParallelizer::TaskParallelizer()
{
	init();
}

TaskParallelizer::TaskParallelizer(Param* paramExt)
{
	pr = paramExt;
	init();
}

TaskParallelizer::~TaskParallelizer()
{
	init();
}

int TaskParallelizer::init()
{
	cr = CRand(pr->getSeed());
	return 1;
}