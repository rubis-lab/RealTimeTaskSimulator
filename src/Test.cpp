#include "experiment/BCLExperiment.h"
#include "experiment/RTAExperiment.h"
#include <cstdlib>
#include <ctime>

int main(int argc, char *argv[])
{
	srand(time(NULL));
	/*
	BCLExperiment bclexp = BCLExperiment();
	bclexp.set();
	bclexp.run();
	bclexp.output();
	*/
	RTAExperiment rtaexp = RTAExperiment();
	rtaexp.set();
	rtaexp.run();
	rtaexp.output();
	return 1;
}