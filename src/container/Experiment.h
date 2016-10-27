#ifndef __EXPERIMENT__
#define __EXPERIMENT__

#include <iostream>
#include <fstream>

#include "TaskSet.h"
#include "Param.h"
#include "../generator/SimpleGenerator.h"
#include "../test/GFB.h"

class Experiment
{
private:
	int repeat;
	Param pr;
	//Generator gen;
	TaskSet ts;

public:
	Experiment();
	Experiment(std::ifstream &file);
	int init();
	int run();
	int output();
};

#endif
