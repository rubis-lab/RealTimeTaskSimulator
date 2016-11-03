#ifndef __GFB__
#define __GFB__

#include "../container/Task.h"
#include "../container/TaskSet.h"
#include "../ops/TaskSetUtil.h"
#include "../container/Param.h"

class GFB
{
private:
	Param *pr;
public:
	GFB();
	GFB(Param *paramExt);
	bool isSchedulable(TaskSet ts);
};
#endif