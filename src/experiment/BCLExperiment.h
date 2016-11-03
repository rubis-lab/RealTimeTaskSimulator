#ifndef __BCL_EXP__
#define __BCL_EXP__

#include "../experiment/Experiment.h"
#include "../generator/MarcoGenerator.h"
#include "../ops/TaskSetUtil.h"
#include "../test/BCL.h"
#include <vector>

class BCLExperiment : public Experiment
{
private:
	int init();
	MarcoGenerator *mg;
	BCL *bcl;
	bool schedulable;
	//std::vector<bool> schedulable;
public:
	BCLExperiment();
	int set();
	int run();
	int output();
};
#endif