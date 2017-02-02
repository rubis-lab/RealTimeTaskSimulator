#include "TaskModifier.h"

// should delete this later on.
std::vector<Task> TaskModifier::splitTaskUniformly(Task baseTask, int pcs)
{
	std::vector<Task> retTasks;
	int idx = std::remainder((int)baseTask.getExecTime(), pcs);

	for(int i = 0; i < pcs; i++) {
		Task t = Task();

		// (0..idx-1) --> C/pcs + 1
		// (idx..pcs) --> C/pcs
		if(i < idx) {
			t.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs) + 1.0);
		} else {
			t.setExecTime(std::floor(baseTask.getExecTime() / (double)pcs));	
		}

		// D, P --> same
		t.setDeadline(baseTask.getDeadline());
		t.setPeriod(baseTask.getPeriod());
		retTasks.push_back(t);
	}

	return retTasks;
}