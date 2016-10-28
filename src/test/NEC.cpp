#include "NEC.h"

NEC::NEC()
{
	pr = Param();
	init();
}

NEC::NEC(Param paramExt)
{
	pr = paramExt;
	init();
}

int NEC::init()
{
	return 1;
}

bool NEC::passesNecTest(TaskSet ts)
{
	double taskSetLCM = TaskSetUtil::calcTaskLCM(ts);

	for(int j = 0; j < ts.count(); j++) {
		Task baseTask = ts.getTask(j);
		// need only be checked at Dj + kTj
		double interval = baseTask.getDeadline();
		
		while(interval <= taskSetLCM) {
			double sum = 0.0;
			for(int i = 0; i < ts.count(); i++) {
				Task otherTask = ts.getTask(i);
				sum += TaskUtil::calcDemandOverInterval(otherTask, interval);
				// Baker et al. (throw-forwards)
				sum += std::max(0.0, \
					std::remainder((interval - otherTask.getDeadline() + otherTask.getExecTime()) \
						, otherTask.getPeriod()));
			}

			// sum(total demand + throw-forwards) < m * interval
			if(sum > pr.getNProc() * interval) {
				return false;
			}

			interval += baseTask.getPeriod();
		}
	}

	return true;
}