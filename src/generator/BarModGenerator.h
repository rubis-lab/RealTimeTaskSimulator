#ifndef __BARMODGEN__
#define __BARMODGEN__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class BarModGenerator : public Generator
{
private:
	double minN;
	double maxN;
	double utilMin;
	double utilMax;
	double thrVariance;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
public:
	BarModGenerator();
	BarModGenerator(Param *paramExt, CRand *crExt);
	~BarModGenerator();
	
	int printInfo(std::ofstream &outFile);
	Task nextTask();
	TaskSet nextTaskSet();
};

#endif