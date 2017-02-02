#include "BARExperiment.h"

BARExperiment::BARExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/barexp.cfg");
	init(file);
	file.close();
}

BARExperiment::~BARExperiment()
{
	delete ng;
	delete bar;
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
	file >> buf;
	file >> midResult;

	return 1;
}

int BARExperiment::set()
{
	ng = new NormalGenerator(pr, cr);
	bar = new BAR(pr);
	el->startRecord(utilizationInc);
	return 1;
}

int BARExperiment::reset()
{
	return 1;
}

int BARExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = ng->nextTaskSet();
		//TaskSetUtil::printTaskInfo(ts);
		//TaskSetUtil::printTaskSet(ts);

		el->addRecord(TaskSetUtil::sumUtilization(ts), bar->isSchedulable(ts));

		if(i % midResult == 0) {
			el->printProbSched();
		}
	}
	
	return 1;
}

int BARExperiment::output()
{
	el->printRecordLong();

	return 1;
}