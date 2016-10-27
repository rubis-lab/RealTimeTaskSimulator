#include "Experiment.h"

Experiment::Experiment()
{
	pr = Param();
	init();
}

Experiment::Experiment(std::ifstream &file)
{
	pr = Param(file);
	init();
}

int Experiment::init()
{
	return 1;
}

int Experiment::run()
{
	// generate task
	SimpleGenerator sg = SimpleGenerator(1000);
	TaskSet ts = sg.nextTaskSet();
	// apply tests
	GFB gfb = GFB(pr);
	bool ret = gfb.isSchedulable(ts);
	// output
	if(ret) {
		std::cout << "Schedulable" << std::endl;
	} else {
		std::cout << "not Schedulable" << std::endl;
	}
	
	return 1;
}

int Experiment::output(void)
{
	return 1;
}
