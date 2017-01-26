#ifndef __TASKSET__
#define __TASKSET__

#include <vector>

#include "Task.h"

class TaskSet
{
private:
	std::vector<Task> ts;
public:
	TaskSet();

	// Vector operators
	int count();
	Task& getTask(int idx);
	int pushBack(Task &t);
	int clear();
	std::vector<Task> getVector();
};

#endif
