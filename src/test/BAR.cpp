#include "BAR.h"

BAR::BAR()
{
	
}

BAR::BAR(Param *paramExt)
{
	pr = paramExt;
}

BAR::~BAR()
{

}

std::vector<double> BAR::getKMaxInterferingExecTime(TaskSet &ts, int k)
{
	std::vector<double> execTimes;
	for(int i = 0; i < ts.count(); i++) {
		execTimes.push_back(ts.getTask(i).getExecTime());
	}
	std::vector<double> ret = PMath::kMax(execTimes, k);

	return ret;
}

std::vector<double> BAR::calcExtendedIntervalBound(TaskSet &ts)
{
	std::vector<double> extendedIntervalList;
	std::vector<double> maxExecTime = getKMaxInterferingExecTime(ts, pr->getNProc() - 1);
	/*
	std::cout << "maxExecTime ";
	for(unsigned int i = 0; i < maxExecTime.size(); i++) {
		std::cout << maxExecTime[i] << " ";
	}
	std::cout << std::endl;
	*/
	// AkBase = [Csum + sum {(Ti - Di) * Ui}]

	// Csum
	double csum = 0.0;
	for(unsigned int i = 0; i < maxExecTime.size(); i++) {
		csum += maxExecTime[i];
	}
	//std::cout << "csum " << csum << std::endl;

	double AkBase = csum;
	// sum {(Ti - Di) * Ui}
	for(int i = 0; i < ts.count(); i++) {
		Task t = ts.getTask(i);
		AkBase += (t.getPeriod() - t.getDeadline()) * TaskUtil::calcUtilization(t);
	}
	//std::cout << "akbase " << AkBase << std::endl;

	// utot 
	double utot = TaskSetUtil::sumUtilization(ts);
	//std::cout << "utot " << utot << std::endl;

	// => Ak = [AkBase + Dk * Utot - m * Dk + m * Ck ] / [m - Utot]
	for(int i = 0; i < ts.count(); i++) {
		Task t = ts.getTask(i);
		double nProc = pr->getNProc();
		double AkRemainder = t.getDeadline() * (utot - nProc) +  nProc * t.getExecTime();
		//std::cout << "AkRemainder " << AkRemainder << std::endl;
		AkRemainder = (AkBase + AkRemainder) / (nProc - utot);
		extendedIntervalList.push_back(AkRemainder);
	}

	return extendedIntervalList;
}

double BAR::calcNCInterference(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval)
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	double dbf = TaskUtil::calcDemandOverInterval(interTask, extendedInterval + baseTask.getDeadline());
	double ret = 0.0;
	// case i != k
	if(baseTaskIndex != interTaskIndex) {
		// min(DBF(ti, ak + dk), ak + dk - ck)
		ret = std::min(dbf, extendedInterval + baseTask.getDeadline() - baseTask.getExecTime());

	// case i == k
	} else {
		// min(DBF(ti, ak + dk) - ck, ak)	(i == k)
		ret = std::min(dbf - baseTask.getExecTime(), extendedInterval);
	}

	return ret;
}

double BAR::calcCarryIn(TaskSet &ts, int baseTaskIndex, int interTaskIndex, double extendedInterval)
{
	Task baseTask = ts.getTask(baseTaskIndex);
	Task interTask = ts.getTask(interTaskIndex);

	// case i != k
	double ret = 0.0;
	if(baseTaskIndex != interTaskIndex) {
		// min(Ci, t mod Ti)
		ret = std::remainder(interTask.getExecTime(), extendedInterval + baseTask.getDeadline());
		ret = std::min(ret, interTask.getExecTime());

	// case i == k
	} else {
		ret = 0.0;
	}

	return ret;
}

bool BAR::isSchedulable(TaskSet &ts)
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
			std::vector<double> iNC;
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// non carry-in
				iNC.push_back(calcNCInterference(ts, baseTaskIndex, interTaskIndex, extInterval));
			}

			std::vector<double> iCI;
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// carry-in
				iCI.push_back(calcCarryIn(ts, baseTaskIndex, interTaskIndex, extInterval));
			}
			
			// find m-1 largest carry-in
			std::vector<double> iKMaxCI = PMath::kMax(iCI, pr->getNProc() - 1);

			// Sum I
			double isum = 0.0;
			for(unsigned int i = 0; i < iNC.size(); i++) {
				isum += iNC[i];
			}
			for(unsigned int i = 0; i < iKMaxCI.size(); i++) {
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