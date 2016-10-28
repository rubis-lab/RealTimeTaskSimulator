#include "MarcoGenerator.h"

MarcoGenerator::MarcoGenerator(int seed) : Generator(seed)
{
	// Default configuration
	std::ifstream file;
	file.open("../config/marcogen.cfg");
	init(file);
	file.close();
}

MarcoGenerator::MarcoGenerator(int seed, std::ifstream &file) : Generator(seed)
{
	init(file);
}

int MarcoGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int MarcoGenerator::loadConfig(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

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

Task MarcoGenerator::nextTask()
{
	Task t = Task();
	t.setExecTime(cr.uniform(minExecTime, maxExecTime));
	t.setDeadline(cr.uniform(minDeadline, maxDeadline));
	t.setPeriod(cr.uniform(minPeriod, maxPeriod));
	return t;
}

TaskSet MarcoGenerator::nextTaskSet()
{
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask();
		tset.pushBack(t);
	}
	return tset;
}