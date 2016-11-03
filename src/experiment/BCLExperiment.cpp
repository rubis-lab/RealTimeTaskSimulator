#include "BCLExperiment.h"

BCLExperiment::BCLExperiment() : Experiment()
{

}

int BCLExperiment::set()
{
	mg = new MarcoGenerator(pr);
	bcl = new BCL(pr);
	return 1;
}

int BCLExperiment::run()
{
	TaskSet ts = mg->nextTaskSet();
	TaskSetUtil::printTaskSet(ts);

	schedulable = bcl->isSchedulable(ts);
	return 1;
}

int BCLExperiment::output()
{
	if(schedulable) {
		std::cout << "Schedulable" << std::endl;
	} else {
		std::cout << "not Schedulable" << std::endl;
	}
	return 1;
}