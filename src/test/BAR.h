#ifndef __BAR__
#define __BAR__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include "../ops/TaskUtil.h"
#include "../ops/TaskSetUtil.h"
#include "../../tools/PMath.h"
#include <cmath>
#include <vector>
#include <iostream>

class BAR
{
protected:
	Param *pr;
	std::vector<double> iNC;
	std::vector<double> iCI;
	std::vector<double> getKMaxInterferingExecTime(TaskSet &ts, int k);
	std::vector<double> calcExtendedIntervalBound(TaskSet &ts);
	double calcNCInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval);
	double calcCarryIn(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval);
public:
	BAR();
	BAR(Param *paramExt);
	~BAR();
	
	bool isSchedulable(TaskSet &ts);
};
#endif