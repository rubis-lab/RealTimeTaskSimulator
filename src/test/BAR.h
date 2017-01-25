#ifndef __BAR__
#define __BAR__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include <cmath>

class BAR
{
private:
	Param *pr;
	double calcAkBound(Task &t);
	double calcNCInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex);
	double calcCIInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex);
public:
	BAR();
	BAR(Param *paramExt);
	bool isSchedulable(TaskSet &ts);
};
#endif