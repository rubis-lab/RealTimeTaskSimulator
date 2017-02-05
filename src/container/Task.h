#ifndef __TASK__
#define __TASK__

class Task
{
private:
	int ID;
	double period;
	double execTime;
	double deadline;
	//int priority;
	double offset;
public:	
	Task();
	Task(double e, double d, double p);
	Task(int id, double e, double d, double p);
	~Task();

	int getID();
	double getExecTime();
	double getDeadline();
	double getPeriod();

	int setID(int id);
	int setExecTime(double e);
	int setDeadline(double d);
	int setPeriod(double p);
};

#endif
