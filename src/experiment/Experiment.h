#ifndef __EXPERIMENT__
#define __EXPERIMENT__

#include "../container/Param.h"

class Experiment
{
protected:
	Param *pr;
	int init();
public:
	Experiment();
	Experiment(std::ifstream &file);
	int set();
	int run();
	int output();
};

#endif
