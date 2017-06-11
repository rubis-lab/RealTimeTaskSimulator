#include "GEDF.h"
GEDF::GEDF()
{
  coreCount = 2;
  pr = new Param();
}

GEDF::GEDF(Param *paramExt)
{
  coreCount = 2;
  pr = paramExt;
}

bool GEDF::isSchedulable(TaskSet ts)
{

  TaskSetUtil::printTaskSet(ts);

  std::vector<PeriodCounter> periodCounters;
  long hyperPeriod = 1;
  int index = 0;
  for(auto it = ts.getVector().begin(); it != ts.getVector().end(); it++) {
    hyperPeriod *= it->getPeriod();

    PeriodCounter newCounter(index);
    newCounter.period = 0;

    periodCounters.push_back(newCounter);
    index++;
  }

  std::cout << "hyperPeriod : " << hyperPeriod << std::endl;
  printPeriodCounters(periodCounters);

  std::vector<Task*> processingTasks;

  for(int i = 0; i < hyperPeriod; i++)
  {
    printPeriodCounters(periodCounters);

    for(auto it = processingTasks.begin(); it != processingTasks.end(); it++) {
      if ((*it)->getDeadline() < (*it)->getExecTime()) {
        std::cout << "ExecTime exceeds Deadline for Task: " << (*it)->getID() << std::endl;
        return false;
      }
    }
    
    //add new tasks
    for(auto it = periodCounters.begin();  it != periodCounters.end(); it++) {

      if (it->period != 0) {
        continue;
      }

      std::vector<Task> tasks = ts.getVector();
      Task* newTask = trySpawnTask(*it, tasks);

      //if there's any task not finished till it's period arrives, test fails
      auto finder = [it](Task* t) {
        return t->getID() == it->index;
      };

      auto found = std::find_if(processingTasks.begin(), processingTasks.end(), finder);
      if (found != processingTasks.end()) {
        std::cout << "Period for unfinished task " << it->index << " arrived." << std::endl;
        return false;
      }

      processingTasks.push_back(newTask);

      it->period = tasks.at(it->index).getPeriod();
    }

    printProcessingTasks(processingTasks);

    auto sorter = [](Task* a, Task* b)  {
      return a->getDeadline() < b->getDeadline();
    };

    //sort processing tasks
    std::sort(processingTasks.begin(), processingTasks.end(), sorter);

    std::cout << "==========sorted===========" << std::endl;
    printProcessingTasks(processingTasks);

    //process tasks
    int capacity = coreCount;
    std::cout << "capacity : " << capacity << std::endl;
    for(auto it = processingTasks.begin(); it != processingTasks.end(); it++) {
      processTask(*it, capacity > 0);
      capacity--;
    }


    std::cout << "==========processed===========" << std::endl;
    printProcessingTasks(processingTasks);


    //remove finished tasks
    for(auto it = processingTasks.begin(); it != processingTasks.end();) {
      if ((*it)->getExecTime() == 0) {
        delete *it;
        it = processingTasks.erase(it);
      } else {
        it++;
      }
    }

    //decrement periodCounters
    for(auto it = periodCounters.begin(); it != periodCounters.end(); it++) {
      it->period--;

    }
  }

  if (processingTasks.size() == 0) {
    std::cout << "Test Passed!" << std::endl;
    return true;
  } 

  std::cout << processingTasks.size() << " tasks remains not finished." << std::endl;

  return false;
}


Task* GEDF::trySpawnTask(PeriodCounter& periodCounter, std::vector<Task>& tasks)  {
  Task *newTask = NULL;
  if (periodCounter.period == 0) {
    Task copyFrom = tasks.at(periodCounter.index);

    newTask = new Task(
        periodCounter.index,
        copyFrom.getExecTime(),
        copyFrom.getDeadline(),
        copyFrom.getPeriod()
        );

    periodCounter.period = copyFrom.getPeriod();
  }

  return newTask;
}

void GEDF::processTask(Task* task, bool coreAllocated) {
  if (coreAllocated) {
    task->setExecTime(task->getExecTime() - 1);
  }
  task->setDeadline(task->getDeadline() - 1);
}


PeriodCounter::PeriodCounter(int index) {
  this->period = 0;
  this->index = index;
}

void GEDF::printPeriodCounters(std::vector<PeriodCounter>& counters) 
{
  std::cout << "==========periodcounters===========" << std::endl;
  for(auto it = counters.begin(); it != counters.end(); it++) {
    std::cout << "Counter index: " << it->index << " period: " << it->period << std::endl;
  }
}

void GEDF::printProcessingTasks(std::vector<Task*>& tasks) {
  std::cout << "==========processing tasks===========" << std::endl;
  for(auto it = tasks.begin(); it != tasks.end(); it++) {
    TaskUtil::printTask(**it);
  }
}
