#ifndef __EXPLOGGER__
#define __EXPLOGGER__

#include <string>
#include <cmath>
#include <iostream>
#include <iomanip>
#include <fstream>
#include "../../tools/FileIO.h"
#include "../container/Param.h"
#include "../container/TaskSet.h"

class ExperimentLogger
{
private:
	Param *pr;
	std::string expName;
	std::string fileName;
	std::vector<TaskSet> taskSets;
	std::ofstream *outFile;
	int init();
public:	
	ExperimentLogger();
	ExperimentLogger(std::string ename, Param *paramExt);
	int printUtilVsSchedulability(std::vector<double> &tsutil, std::vector<bool> &sched, double inc);
};

#endif