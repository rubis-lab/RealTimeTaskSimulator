#include "TaskSetUtil.h"

TaskSet TaskSetUtil::readTaskSet(std::ifstream &file)
{
	TaskSet retTaskSet = TaskSet();
	int e, d, p;
	int cnt;
	file >> cnt;
	retTaskSet.clear();
	for(int i = 0; i < cnt; i++) {
		file >> e;
		file >> d;
		file >> p;
		Task t = Task(e, d, p);
		retTaskSet.pushBack(t);
	}

	return retTaskSet;
}

int TaskSetUtil::printTaskSet(TaskSet &ts)
{
	for(int i = 0; i < ts.count(); i++) {
		std::cout << ts.getTask(i).getExecTime() << "\t";
		std::cout << ts.getTask(i).getDeadline() << "\t";
		std::cout << ts.getTask(i).getPeriod() << std::endl;
	}
	return 1;
}

int TaskSetUtil::printTaskInfo(TaskSet &ts)
{
	std::cout << "cnt\t" << ts.count() << std::endl;
	std::cout << "util\t" << sumUtilization(ts) <<std::endl;
	return 1;
}

double TaskSetUtil::sumDensity(TaskSet &ts)
{
	double sum = 0.0;
	for(int i = 0; i < ts.count(); i++) 
		sum += TaskUtil::calcDensity(ts.getTask(i));
	return sum;
}

double TaskSetUtil::sumUtilization(TaskSet &ts)
{
	double sum = 0.0;
	for(int i = 0; i < ts.count(); i++) 
		sum += TaskUtil::calcUtilization(ts.getTask(i));
	return sum;
}

double TaskSetUtil::getMaxDensity(TaskSet &ts)
{
	std::vector<Task> tsvec = ts.getVector();
	auto maxDensityTask = std::minmax_element(tsvec.begin(), tsvec.end(), \
		[] (Task & t1, Task & t2) {return TaskUtil::calcDensity(t1) > TaskUtil::calcDensity(t2);});
	//[] (Task t1, Task t2) {return TaskUtil::calcDensity(t1) > TaskUtil::calcDensity(t2);});
	//return maxDensityTask.first->getDensity();
	return TaskUtil::calcDensity(*maxDensityTask.first);
}

double TaskSetUtil::getMaxUtilization(TaskSet &ts)
{
	std::vector<Task> tsvec = ts.getVector();
	auto maxUtilizationTask = std::minmax_element(tsvec.begin(), tsvec.end(), \
		[] (Task & t1, Task & t2) {return TaskUtil::calcUtilization(t1) > TaskUtil::calcUtilization(t2);});
	//[] (Task t1, Task t2) {return TaskUtil::calcUtilization(t1) > TaskUtil::calcUtilization(t2);});
	//return maxUtilizationTask.first->getUtilization();
	return TaskUtil::calcUtilization(*maxUtilizationTask.first);
}

int TaskSetUtil::sortByDensity(TaskSet &ts)
{
	std::vector<Task> tsvec = ts.getVector();
	std::sort(tsvec.begin(), tsvec.end(), \
		[] (Task t1, Task t2) {return TaskUtil::calcDensity(t1) < TaskUtil::calcDensity(t2);});
	return 1;
}

double TaskSetUtil::calcTaskLCM(TaskSet &ts)
{
	std::vector<int> periodArr;
	for(int i = 0; i < ts.count(); i++) {
		periodArr.push_back((int)ts.getTask(i).getPeriod());
	}
	return (double)PMath::LCM(periodArr);
}

