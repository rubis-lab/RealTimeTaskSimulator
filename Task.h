#ifndef __TASK__
#define __TASK__

#include <iostream>

class Task
{
private:
	int id;
	double period;
	double executionTime;
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
	int getExecutionTime();
	int getDeadline();
	int getPeriod();
};

#endif