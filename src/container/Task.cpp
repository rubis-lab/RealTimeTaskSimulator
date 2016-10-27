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

int Task::print()
{
	std::cout<<execTime<<" "<<deadline<<" "<<period<<std::endl;
	return 1;
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

double Task::getDensity()
{
	return execTime / deadline;
}

double Task::getUtilization()
{
	return execTime / period;
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