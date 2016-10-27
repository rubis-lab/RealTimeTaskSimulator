#include "BCL.h"

BCL::BCL(Param pr)
{
	nProc = pr.getNProc();
}

double calcInterference(Task tk, Task ti)
{
	return 0.0;
}

bool BCL::isSchedulable(TaskSet ts)
{
	for(int i = 0; i < ts.count(); i++) {
		slack[i] = 0.0;
	}
	bool isFeasible = false;
	bool updated = false;

	while(updated) {
		isFeasible = true;
		updated = false;

		// Dk - Ck - |_(1/m) sum(i!=k)min(J, Dk - Ck + 1)_|

		for(int k = 0; k < ts.count(); k++) {
			Task tk = ts.getTask(k);
			double dk = tk.getDeadline();
			double ck = tk.getExecTime();

			double sumJ = 0.0;
			for(int i = 0; i < ts.count(); i++) {
				if(i == k)
					continue;
				Task ti = ts.getTask(i);
				sumJ += std::min(calcInterference(tk, ti), dk - ck + 1.0);
			}

			double sum = sumJ = dk - ck + ;
		}


	}

	return isFeasible;
}