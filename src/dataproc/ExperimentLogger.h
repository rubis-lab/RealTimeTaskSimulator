#ifndef __EXPLOGGER__
#define __EXPLOGGER__

#include <string>
#include "../container/Param.h"
#include "../container/TaskSet.h"

class ExperimentLogger
{
private:
	std::string expName;
	std::vector<TaskSet> taskSets;
public:	
	ExperimentLogger();
	ExperimentLogger(std::string name);

	int printSchedulabilitySimple();
};

#endif