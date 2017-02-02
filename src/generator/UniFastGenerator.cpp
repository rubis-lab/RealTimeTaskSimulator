#include "UniFastGenerator.h"

UniFastGenerator::UniFastGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ufast.cfg");
	init(file);
	file.close();
}

UniFastGenerator::UniFastGenerator(Param *paramExt, CRand *crExt) : Generator(paramExt, crExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/ufast.cfg");
	init(file);
	file.close();
}
/*
UniFastGenerator::UniFastGenerator(Param *paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}
*/

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

std::vector<double> UniFastGenerator::unifast(int n, double total)
{
	std::vector<double> ret;
	double sum = total;
	for(int i = 0; i < n - 1; i++) {
		double base = cr->uniform(0.00, 1.00);
		double tmp = sum * std::pow(base, (1.00 / (double)(n - i)));
		ret.push_back(sum - tmp);
		sum = tmp;
	}
	ret.push_back(sum);
	return ret;
}

Task UniFastGenerator::nextTask(double util)
{
	double candPeriod = std::floor(cr->uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * util);
	double candDeadline = std::ceil(cr->uniform(candExecTime, candPeriod));
	
	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);
	return t;
}

TaskSet UniFastGenerator::nextTaskSet()
{
	double candSumUtil = cr->uniform(0.0, pr->getNProc());

	int numTask = (int)std::round(cr->uniform(minN - 0.49, maxN + 0.49));
	std::vector<double> candUtilArray = unifast(numTask, candSumUtil);
	
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask(candUtilArray[i]);
		tset.pushBack(t);
	}
	return tset;
}



