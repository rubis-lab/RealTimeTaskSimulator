#include "BAR.h"

BAR::BAR()
{
	pr = new Param();
}

BAR::BAR(Param *paramExt)
{
	pr = paramExt;
}

bool BAR::isSchedulable(TaskSet &ts)
{
	return false;
}