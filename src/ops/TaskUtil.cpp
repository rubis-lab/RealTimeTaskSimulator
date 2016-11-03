#include "TaskUtil.h"

int TaskUtil::printTask(Task &t)
{
	std::cout<<t.getExecTime()<<" "<<t.getDeadline()<<" "<<t.getPeriod()<<std::endl;
	return 1;
}

double TaskUtil::calcDensity(Task &t)
{
	return t.getExecTime() / t.getDeadline();
}

double TaskUtil::calcUtilization(Task &t)
{
	return t.getExecTime() / t.getPeriod();
}

double TaskUtil::calcDemandOverInterval(Task &t, double interval)
{
	double stepFunc = std::floor((interval - t.getDeadline()) / t.getPeriod());
	return t.getExecTime() * (stepFunc + 1.0);
}