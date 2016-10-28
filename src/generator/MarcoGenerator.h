#ifndef __MARCOGEN__
#define __MARCOGEN__

#include "Generator.h"

class MarcoGenerator : public Generator
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