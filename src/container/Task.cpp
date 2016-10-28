#include "Task.h"

Task::Task()
{

}

Task::Task(double e, double d, double p)
{
	execTime = e;
	deadline = d;
	period = p;
}

double Task::getExecTime()
{
	return execTime;
}

double Task::getDeadline()
{
	return deadline;
}

double Task::getPeriod()
{
	return period;
}

int Task::setExecTime(double e)
{
	execTime = e;
	return 1;
}
int Task::setDeadline(double d)
{
	deadline = d;
	return 1;
}
int Task::setPeriod(double p)
{
	period = p;
	return 1;
}