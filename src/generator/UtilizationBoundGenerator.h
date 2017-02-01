#ifndef __UTILBOUND__
#define __UTILBOUND__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class UtilizationBoundGenerator : public Generator
{
private:
	int minN;
	int maxN;
	int capLow;
	int capHigh;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
public:
	UtilizationBoundGenerator();
	UtilizationBoundGenerator(Param *paramExt);
	UtilizationBoundGenerator(Param *paramExt, std::ifstream &file);
	
	Task nextTask(double util);
	TaskSet nextTaskSet();
};

#endif