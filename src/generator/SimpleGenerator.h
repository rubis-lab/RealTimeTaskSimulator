#ifndef __SIMPLE_GEN__
#define __SIMPLE_GEN__

class SimpleGenerator: public Generator
{
private:
	
public:
	SimpleGenerator() : Generator() { };
	SimpleGenerator(int seed) : Generator(seed) { };
	int loadConfig(std::ifstream &file);
	Task nextTask(int id);
	TaskSet nextTaskSet(int id);
	//int saveConfig(void);
};
#endif