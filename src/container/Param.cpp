#include "Param.h"

Param::Param()
{
	// Default configuration
	std::ifstream file;
	file.open("../config/env.cfg");
	init(file);
	file.close();
}

Param::Param(std::ifstream &file)
{
	init(file);
}

int Param::init(std::ifstream &file)
{
	loadEnvironment(file);
	if(!seed) {
		seed = rand();
	}
	return 1;
}

int Param::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> nProc;
	file >> buf;
	file >> seed;
	return 1;
}

double Param::getNProc()
{
	return nProc;
}

int Param::getSeed()
{
	return seed;
}