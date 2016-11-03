#include "MarcoGenerator.h"

MarcoGenerator::MarcoGenerator() : Generator()
{
	// Default configuration
	std::ifstream file;
	file.open("../config/marcogen.cfg");
	init(file);
	file.close();
}

MarcoGenerator::MarcoGenerator(Param *paramExt) : Generator(paramExt)
{
	// Default configuration
	std::ifstream file;
	file.open("../config/marcogen.cfg");
	init(file);
	file.close();
}

MarcoGenerator::MarcoGenerator(Param *paramExt, std::ifstream &file) : Generator(paramExt)
{
	init(file);
}

int MarcoGenerator::init(std::ifstream &file)
{
	nec = new NEC(pr);
	loadConfig(file);
	return 1;
}

int MarcoGenerator::loadConfig(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> lmbd;
	file >> buf;
	file >> minPeriod;
	file >> buf;
	file >> maxPeriod;

	return 1;
}

int MarcoGenerator::reset()
{
	return 1;
}

Task MarcoGenerator::nextTask()
{
	// pick a utilization < 1.0
	// should be exp dist, but has error
	/*
	double candUtilization = 0.0;
	while(candUtilization < 0.0 || candUtilization >= 1.0) {
		candUtilization = cr.exponential(lmbd);
	}
	*/
	
	double candUtilization = 0.0;
	
	while(candUtilization <= 0.0 || candUtilization >= 1.0) {
		candUtilization = cr.normal(lmbd, 0.2);
	}
	//std::cout<<"util: "<<candUtilization<<std::endl;
	
	double candPeriod = std::floor(cr.uniform(minPeriod, maxPeriod));
	double candExecTime = std::floor(candPeriod * candUtilization);
	double candDeadline = std::floor(cr.uniform(candExecTime, candPeriod));

	Task t = Task();
	t.setExecTime(candExecTime);
	t.setDeadline(candDeadline);
	t.setPeriod(candPeriod);

	return t;
}

TaskSet MarcoGenerator::nextTaskSet()
{
	// clean run
	if(ts.count() == 0) {
		// generate m + 1 tasks
		do {
			ts.clear();
			while(ts.count() <= pr->getNProc()) {
				Task t = nextTask();
				ts.pushBack(t);
			}
		// until it passes necessary test
		} while (!nec->passesNaiveNecTest(ts));
		//while (!nec->passesNecTest(ts));
		return ts;
	}
	
	// already have some tasks in --> append a new task
	Task t = nextTask();
	ts.pushBack(t);

	// cannot pass necessary test --> start over
	if(!nec->passesNaiveNecTest(ts)) {
	//if(!nec->passesNecTest(ts)) {
		ts.clear();
		return nextTaskSet();
	}

	return ts;
}