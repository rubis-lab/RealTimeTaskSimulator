#ifndef __BARMOD__
#define __BARMOD__

#include "../algorithm/Knapsack.h"
#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../container/Param.h"
#include "../ops/TaskUtil.h"
#include "../ops/TaskSetUtil.h"
#include "../../tools/PMath.h"
#include "../test/BAR.h"
#include <cmath>
#include <vector>
#include <iostream>

class BARMod : public BAR
{
private:
	Param *pr;
	std::vector<Task> CITask;
	// calculation intermediate
	
	std::vector<bool> threadClassification;
	std::vector<int> packageFalseWeight;
	std::vector<int> packageTrueWeight;
	std::vector<double> packageCost;
	
	int classifyThreads(TaskSet &ts, int baseTaskIndex, double extendedInterval);
	int packageThreads(TaskSet &ts);
	int calculatePackageCost(TaskSet &ts);
	double doKnapsack(TaskSet &ts);
	double doKnapsackPrint(TaskSet &ts);
	int debugPrint(TaskSet &ts);
	double calcSumIDiff(TaskSet &ts, int baseTaskIndex, double extendedInterval);

public:
	BARMod();
	BARMod(Param *paramExt);
	~BARMod();
	
	bool isSchedulable(TaskSet &ts);
};
#endif