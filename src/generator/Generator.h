#ifndef __GENERATOR__
#define __GENERATOR__

#include <iostream>
#include <fstream>
#include <cstdlib>

#include "../container/Param.h"
#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../../tools/FileIO.h"
#include "../../tools/CRand.h"

class Generator
{
protected:
	Param* pr;
	CRand* cr;
	TaskSet ts;
	double numTask;
	double minPeriod;
	double maxPeriod;
	double minDeadline;
	double maxDeadline;
	double minExecTime;
	double maxExecTime;
	int init();

public:
	Generator();
	Generator(Param* paramExt, CRand* crExt);
	~Generator();

	int loadConfig(std::ifstream &file);
	int saveConfig();
	
	Task nextTask();
	TaskSet nextTaskSet();
};

#endif
