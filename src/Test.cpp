#include "experiment/BCLExperiment.h"

int main(int argc, char *argv[])
{
	BCLExperiment bclexp = BCLExperiment();
	bclexp.set();
	bclexp.run();
	bclexp.output();
	return 1;
}