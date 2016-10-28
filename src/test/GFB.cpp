#include "GFB.h"
GFB::GFB()
{
	pr = Param();
}

GFB::GFB(Param paramExt)
{
	pr = paramExt;
}

bool GFB::isSchedulable(TaskSet ts)
{
	bool ret = false;

	double lmdTot = TaskSetUtil::sumUtilization(ts);
	double lmdMax = TaskSetUtil::getMaxUtilization(ts);

	ret = (lmdTot <= pr.getNProc() * (1.0 - lmdMax) + lmdMax);
	return ret;
}