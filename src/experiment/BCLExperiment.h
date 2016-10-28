#ifndef __BCL_EXP__
#define __BCL_EXP__

#include "../expriment/Experiment.h"
#include "../test/BCL.h"

class BCLExperiment : public Experiment
{
private:
	int init();
public:
	RTAExp();
	int testRun();
	int set();
	int run();
	int output();
};
#endif