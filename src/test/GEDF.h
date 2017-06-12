#ifndef __GEDF__
#define __GEDF__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskSetUtil.h"
#include "../ops/TaskUtil.h"
#include "../container/Param.h"
#include <algorithm>

struct PeriodCounter {

  public:
    PeriodCounter(int index);

    int index;
    int period;
};

struct Processor {

  public:
    Processor();

    int capacity;
    double current_capacity;
};


class GEDF
{
  private:
    Param *pr;

    int coreCount;
    int paralCount;
    double paralOverhead;

    std::vector<Task*> trySpawnTask(PeriodCounter& periodCounter, std::vector<Task>& tasks);
    void processTask(Task* task, Processor* p);
    void printPeriodCounters(std::vector<PeriodCounter>& counters);
    void printProcessingTasks(std::vector<Task*>& processingTasks);

  public:
    GEDF();
    GEDF(Param *paramExt);
    bool isSchedulable(TaskSet ts);
};


#endif
