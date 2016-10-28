#ifndef __RTAEXPERIMENT__
#define __RTAEXPERIMENT__

#include "../expriment/Experiment.h"
#include "../test/BCL.h"

class RTAExp : public Experiment
{
private:
public:
	RTAExp();
	int testRun();
};
#endif