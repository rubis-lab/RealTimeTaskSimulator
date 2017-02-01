#ifndef __PARAM__
#define __PARAM__

#include <ctime>

#include "../../tools/FileIO.h"

class Param
{
private:
	double nProc;
	int seed;
	int init(std::ifstream &file);
	int loadEnvironment(std::ifstream &file);
public:	
	Param();
	Param(std::ifstream &file);
	~Param();
	double getNProc();
	int getSeed();
};
#endif