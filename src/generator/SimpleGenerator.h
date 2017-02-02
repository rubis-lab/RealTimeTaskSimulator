#ifndef __SIMPLE_GEN__
#define __SIMPLE_GEN__

#include "../generator/Generator.h"

class SimpleGenerator : public Generator
{
private:
	int init(std::ifstream &file);
public:
	SimpleGenerator();
	SimpleGenerator(Param *paramExt, CRand *crExt);
	//SimpleGenerator(Param *paramExt, std::ifstream &file);
	
	int loadConfig(std::ifstream &file);
	
	Task nextTask();
	TaskSet nextTaskSet();
};
#endif