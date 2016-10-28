#ifndef __SIMPLE_EXP__
#define __SIMPLE_EXP__

#include "../experiment/Experiment.h"
#include "../generator/SimpleGenerator.h"
#include "../ops/TaskSetModifier.h"
#include "../test/GFB.h"

class SimpleExperiment : public Experiment
{
private:
	int init();
	SimpleGenerator sg;
	bool schedulable;
public:
	SimpleExperiment();
	int set();
	int run();
	int output();
};

#endif