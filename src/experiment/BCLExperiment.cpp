#include "BCLExperiment.h"

BCLExperiment::BCLExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../config/bclexp.cfg");
	init(file);
	file.close();
}

int BCLExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
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
	mg = new MarcoGenerator(pr);
	bcl = new BCL(pr);
	return 1;
}

int BCLExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = mg->nextTaskSet();
		TaskSetUtil::printTaskSet(ts);
		schedulable = bcl->isSchedulable(ts);
	}
	
	return 1;
}

int BCLExperiment::output()
{
	el = new ExperimentLogger(expName);

	/*
	if(schedulable) {
		std::cout << "Schedulable" << std::endl;
	} else {
		std::cout << "not Schedulable" << std::endl;
	}
	*/
	return 1;
}