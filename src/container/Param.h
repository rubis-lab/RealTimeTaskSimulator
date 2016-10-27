#ifndef __PARAM__
#define __PARAM__

#include <ctime>
#include "../../tools/FileIO.h"

class Param
{
private:
	double nProc;
	int seed;
public:	
	Param();
	Param(std::ifstream &file);
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
	int getNProc();
	int getSeed();
};
#endif