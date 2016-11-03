#ifndef __UNIFAST__
#define __UNIFAST__
#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"

class UniFastGenerator : public Generator
{
private:
	int minN;
	int maxN;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
public:
	UniFastGenerator();
	UniFastGenerator(Param *paramExt);
	UniFastGenerator(Param *paramExt, std::ifstream &file);
	
	Task nextTask(double util);
	TaskSet nextTaskSet();
};

#endif