#include "SimpleGenerator.h"

int SimpleGenerator::init()
{
	return 1;
}

int SimpleGenerator::loadConfig(std::ifstream &file)
{
	file = FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> numTask;
	file >> buf;
	file >> maxPeriod;
	file >> buf;
	file >> maxDeadline;
	file >> buf;
	file >> maxExecTime;

	return 1;
}

Task SimpleGenerator::nextTask(int id)
{
	Task t = Task();
	t.id = id;
	t.period = cr.uniform(minPeriod, maxPeriod);
	t.execTime = cr.uniform(minExecTime, maxExecTime);
	t.deadline = cr.uniform(minDeadline, maxDeadline);
	return t;
}

TaskSet SimpleGenerator::nextTaskSet(int id);
{
	TaskSet tset;
	tset.id = id;
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask(i);
		// need revision
		tset.putTask(t);
	}
	return tset;
}