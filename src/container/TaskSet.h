#ifndef __TASKSET__
#define __TASKSET__

#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm>
#include "Task.h"

class TaskSet
{
private:
	std::vector<Task> ts;
public:
	TaskSet();

	// Config
	int readTaskSet(std::ifstream &file);
	int printTaskSet();

	// Vector operators
	int count();
	Task getTask(int idx);
	int pushBack(Task t);
	int clear();

	// Sums
	double sumDensity();
	double sumUtilization();

	// Min/Max
	double maxDensity();
	double maxUtilization();

	// Sorter
	int sortByDensity();
};

#endif
