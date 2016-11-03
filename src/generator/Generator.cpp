#include "Generator.h"

Generator::Generator()
{
	pr = new Param();
	init();
}

Generator::Generator(Param* paramExt)
{
	pr = paramExt;
	init();
}

int Generator::init()
{
	cr = CRand(pr->getSeed());
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