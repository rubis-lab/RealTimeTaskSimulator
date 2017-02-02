#include "Experiment.h"

Experiment::Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/exp.cfg");
	init(file);
	file.close();
}

Experiment::Experiment(std::ifstream &file)
{
	init(file);
}

Experiment::~Experiment() {
	delete tsp;
	delete cr;
	//delete el;
	delete pr;
}

int Experiment::init(std::ifstream &file)
{
	pr = new Param();
	//el = new ExperimentLogger(expName, pr);
	cr = new CRand(pr->getSeed());
	tsp = new TaskSetParallelizer(pr, cr);
	
	loadEnvironment(file);
	return 1;
}

int Experiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;
	file >> buf;
	file >> iter;
	file >> buf;
	file >> midResult;
	file >> buf;
	file >> utilizationInc;

	return 1;
}

int Experiment::set()
{
	return 1;
}

int Experiment::run()
{
	return 1;
}

int Experiment::output()
{
	return 1;
}
