#include "TaskSetParallelizer.h"

TaskSetParallelizer::TaskSetParallelizer()
{
	init();
}

TaskSetParallelizer::TaskSetParallelizer(Param* paramExt, CRand* crExt)
{
	pr = paramExt;
	cr = crExt;
	init();
}

TaskSetParallelizer::~TaskSetParallelizer()
{
	delete tp;
}

int TaskSetParallelizer::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> optMin;
	file >> buf;
	file >> optMax;
	file >> buf;
	file >> parallelOverhead;
	file >> buf;
	file >> parallelVariance;

	return 1;
}

int TaskSetParallelizer::init()
{
	std::ifstream file;
	file.open("../cfg/para.cfg");
	loadEnvironment(file);
	file.close();
	
	tp = new TaskParallelizer(pr, cr);

	return 1;
}

int TaskSetParallelizer::printInfo(std::ofstream &outFile)
{
	outFile<<"***Task Set Parallelizer"<<std::endl;
	outFile<<"optMin\t\t"<<optMin<<std::endl;
	outFile<<"optMax\t\t"<<optMax<<std::endl;
	outFile<<"pOverhead\t"<<parallelOverhead<<std::endl;
	outFile<<"pVariance\t"<<parallelVariance<<std::endl;
	outFile<<"----------------"<<std::endl;
	return 1;
}

/*
TaskSet TaskSetParallelizer::parallelizeIntoOption(TaskSet baseTaskSet, int option)
{
	return parallelizeIntoOption(baseTaskSet, option, 0.0, 0.0);
}

TaskSet TaskSetParallelizer::parallelizeIntoOption(TaskSet baseTaskSet, int option, double overhead, double variance)
{
	TaskSet retTaskSet = TaskSet();

	for(int i = 0; i < baseTaskSet.count(); i++) {
		std::vector<Thread> thrs = tp->parallelizeTask(baseTaskSet.getTask(i), option, overhead, variance);
		for(unsigned int j = 0; j < thrs.size(); j++) {
			retTaskSet.pushBack(thrs[j]);
		}
	}
	
	return retTaskSet;
}
TaskSet TaskSetParallelizer::parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax)
{
	return parallelizeIntoRandomOption(baseTaskSet, optionMin, optionMax, 0.0, 0.0);
}

TaskSet TaskSetParallelizer::parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax, double overhead, double variance)
{
	TaskSet retTaskSet = TaskSet();

	for(int i = 0; i < baseTaskSet.count(); i++) {
		int option = (int)std::round(cr->uniform(optionMin - 0.49, optionMax + 0.49));
		std::vector<Thread> thrs = tp->parallelizeTask(baseTaskSet.getTask(i), option, overhead, variance);
		for(unsigned int j = 0; j < thrs.size(); j++) {
			retTaskSet.pushBack(thrs[j]);
		}
	}
	
	return retTaskSet;
}
*/
TaskSet TaskSetParallelizer::parallelizeIntoRandomOption(TaskSet baseTaskSet)
{
	TaskSet retTaskSet = TaskSet();
	for(int i = 0; i < baseTaskSet.count(); i++) {
		int option = (int)std::round(cr->uniform(optMin - 0.49, optMax + 0.49));
		std::vector<Thread> thrs = tp->parallelizeTask(baseTaskSet.getTask(i), option, parallelOverhead, parallelVariance);
		for(unsigned int j = 0; j < thrs.size(); j++) {
			retTaskSet.pushBack(thrs[j]);
		}
	}
	return retTaskSet;
}