#ifndef __JHAHN_EXP__
#define __JHAHN_EXP__

#include "../dataproc/ExperimentLogger.h"
#include "../experiment/Experiment.h"
#include "../generator/JHAhnGenerator.h"
#include "../ops/TaskSetUtil.h"
#include "../test/GEDF.h"

class JHAhnExperiment : public Experiment
{
private:
	int init();
  JHAhnGenerator gen;
  GEDF gedf;
  bool schedulable;
  void init(std::ifstream &file);
public:
	JHAhnExperiment();
	~JHAhnExperiment();
	int run();
  int set();
  int output();
  int loadEnvironment(std::ifstream &file);
};
#endif
