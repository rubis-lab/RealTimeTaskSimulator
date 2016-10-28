#ifndef __TMOD__
#define __TMOD__

#include "../container/Task.h"
#include <vector>
#include <cmath>

namespace TaskModifier
{
	std::vector<Task> splitTaskUniformly(Task baseTask, int pcs);
}

#endif