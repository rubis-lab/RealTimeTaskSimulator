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

int Experiment::init(std::ifstream &file)
{
	pr = new Param();
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
