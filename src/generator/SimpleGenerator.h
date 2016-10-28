#ifndef __SIMPLE_GEN__
#define __SIMPLE_GEN__

#include "Generator.h"

class SimpleGenerator : public Generator
{
private:
	int init(std::ifstream &file);
public:
	SimpleGenerator(int seed);
	SimpleGenerator(int seed, std::ifstream &file);
	
	int loadConfig(std::ifstream &file);
	
	Task nextTask();
	TaskSet nextTaskSet();
};
#endif