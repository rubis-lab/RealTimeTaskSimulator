#ifndef __TASKSET__
#define __TASKSET__

#include <iostream>
#include <fstream>
#include <vector>
#include "Task.h"

class TaskSet
{
private:
	int id;
	int count;
	std::vector<Task> ts;
	//generator gen;
public:
	TaskSet();
	int readTaskSet(std::ifstream &file);
	int printTaskSet(void);
	int length();
	Task getTask(int);
	void putTask(Task t);
	void clear();

};

#endif
