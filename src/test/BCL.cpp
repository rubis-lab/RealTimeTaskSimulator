#include "BCL.h"

BCL::BCL()
{
	pr = Param();
}

BCL::BCL(Param paramExt)
{
	pr = paramExt;
}

double BCL::calcInterference(TaskSet ts, int baseTaskIndex, int interTaskIndex)
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	double nInterfereTask = std::floor(baseTask.getDeadline() / interTask.getPeriod());

	double carryIn = (std::remainder(baseTask.getDeadline(), interTask.getPeriod()) - slack[interTaskIndex]);
	if(carryIn < 0.0) { 
		carryIn = 0.0;
	}

	double jk = interTask.getExecTime() * nInterfereTask + std::min(interTask.getExecTime(), carryIn);

	return std::min(jk, baseTask.getDeadline() - baseTask.getExecTime() + 1.0);
}

bool BCL::isSchedulable(TaskSet ts)
{
	for(int i = 0; i < ts.count(); i++) {
		slack[i] = 0.0;
	}
	bool isFeasible = false;
	bool updated = false;

	while(updated) {
		isFeasible = true;
		updated = false;

		for(int baseTaskIndex = 0; baseTaskIndex < ts.count(); baseTaskIndex++) {
			Task baseTask = ts.getTask(baseTaskIndex);
			double dBase = baseTask.getDeadline();
			double cBase = baseTask.getExecTime();

			double sumJ = 0.0;
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				if(interTaskIndex == baseTaskIndex)
					continue;
				sumJ += calcInterference(ts, baseTaskIndex, interTaskIndex);
			}
			sumJ = std::floor(sumJ / pr.getNProc());

			double sum = dBase - cBase - sumJ;

			if(sum < 0.0) {
				isFeasible = false;
			} else if (sum > slack[baseTaskIndex]) {
				slack[baseTaskIndex] = sum;
				updated = true;
			}
		}
		if(isFeasible) {
			return isFeasible;
		}
	}
	return isFeasible;
}