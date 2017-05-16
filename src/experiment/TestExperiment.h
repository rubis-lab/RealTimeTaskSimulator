#ifndef __TEST_EXP__
#define __TEST_EXP__

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../ops/TaskSetUtil.h"
#include "../test/BAR.h"
#include "../test/BARMod.h"

class TestExperiment : public Experiment
{
private:
	BAR bar;
	BARMod barMod;
	int init();
public:
	TestExperiment();
	~TestExperiment();
	int run();
};
#endif