#ifndef __TASK__
#define __TASK__

#include <iostream>

class Task
{
private:
	int id;
	double period;
	double execTime;
	double deadline;
	//int priority;
	double offset;
public:	
	Task();
	Task(double e, double d, double p);
	int print();

	double getExecTime();
	double getDeadline();
	double getPeriod();
	double getDensity();
	double getUtilization();

	int setExecTime(double e);
	int setDeadline(double d);
	int setPeriod(double p);
};

#endif
