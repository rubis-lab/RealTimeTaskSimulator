#include "Thread.h"

Thread::Thread() : Task()
{

}

Thread::Thread(double e, double d, double p) : Task(e, d, p)
{

}

Thread::Thread(int id, int tid, double e, double d, double p) : Task(id, e, d, p)
{
	threadID = tid;
}

Thread::~Thread()
{
	
}

int Thread::getTID()
{
	return threadID;
}

int Thread::setTID(int tid)
{
	threadID = tid;
	return 1;
}