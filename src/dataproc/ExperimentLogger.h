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
	int prec;
	double incrementSize;
	std::string expName;
	std::string fileName;
	std::vector<TaskSet> taskSets;
	std::ofstream *outFile;
	std::vector<int> schedulableSetCount;
	std::vector<int> totalSetCount;
	std::vector<double> probSchedulable;
	int init();
	int loadEnvironment(std::ifstream &file);
	int normalizeRecord();
public:	
	ExperimentLogger();
	ExperimentLogger(std::string ename, Param *paramExt);
	~ExperimentLogger();
	int startRecord(double inc);
	int addRecord(double util, bool sched);
	int printRecordLong();
	int printProbSched();
	int printUtilVsSchedulability(std::vector<double> &tsutil, std::vector<bool> &sched, double inc);
};

#endif