#include "BCLExperiment.h"

BCLExperiment::BCLExperiment() : Experiment()
{
	
}

int BCLExperiment::set()
{
	sg = SimpleGenerator(pr.getSeed());

	return 1;
}

int BCLExperiment::run()
{
	TaskSet ts = sg.nextTaskSet();
	// apply tests
	GFB gfb = GFB(pr);
	schedulable = gfb.isSchedulable(ts);

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