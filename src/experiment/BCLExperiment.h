#ifndef __BCL_EXP__
#define __BCL_EXP__

#include <vector>

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../generator/MarcoGenerator.h"
#include "../ops/TaskSetUtil.h"
#include "../test/BCL.h"

class BCLExperiment : public Experiment
{
private:
	int init();
	MarcoGenerator *mg;
	BCL *bcl;
	bool schedulable;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
	//std::vector<bool> schedulable;
public:
	BCLExperiment();
	int set();
	int run();
	int output();
};
#endif