#include "ParallelizableTaskSet.h"

ParallelizableTaskSet::ParallelizableTaskSet(TaskSet baseTS)
{
	baseTaskSet = baseTS;
	init(baseTS);
}

int ParallelizableTaskSet::init(TaskSet baseTS)
{
	option = 1;
	taskSetList.push_back(baseTS);
	serializeTaskSets();
	return 1;
}

int ParallelizableTaskSet::serializeTaskSets()
{
	serializedTaskSet = TaskSetModifier::mergeTaskSets(taskSetList);
	return 1;
}

int ParallelizableTaskSet::convertToOptionUniformly(int opt)
{
	option = opt;
	taskSetList.clear();
	taskSetList = TaskSetModifier::splitAllTasksUniformly(baseTaskSet, opt);
	serializeTaskSets();
	return 1;
}

int ParallelizableTaskSet::getCurrentOption()
{
	return option;
}

TaskSet ParallelizableTaskSet::getTaskSet(int taskSetIdx)
{
	return taskSetList[taskSetIdx];
}

TaskSet ParallelizableTaskSet::getTaskSet()
{
	return serializedTaskSet;
}

Task ParallelizableTaskSet::getTask(int taskIdx)
{
	return serializedTaskSet.getTask(taskIdx);
}

Task ParallelizableTaskSet::getTask(int taskSetIdx, int taskIdx)
{
	return taskSetList[taskSetIdx].getTask(taskIdx);
}