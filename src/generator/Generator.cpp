#include "Generator.h"

Generator::Generator()
{

}

Generator::Generator(int seed)
{

}

int Generator::init()
{
	return 1;
}

int Generator::loadConfig(std::ifstream &file)
{
	return 1;
}

int Generator::saveConfig(void)
{
	return 1;
}

Task Generator::nextTask(int id)
{
	Task t = Task();
	t.id = id;
	return t;
}

TaskSet nextTaskSet(int id)
{
	TaskSet tset = TaskSet();
	tset.id = id;
	return tset;
}