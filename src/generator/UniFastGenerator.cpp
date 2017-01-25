#include "UniFastGenerator.h"
#include <iostream>

UniFastGenerator::UniFastGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ufast.cfg");
	init(file);
	file.close();
}

UniFastGenerator::UniFastGenerator(Param *paramExt) : Generator(paramExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ufast.cfg");
	init(file);
	file.close();
}

UniFastGenerator::UniFastGenerator(Param *paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}

int UniFastGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int UniFastGenerator::loadConfig(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> minN;
	file >> buf;
	file >> maxN;
	file >> buf;
	file >> minPeriod;
	file >> buf;
	file >> maxPeriod;
	
	return 1;
}

Task UniFastGenerator::nextTask(double util)
{
	double candPeriod = std::floor(cr.uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * util);
	double candDeadline = std::floor(cr.uniform(candExecTime, candPeriod));
	
	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);
	return t;
}

TaskSet UniFastGenerator::nextTaskSet()
{
	double candSumUtil = cr.uniform(0.0, pr->getNProc());
	int numTask = (int)std::round(cr.uniform(minN - 0.5, maxN + 0.5));
	std::vector<double> candUtilArray = PMath::Unifast(numTask, candSumUtil);

	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask(candUtilArray[i]);
		tset.pushBack(t);
	}
	return tset;
}



