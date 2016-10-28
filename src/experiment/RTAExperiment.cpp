#include "RTAExperiment.h"

RTAExperiment::RTAExperiment() : Experiment()
{
	
}

int RTAExperiment::testRun()
{
	// generate task
	SimpleGenerator sg = SimpleGenerator();
	TaskSet ts = sg.nextTaskSet();
	// apply tests
	BCL bcl = BCL(pr);
	bool ret = bcl.isSchedulable(ts);
	// output
	if(ret) {
		std::cout << "Schedulable" << std::endl;
	} else {
		std::cout << "not Schedulable" << std::endl;
	}
	return 1;
}