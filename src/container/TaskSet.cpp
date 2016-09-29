#include "TaskSet.h"

TaskSet::TaskSet(void)
{

}

int TaskSet::readTaskSet(std::ifstream &file)
{
	int e, d, p;
	file >> count;
	ts.clear();
	for(int i = 0; i < count; i++) {
		file >> e;
		file >> d;
		file >> p;
		Task t = Task(e, d, p);
		ts.push_back(t);
	}

	return 1;
}

int TaskSet::printTaskSet(void)
{
	std::cout << count << std::endl;

	for(int i = 0; i < count; i++) {
		std::cout << ts[i].getExecutionTime() << " ";
		std::cout << ts[i].getDeadline() << " ";
		std::cout << ts[i].getPeriod() << std::endl;
	}
	return 1;
}

int TaskSet::length(){
  return ts.size();
}

Task TaskSet::getTask(int i){
  return ts[i];
}

void TaskSet::putTask(Task t){
  ts.push_back(t);
}

void TaskSet::clear(){
  ts.clear();
}
