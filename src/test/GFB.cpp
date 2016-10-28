#include "GFB.h"

GFB::GFB(Param pr)
{
	nProc = pr.getNProc();
}

bool GFB::isSchedulable(TaskSet ts)
{
	bool ret = false;

	double lmdTot = TaskSetUtil::sumUtilization(ts);
	double lmdMax = TaskSetUtil::getMaxUtilization(ts);

	ret = (lmdTot <= nProc * (1.0 - lmdMax) + lmdMax);
	return ret;
}