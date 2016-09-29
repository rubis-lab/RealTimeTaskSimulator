#include "SimpleGenerator.h"

SimpleGenerator::SimpleGenerator()
{

}

int SimpleGenerator::loadConfig(std::ifstream &file)
{
	FileIO fio;
	file = fio.goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> numTask;
	file >> buf;
	file >> maxPeriod;
	file >> buf;
	file >> maxDeadline;
	file >> buf;
	file >> maxExecutionTime;

	return 1;
}