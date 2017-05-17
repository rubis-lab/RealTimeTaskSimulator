#ifndef __JHAHN_EXP__
#define __JHAHN_EXP__

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../ops/TaskSetUtil.h"

class JHAhnExperiment : public Experiment
{
private:
	int init();
public:
	JHAhnExperiment();
	~JHAhnExperiment();
	int run();
};
#endif