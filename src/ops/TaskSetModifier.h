#ifndef __TSMOD__
#define __TSMOD__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskModifier.h"
#include <vector>
#include <cmath>

namespace TaskSetModifier
{
	// splitter
	std::vector<TaskSet> splitAllTasksUniformly(TaskSet baseTaskSet, int pcs);

	// merger
	TaskSet mergeTaskSets(std::vector<TaskSet> tslist);

	// shuffle
	TaskSet shuffleTasks(TaskSet ts);

}
#endif