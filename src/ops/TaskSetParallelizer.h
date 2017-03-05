#ifndef __TSPARALLEL__
#define __TSPARALLEL__

#include "../container/Task.h"
#include "../container/Thread.h"
#include "../container/TaskSet.h"
#include "../../tools/FileIO.h"
#include "../ops/TaskParallelizer.h"
#include <vector>
#include <cmath>

class TaskSetParallelizer
{
private:
	double optMin;
	double optMax;
	double parallelOverhead;
	double parallelVariance;
	Param* pr;
	CRand* cr;
	TaskParallelizer* tp;
	int loadEnvironment(std::ifstream &file);
	int init();
public:
	TaskSetParallelizer();
	TaskSetParallelizer(Param* paramExt, CRand* crExt);
	//TaskSetParallelizer();
	~TaskSetParallelizer();
	int printInfo(std::ofstream &outFile);
	/*
	TaskSet parallelizeIntoOption(TaskSet baseTaskSet, int option);
	TaskSet parallelizeIntoOption(TaskSet baseTaskSet, int option, double overhead, double variance);
	TaskSet parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax);
	TaskSet parallelizeIntoRandomOption(TaskSet baseTaskSet, int optionMin, int optionMax, double overhead, double variance);
	*/
	TaskSet parallelizeIntoRandomOption(TaskSet baseTaskSet);
	
};
#endif