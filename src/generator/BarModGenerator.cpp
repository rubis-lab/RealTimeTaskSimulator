#include "BarModGenerator.h"

BarModGenerator::BarModGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/vargen.cfg");
	init(file);
	file.close();
}

BarModGenerator::BarModGenerator(Param *paramExt, CRand *crExt) : Generator(paramExt, crExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/vargen.cfg");
	init(file);
	file.close();
}

BarModGenerator::~BarModGenerator()
{

}

int BarModGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int BarModGenerator::loadConfig(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> minN;
	file >> buf;
	file >> maxN;
	file >> buf;
	file >> minDeadline;
	file >> buf;
	file >> minPeriod;
	file >> buf;
	file >> maxPeriod;
	file >> buf;
	file >> thrVariance;
	return 1;
}

int BarModGenerator::printInfo(std::ofstream &outFile)
{
	outFile<<"***Bar Mod Generator"<<std::endl;
	outFile<<"minN\t\t"<<minN<<std::endl;
	outFile<<"maxN\t\t"<<maxN<<std::endl;
	outFile<<"minDeadline\t"<<minDeadline<<std::endl;
	outFile<<"minPeriod\t"<<minPeriod<<std::endl;
	outFile<<"maxPeriod\t"<<maxPeriod<<std::endl;
	outFile<<"thrVariance\t"<<thrVariance<<std::endl;
	outFile<<"----------------"<<std::endl;
	return 1;
}

Task BarModGenerator::nextTask()
{
	double candPeriod = std::floor(cr->uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * candUtilization);
	double candDeadline = std::ceil(cr->uniform(minDeadline, candPeriod));
	
	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);
	return t;
}

std::vector<Task> BarModGenerator::nextTaskList(int id)
{
	std::vector<Task> tlist;
	double candPeriod = std::floor(cr->uniform(minPeriod, maxPeriod));

	int numTask = (int)std::round(cr->uniform(minN - 0.49, maxN + 0.49));
	for(int i = 0; i < numTask; i++) {

	}

	//double candDeadline = 


	return tlist;
}

TaskSet BarModGenerator::nextTaskSet()
{
	int numTask = pr->getNProc() + 1;
	
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask();
		t.setID(i);
		tset.pushBack(t);
	}
	return tset;
}

/*
1. choose task
2. set utilization, period, deadline, id
3. set num thread.
4. divide utilization, set exectime
*/