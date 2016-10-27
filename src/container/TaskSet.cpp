#include "TaskSet.h"

TaskSet::TaskSet()
{

}

int TaskSet::readTaskSet(std::ifstream &file)
{
	int e, d, p;
	int cnt;
	file >> cnt;
	ts.clear();
	for(int i = 0; i < cnt; i++) {
		file >> e;
		file >> d;
		file >> p;
		Task t = Task(e, d, p);
		ts.push_back(t);
	}

	return 1;
}

int TaskSet::printTaskSet()
{
	std::cout << count() << std::endl;

	for(int i = 0; i < count(); i++) {
		std::cout << ts[i].getExecTime() << " ";
		std::cout << ts[i].getDeadline() << " ";
		std::cout << ts[i].getPeriod() << std::endl;
	}
	return 1;
}

int TaskSet::count() 
{
	return ts.size();
}

Task TaskSet::getTask(int idx)
{
	return ts[idx];
}

int TaskSet::pushBack(Task t)
{
	ts.push_back(t);
	return 1;
}

int TaskSet::clear()
{
	ts.clear();
	return 1;
}

double TaskSet::sumDensity()
{
	double sum = 0.0;
	for(int i = 0; i < count(); i++) 
		sum += ts[i].getDensity();
	return sum;
}

double TaskSet::sumUtilization()
{
	double sum = 0.0;
	for(int i = 0; i < count(); i++) 
		sum += ts[i].getUtilization();
	return sum;
}

double TaskSet::maxDensity()
{
	auto maxDensityTask = std::minmax_element(ts.begin(), ts.end(), \
		[] (Task & t1, Task & t2) {return t1.getDensity() > t2.getDensity();});
	//[] (Task t1, Task t2) {return t1.getDensity() > t2.getDensity();});
	return maxDensityTask.first->getDensity();
}

double TaskSet::maxUtilization()
{
	auto maxUtilizationTask = std::minmax_element(ts.begin(), ts.end(), \
		[] (Task & t1, Task & t2) {return t1.getUtilization() > t2.getUtilization();});
	//[] (Task t1, Task t2) {return t1.getUtilization() > t2.getUtilization();});
	return maxUtilizationTask.first->getUtilization();
}

int TaskSet::sortByDensity()
{
	std::sort(ts.begin(), ts.end(), \
		[] (Task t1, Task t2) {return t1.getDensity() < t2.getDensity();});
	return 1;
}

