#include "BAR.h"

BAR::BAR()
{
	pr = new Param();
}

BAR::BAR(Param *paramExt)
{
	pr = paramExt;
}

std::vector<double> BAR::getKMaxInterferingExecTime(TaskSet &ts, int k, int baseTaskIndex)
{
	std::vector<double> execTimes;
	for(int i = 0; i < ts.count(); i++) {
		if(i != baseTaskIndex) {
			execTimes.push_back(ts.getTask(i).getExecTime());
		}
	}
	std::vector<double> ret = PMath::kMax(execTimes, k);

	return ret;
}

double double BAR::calcExtendedIntervalBound(TaskSet &ts, int baseTaskIndex)
{
	// m - 1 largest Ci's
	std::vector<double> maxExecTime = getKMaxInterferingExecTime(ts, pr->getNProc() - 1, baseTaskIndex);

	// Csum
	double csum = 0.0;
	for(int i = 0; i < maxExecTime.size(); i++) {
		csum += maxExecTime[i];
	}

	// Utot
	double utot = TaskSetUtil::sumUtilization(ts);

	// Ak = [Csum - Dk(m - Utot) + sum {(Ti - Di) * Ui} + m * Ck ] / [m - Utot]
	Task baseTask = ts.getTask(baseTaskIndex);
	// Csum - Dk(m - Utot)
	double extBound = csum - baseTask.getDeadline() * (pr->getNProc() - utot);
	
	// sum {(Ti - Di) * Ui}
	for(int i = 0; i < ts.count(); i++) {
		if(i != baseTaskIndex) {
			Task interTask = ts.getTask(i);
			extBound += (interTask.getPeriod() - interTask.getDeadline()) / TaskUtil::calcUtilization(interTask);	
		}
	}

	// m * Ck
	extBound += pr->getNProc() * baseTask.getExecTime();

	// m - Utot
	extBound = extBound / (pr->getNProc() - utot);

	return extBound;
}

double BAR::calcNCInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval);
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	double dbf = TaskUtil::calcDemandOverInterval(interTask, extendedInterval + baseTask.getDeadline());

	// case i != k
	if(baseTaskIndex != interTaskIndex) {
		// min(DBF(ti, ak + dk), ak + dk - ck)
		double ret = std::min(dbf, extendedInterval + baseTask.getDeadline() - baseTask.getExecTime());

	// case i == k
	} else {
		// min(DBF(ti, ak + dk) - ck, ak)	(i == k)
		double ret = std::min(dbf - baseTask.getExecTime(), extendedInterval);
	}

	return ret;
}

double BAR::calcCarryIn(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval)
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	// case i != k
	if(baseTaskIndex != interTaskIndex) {
		// min(Ci, t mod Ti)
		double ret = std::remainder(interTask.getExecTime(), extendedInterval + baseTask.getDeadline());
		ret = std::min(ret, interTask.getExecTime());

	// case i == k
	} else {
		double ret = 0.0;
	}

	return ret;
}

bool BAR::isSchedulable(TaskSet &ts)
{
	for(int baseTaskIndex = 0; baseTaskIndex < ts.count(); baseTaskIndex++) {
		// Ak bound
		double extIntervalBound = calcExtendedIntervalBound(ts, baseTaskIndex);
		// RHS m(Ak + Dk - Ck)
		double rhs = ts.getTask(baseTaskIndex).getDeadline() - ts.getTask(baseTaskIndex).getExecTime();

		// iterate with Ak
		double extInterval = 0.0;
		while(extInterval < extIntervalBound) {
			std::vector<double> iNC;
			std::vector<double> iCI;
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// non carry-in
				iNC.push_back(calcNCInterference(ts, baseTaskIndex, interTaskIndex, extInterval));
				// carry-in
				iCI.push_back(calcCarryIn(ts, baseTaskIndex, interTaskIndex, extInterval));
			}
			
			// find m-1 largest carry-in
			std::vector<double> iKMaxCI = PMath::kMax(iCI, pr->getNProc() - 1);

			// Sum I
			double isum = 0.0;
			for(int i = 0; i < iNC.size(); i++) {
				isum += iNC[i];
			}
			for(int i = 0; i < iKMaxCI.size(); i++) {
				isum += iKMaxCI[i];
			}

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