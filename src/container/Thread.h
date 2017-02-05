#ifndef __THREAD__
#define __THREAD__

#include "../container/Task.h"

class Thread: public Task
{
private:
	int threadID;

public:	
	Thread();
	Thread(double e, double d, double p);
	Thread(int id, int tid, double e, double d, double p);
	~Thread();

	int getTID();
	int setTID(int tid);
};

#endif