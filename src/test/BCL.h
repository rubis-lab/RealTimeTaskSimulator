#ifndef __BCL__
#define __BCL__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"

class BCL
{
private:
	double nProc;
	std::vector<double> slack;
	double calcInterference(Task tk, Task ti);
public:
	BCL(Param pr);
	bool isSchedulable(TaskSet ts);
}
#endif