#ifndef __GFB__
#define __GFB__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"

class GFB
{
private:
	double nProc;
public:
	GFB(Param pr);
	bool isSchedulable(TaskSet ts);
};
#endif