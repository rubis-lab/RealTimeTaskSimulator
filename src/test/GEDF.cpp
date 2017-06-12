#include "GEDF.h"
GEDF::GEDF()
{
  coreCount = 2;
  pr = new Param();
}

GEDF::GEDF(Param *paramExt)
{
  coreCount = 4;
  paralCount = 2;
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

  std::vector<Processor> processors;
  for(int i = 0; i < coreCount; i++) {
    Processor newp;
    processors.push_back(newp);
  }

  std::vector<Task*> processingTasks;

  for(int i = 0; i < hyperPeriod; i++)
  {
    printPeriodCounters(periodCounters);

    //if exectime exceeds deadline, fails
    for(auto it = processingTasks.begin(); it != processingTasks.end(); it++) {
      if ((*it)->getDeadline() < (*it)->getExecTime()) {
        std::cout << "ExecTime exceeds Deadline for Task: " << (*it)->getID() << std::endl;
        return false;
      }
    }

    //refuels processor 
    for(auto it = processors.begin(); it != processors.end(); it++) {
      it->current_capacity = it->capacity;
    }

    //add new tasks
    for(auto it = periodCounters.begin();  it != periodCounters.end(); it++) {

      if (it->period != 0) {
        continue;
      }


      //if there's any task not finished till it's period arrives, test fails
      auto finder = [it](Task* t) {
        return t->getID() == it->index;
      };

      auto found = std::find_if(processingTasks.begin(), processingTasks.end(), finder);
      if (found != processingTasks.end()) {
        std::cout << "Period for unfinished task " << it->index << " arrived." << std::endl;
        return false;
      }

      std::vector<Task> tasks = ts.getVector();
      std::vector<Task*> newTask = trySpawnTask(*it, tasks);

      for(auto t = newTask.begin(); t != newTask.end(); t++) {
        processingTasks.push_back(*t);
      }

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
    unsigned int taskIndex = 0;
    bool processedAll = false;
    auto finder = [](Processor p) { return p.current_capacity > 0; };
    auto idleProcessor = std::find_if(processors.begin(), processors.end(), finder);
    while(idleProcessor != processors.end() && !processedAll) {
      for(auto it = processors.begin(); it != processors.end(); it++) {
        if (taskIndex == processingTasks.size()) {
          processedAll = true;
          break;
        }

        if (it->current_capacity > 0) {
          processTask(processingTasks.at(taskIndex), &*it);
          taskIndex++;
        }

      }
      idleProcessor = std::find_if(processors.begin(), processors.end(), finder);
    }

    for(auto it = processingTasks.begin(); it != processingTasks.end(); it++) {
      (*it)->setDeadline((*it)->getDeadline() - 1);
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


std::vector<Task*> GEDF::trySpawnTask(PeriodCounter& periodCounter, std::vector<Task>& tasks)  {
  std::vector<Task*> newTasks;
  if (periodCounter.period == 0) {
    Task copyFrom = tasks.at(periodCounter.index);

    for(int i = 0; i < paralCount; i++) {
      Task* newTask = new Task(
          periodCounter.index,
          (copyFrom.getExecTime() / paralCount) + paralOverhead,
          copyFrom.getDeadline(),
          copyFrom.getPeriod()
          );

      newTasks.push_back(newTask);

    }


    periodCounter.period = copyFrom.getPeriod();
  }

  return newTasks;
}

void GEDF::processTask(Task* task, Processor* p) {
  double processingUnit = std::min(task->getExecTime(), p->current_capacity);
  p->current_capacity -= processingUnit;
  task->setExecTime(task->getExecTime() - processingUnit);
}


PeriodCounter::PeriodCounter(int index) {
  this->period = 0;
  this->index = index;
}

Processor::Processor() {
  this->capacity = 1;
  this->current_capacity = 0;
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
