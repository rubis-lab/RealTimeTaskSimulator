#ifndef __UNIFAST__
#define __UNIFAST__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class UniFastGenerator : public Generator
{
private:
	double minN;
	double maxN;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
	std::vector<double> unifast(int n, double total);
public:
	UniFastGenerator();
	UniFastGenerator(Param *paramExt);
	UniFastGenerator(Param *paramExt, std::ifstream &file);
	
	Task nextTask(double util);
	TaskSet nextTaskSet();
};

#endif