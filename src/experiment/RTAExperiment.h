#ifndef __RTA_EXP__
#define __RTA_EXP__

#include <vector>

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../ops/TaskSetUtil.h"
#include "../test/RTA.h"
#include "../generator/UniFastGenerator.h"

class RTAExperiment : public Experiment
{
private:
	int init();
	UniFastGenerator *ug;
	BCL *bcl;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
	std::vector<double> taskSetUtilization;
	std::vector<bool> schedulability;
public:
	RTAExperiment();
	int set();
	int reset();
	int run();
	int output();
};
#endif