class Thread: public Task
{
private:
public:	
	Thread();
	Thread(double e, double d, double p);
	int setDeadline(double d);
	int setPeriod(double p);
	
};