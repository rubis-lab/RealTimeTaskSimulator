#ifndef __GENERATOR__
#define __GENERATOR__

#include <iostream>
#include <fstream>

#include "../container/Task.h"
#include "../container/TaskSet.h"

class Generator
{
	private:
		//double maxperiod;
	public:
		Generator();
		int loadConfig(std::ifstream &file);
		int saveConfig(void);
		Task nextTask();
		TaskSet nextTaskSet();
};

#endif
