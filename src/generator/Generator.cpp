#include "Generator.h"

Generator::Generator()
{
	cr = CRand();
	init();
}

Generator::Generator(int seed)
{
	cr = CRand(seed);
	init();
}

int Generator::init()
{
	return 1;
}

int Generator::loadConfig(std::ifstream &file)
{
	return 1;
}

int Generator::saveConfig()
{
	return 1;
}

Task Generator::nextTask()
{
	Task t = Task();
	return t;
}

TaskSet nextTaskSet()
{
	TaskSet tset = TaskSet();
	return tset;
}