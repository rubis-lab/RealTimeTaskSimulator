#include "SimpleExperiment.h"

SimpleExperiment::SimpleExperiment() : Experiment()
{
	
}

int SimpleExperiment::set()
{
	sg = SimpleGenerator(pr.getSeed());

	return 1;
}

int SimpleExperiment::run()
{
	TaskSet ts = sg.nextTaskSet();
	// apply tests
	GFB gfb = GFB(pr);
	schedulable = gfb.isSchedulable(ts);

	return 1;
}

int SimpleExperiment::output()
{
	if(schedulable) {
		std::cout << "Schedulable" << std::endl;
	} else {
		std::cout << "not Schedulable" << std::endl;
	}
	return 1;
}