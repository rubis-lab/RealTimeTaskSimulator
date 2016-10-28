#include "SimpleGenerator.h"

SimpleGenerator::SimpleGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../config/sgen.cfg");
	init(file);
	file.close();
}

SimpleGenerator::SimpleGenerator(Param paramExt) : Generator(paramExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../config/sgen.cfg");
	init(file);
	file.close();
}

SimpleGenerator::SimpleGenerator(Param paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}

int SimpleGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int SimpleGenerator::loadConfig(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> numTask;
	file >> buf;
	file >> minPeriod;
	file >> buf;
	file >> maxPeriod;
	file >> buf;
	file >> minDeadline;
	file >> buf;
	file >> maxDeadline;
	file >> buf;
	file >> minExecTime;
	file >> buf;
	file >> maxExecTime;
	
	return 1;
}

Task SimpleGenerator::nextTask()
{
	Task t = Task();
	t.setExecTime(cr.uniform(minExecTime, maxExecTime));
	t.setDeadline(cr.uniform(minDeadline, maxDeadline));
	t.setPeriod(cr.uniform(minPeriod, maxPeriod));
	return t;
}

TaskSet SimpleGenerator::nextTaskSet()
{
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask();
		tset.pushBack(t);
	}
	return tset;
}