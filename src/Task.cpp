#include "Task.h"
Task::Task(void)
{

}
Task::Task(int e, int d, int p)
{
	executionTime = e;
	deadline = d;
	period = p;
}

int Task::getExecutionTime(void)
{
	return executionTime;
}

int Task::getDeadline(void)
{
	return deadline;
}

int Task::getPeriod(void)
{
	return period;
}