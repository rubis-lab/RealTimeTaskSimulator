#include "GEDF.h"

int lcm(int a, int b);
int gcd(int a, int b);
void printPassed(int arr[16]);
void printPassedForTask(int arr[5][4], int t);


GEDF::GEDF()
{
  pr = new Param();

  std::ifstream file;
  file.open("cfg/jhahn/test.cfg");

  FileIO::goToLine(file, 4);

  std::string buf;
  file >> buf;
  file >> coreCount;
  file >> buf;
  file >> paralCount;
  file >> buf;
  file >> paralOverhead;


  file.close();

}

GEDF::GEDF(Param *paramExt)
{
  std::ifstream file;
  file.open("cfg/jhahn/test.cfg");
  if (file.is_open() == false) {
    std::cout << "can't find input file" << std::endl;
  }


  FileIO::goToLine(file, 4);

  std::string buf;
  file >> buf;
  file >> coreCount;
  file >> buf;
  file >> paralCount;
  file >> buf;
  file >> paralOverhead;


  pr = paramExt;

  std::cout << "gedf setting" << std::endl;
  std::cout << coreCount << " " << paralCount << " " << paralOverhead << std::endl;

  file.close();
}

void GEDF::printResult(TaskSet ts) {

  TaskSetUtil::printTaskSet(ts);

  std::vector<int> paralSetting;
  int totalIteration = 1;

  int passed[16] = { 0 };
  int tPassed[5][4] = { 0 };
  int testCount[16] = { 0 };

  for(int k = 0; k < ts.count(); k++) {
    paralSetting.push_back(totalIteration);
    totalIteration *= paralCount;
  }

  for(int paralLoop = 0; paralLoop < totalIteration; paralLoop++) {

    std::vector<int> paralVector;

    for(int i = 0; i < paralSetting.size(); i++) {
      paralVector.push_back(
          ((paralLoop / paralSetting.at(i)) % paralCount) + 1
          );
    }

    std::cout << "paralVector: ";

    for(int i = 0; i < paralVector.size(); i++) {
      std::cout << paralVector.at(i) << " ";
    }

    std::cout << std::endl;

    bool schedulable = isSchedulable(ts, paralVector);

    int sum = 0;
    for(int i = 0; i < paralVector.size(); i++) {
      int p = paralVector.at(i);
      sum += p;

      tPassed[i][p - 1] += schedulable ? 1 : 0;
    }

    passed[sum - 5] += schedulable ? 1 : 0;
    testCount[sum - 5] += 1;
  }

  printPassedForTask(tPassed, 0);
  printPassedForTask(tPassed, 1);
  printPassedForTask(tPassed, 2);
  printPassedForTask(tPassed, 3);
  printPassedForTask(tPassed, 4);

  printPassed(passed);

  printPassed(testCount);

}

void printPassed(int arr[16]) {
  std::cout << "==== passed for task sum ====" << std::endl;
  for(int i = 0; i < 16; i++) {
    std::cout << i + 5 << "\t" << arr[i] << std::endl;
  }
}


void printPassedForTask(int arr[5][4], int t) {
  std::cout << "==== passed for task " << t << "=====" << std::endl;
  for(int i = 0; i < 4; i++) {
    std::cout << i + 1 << "\t" << arr[t][i] << std::endl;
  }
}

bool GEDF::isSchedulable(TaskSet ts, std::vector<int>& paralVector)
{


  std::vector<PeriodCounter> periodCounters;
  long hyperPeriod = 1;
  int index = 0;
  for(int i = 0; i < ts.count(); i++) {
    Task task = ts.getTask(i);

    hyperPeriod = lcm(hyperPeriod, task.getPeriod());

    PeriodCounter newCounter(index);
    newCounter.period = 0;

    periodCounters.push_back(newCounter);
    index++;
  }

  //std::cout << "hyperPeriod : " << hyperPeriod << std::endl;
  //printPeriodCounters(periodCounters);

  std::vector<Processor> processors;
  for(int i = 0; i < coreCount; i++) {
    Processor newp;
    processors.push_back(newp);
  }

  std::vector<int> paralSetting;


  std::vector<Task*> processingTasks;

  for(int i = 0; i < hyperPeriod; i++)
  {
    //printPeriodCounters(periodCounters);

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
      std::vector<Task*> newTask = trySpawnTask(*it, tasks, paralVector);

      for(auto t = newTask.begin(); t != newTask.end(); t++) {
        processingTasks.push_back(*t);
      }

      it->period = tasks.at(it->index).getPeriod();
    }

    //printProcessingTasks(processingTasks);

    auto sorter = [](Task* a, Task* b)  {
      return a->getDeadline() < b->getDeadline();
    };

    //sort processing tasks
    std::sort(processingTasks.begin(), processingTasks.end(), sorter);

    //std::cout << "==========sorted===========" << std::endl;
    //printProcessingTasks(processingTasks);

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

    //std::cout << "==========processed===========" << std::endl;
    //printProcessingTasks(processingTasks);


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


std::vector<Task*> GEDF::trySpawnTask(PeriodCounter& periodCounter, std::vector<Task>& tasks, std::vector<int>& paralVector)  {
  std::vector<Task*> newTasks;
  if (periodCounter.period == 0) {
    Task copyFrom = tasks.at(periodCounter.index);

    int pCount = paralVector.at(periodCounter.index);

    double totalOverhead = pCount == 1 ? 0 : paralOverhead * pCount;

    int remainingTime = (int)copyFrom.getExecTime() % pCount;


    for(int i = 0; i < pCount; i++) {
      Task* newTask = new Task(
          periodCounter.index,
          (int)(copyFrom.getExecTime() / pCount),
          copyFrom.getDeadline(),
          copyFrom.getPeriod()
          );

      newTasks.push_back(newTask);
    }


    //add overhead and remains

    int extras = totalOverhead + remainingTime;

    while(extras > 0) {
      for(auto it = newTasks.begin(); it != newTasks.end(); it++ ){
        (*it)->setExecTime((*it)->getExecTime() + 1);
        extras--;

        if(extras == 0) {
          break;
        }
      }
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

int gcd(int a, int b)
{
  for (;;)
  {
    if (a == 0) return b;
    b %= a;
    if (b == 0) return a;
    a %= b;
  }
}

int lcm(int a, int b)
{
  int temp = gcd(a, b);

  return temp ? (a / temp * b) : 0;
}
