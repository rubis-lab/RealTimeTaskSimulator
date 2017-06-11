#include "JHAhnExperiment.h"

JHAhnExperiment::JHAhnExperiment() : Experiment()
{
}

JHAhnExperiment::~JHAhnExperiment()
{
}

int JHAhnExperiment::set()
{
  gen = JHAhnGenerator(pr, cr);
  gedf = GEDF(pr);

	return 1;
}

int JHAhnExperiment::run()
{
  TaskSet ts = gen.generateTaskSet();

  schedulable = gedf.isSchedulable(ts);
	
	return 1;
}

int JHAhnExperiment::output()
{
  if (schedulable) {
    std::cout << "Schedulable" << std::endl;
  } else {
    std::cout << "not Schedulable" << std::endl;
  }

  return 1;
}
