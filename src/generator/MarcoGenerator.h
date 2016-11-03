#ifndef __MARCOGEN__
#define __MARCOGEN__

#include "../generator/Generator.h"
#include "../ops/TaskSetUtil.h"
#include "../test/NEC.h"

class MarcoGenerator : public Generator
{
private:
	double lmbd;
	NEC *nec;
	int init(std::ifstream &file);
	int loadConfig(std::ifstream &file);
	int reset();
public:
	MarcoGenerator();
	MarcoGenerator(Param *paramExt);
	MarcoGenerator(Param *paramExt, std::ifstream &file);
	
	Task nextTask();
	TaskSet nextTaskSet();
};
#endif