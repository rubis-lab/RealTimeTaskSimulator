#ifndef __GEDF__
#define __GEDF__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskSetUtil.h"
#include "../ops/TaskUtil.h"
#include "../container/Param.h"

struct PeriodCounter {

  public:
    PeriodCounter(int index);

    int index;
    int period;
};


class GEDF
{
  private:
    Param *pr;

    int coreCount;

    Task* trySpawnTask(PeriodCounter& periodCounter, std::vector<Task>& tasks);
    void processTask(Task* task, bool coreAllocated);
    void printPeriodCounters(std::vector<PeriodCounter>& counters);
    void printProcessingTasks(std::vector<Task*>& processingTasks);

  public:
    GEDF();
    GEDF(Param *paramExt);
    bool isSchedulable(TaskSet ts);
};


#endif
