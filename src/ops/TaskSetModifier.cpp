#include "TaskSetModifier.h"

std::vector<TaskSet> TaskSetModifier::splitAllTasksUniformly(TaskSet baseTaskSet, int pcs)
{
	// will return n(pcs) task sets.
	std::vector<TaskSet> retTaskSets;
	for(int j = 0; j < pcs; j++) {
		TaskSet ts = TaskSet();
		retTaskSets.push_back(ts);
	}

	for(int i = 0; i < baseTaskSet.count(); i++) {
		std::vector<Task> splittedTask = TaskModifier::splitTaskUniformly(baseTaskSet.getTask(i), pcs);
		for(int j = 0; j < pcs; j++) {
			retTaskSets[j].pushBack(splittedTask[j]);
		}
	}
	return retTaskSets;
}

TaskSet TaskSetModifier::mergeTaskSets(std::vector<TaskSet> tslist) 
{
	TaskSet mergedTaskSet = TaskSet();
	for(unsigned int j = 0; j < tslist.size(); j++) {
		for(int i = 0; i < tslist[j].count(); i++) {
			mergedTaskSet.pushBack(tslist[j].getTask(i));
		}
	}
	return mergedTaskSet;
}

TaskSet TaskSetModifier::shuffleTasks(TaskSet ts)
{
	return ts;
}