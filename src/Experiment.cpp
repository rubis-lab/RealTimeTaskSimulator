#include "Experiment.h"

Experiment::Experiment(void)
{
	ts = TaskSet();
}

int Experiment::run(void)
{
	std::cout << "running #" << "1" << std::endl;
	std::ifstream file;
	file.open("data/sample_task.in");
	if(file.is_open()) {
		ts.readTaskSet(file);
		ts.printTaskSet();
		file.close();
	} else {
		std::cout << "no input file" << std::endl;
	}
	return 1;
}

int Experiment::output(void)
{
	return 1;
}