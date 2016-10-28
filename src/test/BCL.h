#ifndef __BCL__
#define __BCL__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include <cmath>

class BCL
{
private:
	double nProc;
	std::vector<double> slack;
	double calcInterference(TaskSet ts, int baseTaskIndex, int interTaskIndex);
public:
	BCL(Param pr);
	bool isSchedulable(TaskSet ts);
};
#endif