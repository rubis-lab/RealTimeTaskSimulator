#include "BARExperiment.h"

BARExperiment::BARExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/barexp.cfg");
	init(file);
	file.close();
}

int BARExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
	set();
	return 1;
}

int BARExperiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;
	file >> buf;
	file >> utilizationInc;

	return 1;
}

int BARExperiment::set()
{
	ug = new UniFastGenerator(pr);
	bar = new BAR(pr);
	return 1;
}

int BARExperiment::reset()
{
	taskSetUtilization.clear();
	schedulability.clear();
	return 1;
}

int BARExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = ug->nextTaskSet();
		
		taskSetUtilization.push_back(TaskSetUtil::sumUtilization(ts));

		schedulability.push_back(bar->isSchedulable(ts));
	}
	
	return 1;
}

int BARExperiment::output()
{
	el = new ExperimentLogger(expName, pr);
	el->printUtilVsSchedulability(taskSetUtilization, schedulability, utilizationInc);
	return 1;
}