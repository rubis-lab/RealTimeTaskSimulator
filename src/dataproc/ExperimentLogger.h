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

#define __PRINT(x)	std::cout<<#x<<std::endl;
#define __DLINE	std::cout<<"================"<<std::endl;
#define __LINE 	std::cout<<"----------------"<<std::endl;

class ExperimentLogger
{
private:
	Param *pr;
	int outPrecision;
	double capacity;
	double incSize;
	int containerSize;
	std::string expName;
	std::string fileName;
	std::vector<TaskSet> taskSets;
	std::ofstream outFile;
	std::vector<int> schedulableSetCount;
	std::vector<int> totalSetCount;
	std::vector<double> probSchedulable;
	int init();
	int loadEnvironment(std::ifstream &file);
	int normalizeRecord();
public:	
	ExperimentLogger(std::string ename = "Default", Param *paramExt = 0, double cap = 1, double inc = 0.5);
	~ExperimentLogger();
	unsigned int getSize();
	double getInc();
	std::ofstream& getOutFile();

	double getProbSchedAtIdx(int idx);
	int addRecord(double util, bool sched);
	int printRecordLong();
	int outputRecord(std::string str = "***Result");
	int printProbSched();
	int printUtilVsSchedulability(std::vector<double> &tsutil, std::vector<bool> &sched, double inc);
};

#endif