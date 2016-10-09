#include "Task.h"
Task::Task(void)
{

}
Task::Task(int e, int d, int p)
{
	execTime = e;
	deadline = d;
	period = p;
}

int Task::getExecTime(void)
{
	return execTime;
}

int Task::getDeadline(void)
{
	return deadline;
}

int Task::getPeriod(void)
{
	return period;
}