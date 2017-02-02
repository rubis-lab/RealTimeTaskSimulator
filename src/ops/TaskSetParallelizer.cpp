#include "TaskSetParallelizer.h"

TaskSetParallelizer::TaskSetParallelizer()
{
	init();
}

TaskSetParallelizer::TaskSetParallelizer(Param* paramExt, CRand* crExt)
{
	pr = paramExt;
	cr = crExt;
	init();
}

TaskSetParallelizer::~TaskSetParallelizer()
{
	delete tp;
}

int TaskSetParallelizer::init()
{
	tp = new TaskParallelizer(pr, cr);
	return 1;
}

TaskSet TaskSetParallelizer::parallelizeIntoOption(TaskSet baseTaskSet, int option)
{
	return parallelizeIntoOption(baseTaskSet, option, 0.0, 0.0);
}

TaskSet TaskSetParallelizer::parallelizeIntoOption(TaskSet baseTaskSet, int option, double overhead, double variance)
{
	TaskSet retTaskSet = TaskSet();

	for(int i = 0; i < baseTaskSet.count(); i++) {
		std::vector<Thread> thrs = tp->parallelizeTask(baseTaskSet.getTask(i), option, overhead, variance);
		for(unsigned int j = 0; j < thrs.size(); j++) {
			retTaskSet.pushBack(thrs[j]);
		}
	}
	
	return retTaskSet;
}

TaskSet TaskSetParallelizer::parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax, double overhead, double variance)
{
	TaskSet retTaskSet = TaskSet();

	for(int i = 0; i < baseTaskSet.count(); i++) {
		int option = (int)std::round(cr->uniform(optionMin - 0.49, optionMax + 0.49));
		std::vector<Thread> thrs = tp->parallelizeTask(baseTaskSet.getTask(i), option, overhead, variance);
		for(unsigned int j = 0; j < thrs.size(); j++) {
			retTaskSet.pushBack(thrs[j]);
		}
	}
	
	return retTaskSet;
}