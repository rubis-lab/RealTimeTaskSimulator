
#include "JHAhnGenerator.h"

JHAhnGenerator::JHAhnGenerator() : Generator()
{
  std::ifstream file;
  file.open("cfg/jhahn/gen.cfg");
  init(file);
  file.close();
}

JHAhnGenerator::JHAhnGenerator(Param *paramExt, CRand *crExt) : Generator(paramExt, crExt)
{
  std::ifstream file;
  file.open("cfg/jhahn/gen.cfg");
  init(file);
  file.close();
}

int JHAhnGenerator::init(std::ifstream &file) {
  FileIO::goToLine(file, 4);

  std::string buf;
  file >> buf;
  file >> numTask;
  file >> buf;
  file >> minPeriod;
  file >> buf;
  file >> maxPeriod;
  file >> buf;
  file >> minExecTime;
  file >> buf;
  file >> maxExecTime;

  std::cout << numTask << " minPeriod: " << minPeriod << " maxPeriod: " << maxPeriod << " minExec: " << minExecTime << " maxExec: " << maxExecTime << std::endl;

  return 1;
}

int JHAhnGenerator::init(int taskSetSize)
{
  this->numTask = taskSetSize;
  this->minPeriod = 3;
  this->maxPeriod = 10;
  this->minExecTime = 1;
  this->maxExecTime = 5;
  return 1;
}



Task JHAhnGenerator::generateTask()
{
  Task t = Task();
  t.setExecTime(floor(cr->uniform(minExecTime, maxExecTime)));
  t.setPeriod(floor(cr->uniform(t.getExecTime(), maxPeriod)));
  t.setDeadline(t.getPeriod());

  return t;
}


TaskSet JHAhnGenerator::generateTaskSet()
{
  TaskSet tset = TaskSet();
  for(int i = 0; i < numTask; i++) {
    Task t = generateTask();
    tset.pushBack(t);
  }

  return tset;
}
