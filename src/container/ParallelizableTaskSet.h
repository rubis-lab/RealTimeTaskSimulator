#ifndef __PTSET__
#define __PTSET__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskSetModifier.h"
#include <vector>

class ParallelizableTaskSet
{
private:
	int option;
	TaskSet baseTaskSet;
	std::vector<TaskSet> taskSetList;
	TaskSet serializedTaskSet;
	int init(TaskSet baseTS);
	int serializeTaskSets();
public:
	ParallelizableTaskSet(TaskSet baseTS);
	int convertToOptionUniformly(int opt);

	int getCurrentOption();
	
	TaskSet getTaskSet();
	TaskSet getTaskSet(int taskSetIdx);

	Task getTask(int taskIdx);
	Task getTask(int taskSetIdx, int taskIdx);
};

#endif