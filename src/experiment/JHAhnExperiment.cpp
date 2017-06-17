#include "JHAhnExperiment.h"

JHAhnExperiment::JHAhnExperiment() : Experiment()
{
  std::ifstream file;
  file.open("cfg/jhahn/exp.cfg");
  init(file);
  file.close();
}

JHAhnExperiment::~JHAhnExperiment()
{
}

void JHAhnExperiment::init(std::ifstream &file)
{
  loadEnvironment(file);
  set();
}

int JHAhnExperiment::loadEnvironment(std::ifstream &file) {
  FileIO::goToLine(file, 4);

  std::string buf;
  file >> buf;
  file >> expName;

  return 1;
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

  gedf.printResult(ts);

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
