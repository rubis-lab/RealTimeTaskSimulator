#ifndef __TSPARALLEL__
#define __TSPARALLEL__

#include "../container/Task.h"
#include "../container/Thread.h"
#include "../container/TaskSet.h"
#include "../ops/TaskParallelizer.h"
#include <vector>
#include <cmath>

class TaskSetParallelizer
{
private:
	Param* pr;
	CRand* cr;
	TaskParallelizer* tp;
	int init();
public:
	TaskSetParallelizer();
	TaskSetParallelizer(Param* paramExt, CRand* crExt);
	//TaskSetParallelizer();
	~TaskSetParallelizer();
	TaskSet parallelizeIntoOption(TaskSet baseTaskSet, int option);
	TaskSet parallelizeIntoOption(TaskSet baseTaskSet, int option, double overhead, double variance);
	TaskSet parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax, double overhead, double variance);
	
};
/*
namespace TaskSetModifier
{
	// splitter
	std::vector<TaskSet> splitAllTasksUniformly(TaskSet baseTaskSet, int pcs);

	// merger
	TaskSet mergeTaskSets(std::vector<TaskSet> tslist);

	// shuffle
	TaskSet shuffleTasks(TaskSet ts);

}
*/ 
#endif