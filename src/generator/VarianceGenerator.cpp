#include "VarianceGenerator.h"

VarianceGenerator::VarianceGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/vargen.cfg");
	init(file);
	file.close();
}

VarianceGenerator::VarianceGenerator(Param *paramExt, CRand *crExt) : Generator(paramExt, crExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/vargen.cfg");
	init(file);
	file.close();
}

VarianceGenerator::~VarianceGenerator()
{

}

int VarianceGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int VarianceGenerator::loadConfig(std::ifstream &file)
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
	file >> utilMin;
	file >> buf;
	file >> utilMax;
	return 1;
}

int VarianceGenerator::printInfo(std::ofstream &outFile)
{
	outFile<<"***Variance Generator"<<std::endl;
	outFile<<"minN\t\t"<<minN<<std::endl;
	outFile<<"maxN\t\t"<<maxN<<std::endl;
	outFile<<"minDeadline\t"<<minDeadline<<std::endl;
	outFile<<"minPeriod\t"<<minPeriod<<std::endl;
	outFile<<"maxPeriod\t"<<maxPeriod<<std::endl;
	outFile<<"utilMin\t\t"<<utilMin<<std::endl;
	outFile<<"utilMax\t\t"<<utilMax<<std::endl;
	outFile<<"----------------"<<std::endl;
	return 1;
}

Task VarianceGenerator::nextTask()
{
	double candUtilization = cr->uniform(utilMin, utilMax);
	double candPeriod = std::floor(cr->uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * candUtilization);
	double candDeadline = std::ceil(cr->uniform(minDeadline, candPeriod));
	
	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);
	return t;
}

TaskSet VarianceGenerator::nextTaskSet()
{
	int numTask = (int)std::round(cr->uniform(minN - 0.49, maxN + 0.49));
	
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask();
		t.setID(i);
		tset.pushBack(t);
	}
	return tset;
}