#ifndef __TASK__
#define __TASK__

#include <iostream>
#include <cmath>

class Task
{
private:
	int id;
	double period;
	double execTime;
	double deadline;
	//double responseTime;
	int priority;
	double offset;
	//taskSet# 
	//int seed;
	//string tag;
public:	
	Task();
	Task(int e, int d, int p);
	int getExecTime();
	int getDeadline();
	int getPeriod();
};

#endif
