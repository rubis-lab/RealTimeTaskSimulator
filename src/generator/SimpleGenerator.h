#ifndef __SIMPLE_GEN__
#define __SIMPLE_GEN__

#include "../../tools/FileIO.h"

class SimpleGenerator: public Generator
{
private:
	double numTask;
	double maxPeriod;
	double maxDeadline;
	double maxExecutionTime;

public:
	SimpleGenerator();
	int loadConfig(std::ifstream &file);
	//int saveConfig(void);
}
#endif