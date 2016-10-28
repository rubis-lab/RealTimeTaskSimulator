#ifndef __NEC__
#define __NEC__

#include "../container/Param.h"
#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskUtil.h"
#include "../ops/TaskSetUtil.h"

#include <cmath>

class NEC
{
private:
	Param pr;
	int init();
public:	
	NEC();
	NEC(Param paramExt);
	bool passesNecTest(TaskSet ts);
};

#endif