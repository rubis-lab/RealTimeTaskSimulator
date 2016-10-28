#ifndef __EXPERIMENT__
#define __EXPERIMENT__

#include <iostream>
#include <fstream>

#include "../container/TaskSet.h"
#include "../container/Param.h"
#include "../generator/SimpleGenerator.h"
#include "../test/GFB.h"
#include "../ops/TaskSetModifier.h"

class Experiment
{
private:
	int repeat;
	Param pr;
	//Generator gen;
	TaskSet ts;
	int init();
public:
	Experiment();
	Experiment(std::ifstream &file);
	
	int naiveTest();
	int output();
};

#endif
