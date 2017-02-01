#include "UtilizationBoundGenerator.h"

UtilizationBoundGenerator::UtilizationBoundGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ubgen.cfg");
	init(file);
	file.close();
}

UtilizationBoundGenerator::UtilizationBoundGenerator(Param *paramExt) : Generator(paramExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ubgen.cfg");
	init(file);
	file.close();
}

UtilizationBoundGenerator::UtilizationBoundGenerator(Param *paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}

std::vector<double> BoundSplit(int n, double boundMin, double boundMax, double total)
{
	std::vector<double> ret;

	// error condition (capacity > capHigh or < capLow)
	if(((capLow * boundMin + capHigh * boundMax) / (capLow + capHigh)) * (double)n < total) {
		std::cout << "In BoundSplit: Total exeeds bound." << std::endl;
		exit(1);
	} else if () {

	}

	while(1) {
		ret.clear();
		for(int i = 0; i < n - 1; i++) {
			ret.push_back(cr.uniform(boundMin, boundMax));
		}

	}
	double sum = total;
	
	return ret;
}

int UtilizationBoundGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int UtilizationBoundGenerator::loadConfig(std::ifstream &file)
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
	file >> buf;
	file >> capLow;
	file >> buf;
	file >> capHigh;
	
	return 1;
}

Task UtilizationBoundGenerator::nextTask(double util)
{
	double candPeriod = std::floor(cr.uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * util);
	double candDeadline = std::ceil(cr.uniform(candExecTime, candPeriod));
	
	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);
	return t;
}

TaskSet UtilizationBoundGenerator::nextTaskSet()
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



