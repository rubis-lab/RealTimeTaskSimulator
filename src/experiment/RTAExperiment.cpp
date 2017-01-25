#include "RTAExperiment.h"

RTAExperiment::RTAExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/rtaexp.cfg");
	init(file);
	file.close();
}

int RTAExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
	reset();
	return 1;
}

int RTAExperiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;
	file >> buf;
	file >> utilizationInc;

	return 1;
}

int RTAExperiment::set()
{
	//mg = new MarcoGenerator(pr);
	ug = new UniFastGenerator(pr);
	rta = new RTA(pr);
	return 1;
}

int RTAExperiment::reset()
{
	taskSetUtilization.clear();
	schedulability.clear();
	return 1;
}

int RTAExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = ug->nextTaskSet();
		
		taskSetUtilization.push_back(TaskSetUtil::sumUtilization(ts));

		schedulability.push_back(gfb->isSchedulable(ts));
	}
	
	return 1;
}

int RTAExperiment::output()
{
	el = new ExperimentLogger(expName, pr);
	el->printUtilVsSchedulability(taskSetUtilization, schedulability, utilizationInc);
	return 1;
}