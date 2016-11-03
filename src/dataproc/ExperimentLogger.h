#ifndef __EXPLOGGER__
#define __EXPLOGGER__

#include <string>
#include <iostream>
#include <fstream>
#include "../../tools/FileIO.h"
#include "../container/Param.h"
#include "../container/TaskSet.h"

class ExperimentLogger
{
private:
	std::string expName;
	std::string fileName;
	std::vector<TaskSet> taskSets;
	std::ofstream *outFile;
	int init(std::string fname);
public:	
	ExperimentLogger();
	ExperimentLogger(std::string fname);
	int printTask();
	int printSchedulabilitySimple();
};

#endif