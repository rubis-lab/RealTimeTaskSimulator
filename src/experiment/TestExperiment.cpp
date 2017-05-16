#include "TestExperiment.h"

TestExperiment::TestExperiment() : Experiment()
{
	init();
}

TestExperiment::~TestExperiment()
{
}

int TestExperiment::init()
{
	bar = BAR(pr);
	barMod = BARMod(pr);
	return 1;
}

int TestExperiment::run()
{
	// create task set
	TaskSet ts = TaskSet();
	Task t0 = Task(0, 5, 7, 7);
	ts.pushBack(t0);
	Task t1 = Task(0, 5, 7, 7);
	ts.pushBack(t1);
	Task t2 = Task(1, 2, 4, 7);
	ts.pushBack(t2);

	TaskSetUtil::printTaskInfo(ts);
	TaskSetUtil::printTaskSet(ts);

	// apply test
	__LINE

	std::cout<<bar.isSchedulable(ts)<<std::endl;
	bar.isSchedulablePrint(ts);
	barMod.isSchedulablePrint(ts);

	__LINE

	std::cout<<bar.isSchedulable(ts)<<std::endl;
	std::cout<<barMod.isSchedulable(ts)<<std::endl;

	return 1;
}