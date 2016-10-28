#ifndef __TASKUTIL__
#define __TASKUTIL__

#include <iostream>
#include <cmath>

#include "../container/Task.h"

namespace TaskUtil
{
	// print
	int printTask(Task t);

	// primitive
	double calcDensity(Task t);
	double calcUtilization(Task t);

	// Demand 
	double calcDemandOverInterval(Task t, double interval);
}
#endif