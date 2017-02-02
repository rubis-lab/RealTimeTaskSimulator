#include "NormalGenerator.h"

NormalGenerator::NormalGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/normgen.cfg");
	init(file);
	file.close();
}

NormalGenerator::NormalGenerator(Param *paramExt, CRand *cr) : Generator(paramExt, cr)
{
	// Default configuration
	std::ifstream file;
	file.open("../cfg/gen/normgen.cfg");
	init(file);
	file.close();
}
/*
NormalGenerator::NormalGenerator(Param *paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}
*/

NormalGenerator::~NormalGenerator()
{

}

int NormalGenerator::init(std::ifstream &file)
{
	loadConfig(file);
	return 1;
}

int NormalGenerator::loadConfig(std::ifstream &file)
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
	file >> sigMax;
	
	return 1;
}

std::vector<double> NormalGenerator::generateUtilizationArray(int n, double util)
{
	double normalizedSig = sigMax * util / pr->getNProc() / (double) n;
	double meanUtilization = util / (double) n;
	/*
	std::cout << "n " << n << std::endl;
	std::cout << "util " << util << std::endl;
	std::cout << "normalizedSig " << normalizedSig << std::endl;
	std::cout << "meanUtilization " << meanUtilization << std::endl;
	*/
	std::vector<double> ret;
	double sum;
	do {
		ret.clear();
		sum = 0.0;
		for(int i = 0; i < n; i++) {
			double candUtilization = -1.0;
			while(1) {
				candUtilization = cr->normal(meanUtilization, normalizedSig);
				if(candUtilization < 1.0 && candUtilization >= 0.0) {
					sum += candUtilization;
					ret.push_back(candUtilization);
					break;
				}
			}
		}
	} while(sum > util);

	return ret;
}

Task NormalGenerator::nextTask(double util)
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

TaskSet NormalGenerator::nextTaskSet()
{
	double candSumUtil = cr->uniform(0.0, pr->getNProc());

	int numTask = (int)std::round(cr->uniform(minN - 0.5, maxN + 0.5));

	std::vector<double> candUtilArray = generateUtilizationArray(numTask, candSumUtil);
	
	TaskSet tset = TaskSet();
	for(int i = 0; i < numTask; i++) {
		Task t = nextTask(candUtilArray[i]);
		tset.pushBack(t);
	}
	return tset;
}