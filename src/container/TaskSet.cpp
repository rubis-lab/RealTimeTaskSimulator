#include "TaskSet.h"

TaskSet::TaskSet()
{

}

int TaskSet::count() 
{
	return ts.size();
}

Task TaskSet::getTask(int idx)
{
	return ts[idx];
}

int TaskSet::pushBack(Task t)
{
	ts.push_back(t);
	return 1;
}

int TaskSet::clear()
{
	ts.clear();
	return 1;
}

std::vector<Task> TaskSet::getVector()
{
	return ts;
}