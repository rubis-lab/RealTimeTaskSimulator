#ifndef __BAR_EXP__
#define __BAR_EXP__

#include <vector>

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../ops/TaskSetUtil.h"
#include "../test/BAR.h"
#include "../test/BARMod.h"
#include "../generator/NormalGenerator.h"

class BARExperiment : public Experiment
{
private:
	int oMin;
	int oMax;
	double pOverhead;
	double pVariance;
	int init();
	NormalGenerator *ng;
	BAR *bar;
	BARMod *barMod;
	//ExperimentLogger *elNorm;
	ExperimentLogger *elPara;
	ExperimentLogger *elMod;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
public:
	BARExperiment();
	~BARExperiment();
	int set();
	int reset();
	int run();
	int output();
};
#endif