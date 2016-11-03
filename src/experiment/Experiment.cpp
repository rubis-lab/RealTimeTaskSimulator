#include "Experiment.h"

Experiment::Experiment()
{
	pr = new Param();
	init();
}

Experiment::Experiment(std::ifstream &file)
{
	pr = new Param(file);
	init();
}

int Experiment::init()
{
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
