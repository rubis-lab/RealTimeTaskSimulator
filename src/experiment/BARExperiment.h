#ifndef __BAR_EXP__
#define __BAR_EXP__

#include <vector>

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../ops/TaskSetUtil.h"
#include "../test/BAR.h"
#include "../generator/UniFastGenerator.h"

class BARExperiment : public Experiment
{
private:
	int init();
	UniFastGenerator *ug;
	BAR *bar;
	double utilizationInc;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
	std::vector<double> taskSetUtilization;
	std::vector<bool> schedulability;
public:
	BARExperiment();
	int set();
	int reset();
	int run();
	int output();
};
#endif