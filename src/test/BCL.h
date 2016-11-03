#ifndef __BCL__
#define __BCL__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include <cmath>

class BCL
{
private:
	Param *pr;
	std::vector<double> slack;
	int reset();
	double calcInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex);
public:
	BCL();
	BCL(Param *paramExt);
	bool isSchedulable(TaskSet &ts);
};
#endif