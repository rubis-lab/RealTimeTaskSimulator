#ifndef __TASKSETUTIL__
#define __TASKSETUTIL__

#include <iostream>
#include <fstream>
#include <algorithm>
#include <vector>
#include <numeric>

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskUtil.h"
#include "../../tools/PMath.h"

namespace TaskSetUtil
{
	// Config
	TaskSet readTaskSet(std::ifstream &file);

	// Debug
	int printTaskSet(TaskSet &ts);
	int printTaskInfo(TaskSet &ts);
	TaskSet copyTaskSet(TaskSet ts);

	// Sums
	double sumDensity(TaskSet &ts);
	double sumUtilization(TaskSet &ts);

	// Min/Max
	double getMaxDensity(TaskSet &ts);
	double getMaxUtilization(TaskSet &ts);

	// Sorter
	int sortByDensity(TaskSet &ts);

	//lcm
	double calcTaskLCM(TaskSet &ts);
}
#endif
