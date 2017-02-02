#include "BCLExperiment.h"

BCLExperiment::BCLExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/bclexp.cfg");
	init(file);
	file.close();
}

int BCLExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
	set();
	return 1;
}

int BCLExperiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;

	return 1;
}

int BCLExperiment::set()
{
	//mg = new MarcoGenerator(pr);
	ug = new UniFastGenerator(pr, cr);
	//bcl = new BCL(pr);
	gfb = new GFB(pr);
	return 1;
}

int BCLExperiment::reset()
{
	taskSetUtilization.clear();
	schedulability.clear();
	return 1;
}

int BCLExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = ug->nextTaskSet();
		//TaskSetUtil::printTaskSet(ts);
		
		taskSetUtilization.push_back(TaskSetUtil::sumUtilization(ts));

		schedulability.push_back(gfb->isSchedulable(ts));
	}
	
	return 1;
}

int BCLExperiment::output()
{
	//el = new ExperimentLogger(expName, pr);
	//el->printUtilVsSchedulability(taskSetUtilization, schedulability, utilizationInc);
	return 1;
}