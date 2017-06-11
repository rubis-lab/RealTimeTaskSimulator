
#include "JHAhnGenerator.h"

JHAhnGenerator::JHAhnGenerator() : Generator()
{
  init(5);
}

JHAhnGenerator::JHAhnGenerator(Param *paramExt, CRand *crExt) : Generator(paramExt, crExt)
{
  init(5);
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
