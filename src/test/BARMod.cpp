#include "BARMod.h"

BARMod::BARMod()
{
	
}

BARMod::BARMod(Param *paramExt)
{
	pr = paramExt;
}

BARMod::~BARMod()
{

}

int BARMod::classifyThreads(TaskSet &ts, double extendedInterval)
{
	threadClassification.clear();

	for(int i = 0; i < ts.count(); i++) {
		Task baseTask = ts.getTask(i);
		double leftOverInterval = std::remainder(extendedInterval, baseTask.getPeriod());
		if(iCI[i] < leftOverInterval) {
			// CI impossible thread
			threadClassification.push_back(false);
		} else {
			// CI possible thread
			threadClassification.push_back(true);
		}
	}

	return 1;
}

int BARMod::packageThreads(TaskSet &ts)
{
	packageFalseWeight.clear();
	packageTrueWeight.clear();

	int currID = 0;
	for(int i = 0; i < ts.count(); i++) {
		// task-wise iteration
		int cnt = 0;
		while(ts.getTask(i).getID() == currID) {
			// is CI able task
			if(threadClassification[i]) {
				// pop last one, put 0 inside to distinguish
				if(cnt >= 1) {
					packageFalseWeight.pop_back();
					packageFalseWeight.push_back(0);
					packageTrueWeight.pop_back();
					packageTrueWeight.push_back(0);
				}
				cnt++;
				// last one following 0 is packaged weight.
				packageFalseWeight.push_back(cnt);
				packageTrueWeight.push_back(cnt);
			} else {
				// CI not possible, cnt + 1 is the false weight
				packageFalseWeight.push_back(cnt + 1);
				packageTrueWeight.push_back(1);
			}
			i++;
		}
		cnt = 0;
		i--;
		currID++;
	}
	// debug
	std::cout<<"package count: "<<packageFalseWeight.size()<<std::endl;

	return 1;
}

int BARMod::calculatePackageCost(TaskSet &ts)
{
	packageCost.clear();

	double tmp = 0.0;
	for(int i = 0; i < ts.count(); i++) {
		// add up until not zero.
		while(packageTrueWeight[i] == 0) {
			// push in 0.0 to distinguish
			tmp += ts.getTask(i).getExecTime();
			packageCost.push_back(0.0);
			i++;
		}
		tmp += ts.getTask(i).getExecTime();
		packageCost.push_back(tmp);
		tmp = 0.0;
	}
	return 1;
}

double BARMod::calculateIDiff(TaskSet &ts, double extendedInterval)
{
	classifyThreads(ts, extendedInterval);
	packageThreads(ts);
	calculatePackageCost(ts);
	return Knapsack::knapsackFalseWeight(pr->getNProc() - 1, packageFalseWeight, packageTrueWeight, packageCost);
}

bool BARMod::isSchedulable(TaskSet &ts)
{
	// Ak bound
	std::vector<double> extIntervalBoundList = calcExtendedIntervalBound(ts);

	for(int baseTaskIndex = 0; baseTaskIndex < ts.count(); baseTaskIndex++) {
		
		//std::cout << "extIntervalBoundList : " << extIntervalBoundList[baseTaskIndex] << std::endl;
		// Ak <= 0, don't need to check
		if(extIntervalBoundList[baseTaskIndex] <= 0.0) {
			continue;
		}
		// RHS m(Ak + Dk - Ck)
		double rhs = ts.getTask(baseTaskIndex).getDeadline() - ts.getTask(baseTaskIndex).getExecTime();
		//std::cout << "rhs : " << rhs << std::endl;
		// iterate with Ak
		double extInterval = 0.0;
		while(extInterval < extIntervalBoundList[baseTaskIndex]) {
			iNC.clear();
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// non carry-in
				iNC.push_back(calcNCInterference(ts, baseTaskIndex, interTaskIndex, extInterval));
			}

			iCI.clear();
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// carry-in
				iCI.push_back(calcCarryIn(ts, baseTaskIndex, interTaskIndex, extInterval));
			}
			
			// Sum I
			double isum = 0.0;
			for(unsigned int i = 0; i < iNC.size(); i++) {
				isum += iNC[i];
			}
			isum += calculateIDiff(ts, extInterval);

			// unschedule condition
			if(isum > pr->getNProc() * (rhs + extInterval)) {
				return false;
			}

			// next A (for now)
			// can be checked everytime DBF changes
			extInterval += 1.0;
		}
	}
	
	return true;
}