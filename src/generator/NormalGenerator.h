#ifndef __NORMGEN__
#define __NORMGEN__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class NormalGenerator : public Generator
{
private:
	double minN;
	double maxN;
	double sigMax;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
	std::vector<double> generateUtilizationArray(int n, double util);
public:
	NormalGenerator();
	NormalGenerator(Param *paramExt);
	NormalGenerator(Param *paramExt, std::ifstream &file);
	~NormalGenerator();
	
	Task nextTask(double util);
	TaskSet nextTaskSet();
};

#endif