#ifndef __NEC__
#define __NEC__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include <cmath>

class NEC
{
private:
	double nProc;
public:	
	NEC(Param pr);
	bool isSchedulable(TaskSet ts);
};

#endif