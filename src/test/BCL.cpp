#include "BCL.h"

BCL::BCL()
{
	pr = new Param();
}

BCL::BCL(Param *paramExt)
{
	pr = paramExt;
}

int BCL::reset()
{
	slack.clear();
	return 1;
}

double BCL::calcInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex)
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	double nInterfereTask = std::floor(baseTask.getDeadline() / interTask.getPeriod());

	double carryIn = (std::fmod(baseTask.getDeadline(), interTask.getPeriod()) - slack[interTaskIndex]);
	if(carryIn < 0.0) { 
		carryIn = 0.0;
	}

	double jk = interTask.getExecTime() * nInterfereTask + std::min(interTask.getExecTime(), carryIn);

	return std::min(jk, baseTask.getDeadline() - baseTask.getExecTime() + 1.0);
}

bool BCL::isSchedulable(TaskSet &ts)
{
	reset();

	// init slack
	for(int i = 0; i < ts.count(); i++) {
		slack.push_back(0.0);
	}

	bool isFeasible = false;
	bool updated = true;

	// terminate when not feasible & not updated
	while(updated) {
		isFeasible = true;
		updated = false;

		// check each task's feasibility
		for(int baseTaskIndex = 0; baseTaskIndex < ts.count(); baseTaskIndex++) {
			Task baseTask = ts.getTask(baseTaskIndex);

			// add up all demand from interfering tasks
			double sumJ = 0.0;
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				if(interTaskIndex == baseTaskIndex)
					continue;
				sumJ += calcInterference(ts, baseTaskIndex, interTaskIndex);
			}
			sumJ = std::floor(sumJ / pr->getNProc());

			double slackTmp = baseTask.getDeadline() - baseTask.getExecTime() - sumJ;

			// slack < 0 --> infeasible
			if(slackTmp < 0.0) {
				isFeasible = false;
			// slack updated
			} else if (slackTmp > slack[baseTaskIndex]) {
				slack[baseTaskIndex] = slackTmp;
				updated = true;
			}
		}

		// taskset feasible
		if(isFeasible) {
			return isFeasible;
		}
	}

	return false;
}