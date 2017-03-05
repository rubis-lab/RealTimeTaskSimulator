#ifndef __VARGEN__
#define __VARGEN__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class VarianceGenerator : public Generator
{
private:
	double minN;
	double maxN;
	double utilMin;
	double utilMax;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
public:
	VarianceGenerator();
	VarianceGenerator(Param *paramExt, CRand *crExt);
	~VarianceGenerator();
	
	int printInfo(std::ofstream &outFile);
	Task nextTask();
	TaskSet nextTaskSet();
};

#endif