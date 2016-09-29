#ifndef __GENERATOR__
#define __GENERATOR__

#include <iostream>
#include <fstream>

#include "../container/Task.h"
#include "../container/TaskSet.h"

class Generator
{
	private:
		double period;
	public:
		Generator();
		int setParam();
		Task nextTask();
		TaskSet nextTaskSet();
};

#endif
