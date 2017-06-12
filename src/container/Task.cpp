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

Task::Task(int id, double e, double d, double p)
{
	ID = id;
	execTime = e;
	deadline = d;
	period = p;
}


Task::~Task()
{
	
}

int Task::getID()
{
	return ID;
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

int Task::setID(int id)
{
	ID = id;
	return 1;
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

bool Task::getIsProcessing()
{
  return isProcessing;
}

void Task::setIsProcessing(bool p)
{
  isProcessing = p;
}
