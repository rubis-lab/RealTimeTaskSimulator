#ifndef __EXPPRINT__
#define __EXPPRINT__

#include <string>
#include <cmath>
#include <iostream>
#include <iomanip>
#include <fstream>
#include "../../tools/FileIO.h"
#include "../container/Param.h"
#include "../container/TaskSet.h"

class ExperimentPrinter
{
private:
public:	
	ExperimentPrinter();
	ExperimentPrinter(Param *paramExt, double inc);
	~ExperimentPrinter();
};

#endif