#ifndef __EXPERIMENT__
#define __EXPERIMENT__

#include <string>

#include "../container/Param.h"
#include "../dataproc/ExperimentLogger.h"
#include "../../tools/CRand.h"
#include "../../tools/FileIO.h"

class Experiment
{
protected:
	Param *pr;
	ExperimentLogger *el;
	CRand *cr;
	std::string expName;
	int iter;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
public:
	Experiment();
	~Experiment();
	Experiment(std::ifstream &file);
	int set();
	int run();
	int output();
};

#endif
