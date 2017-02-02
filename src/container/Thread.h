#ifndef __THREAD__
#define __THREAD__

#include "../container/Task.h"

class Thread: public Task
{
private:
public:	
	Thread();
	Thread(double e, double d, double p);
	~Thread();
};

#endif