#include "experiment/TestExperiment.h"
#include <cstdlib>
#include <ctime>

int main(int argc, char *argv[])
{
	srand(time(NULL));
	TestExperiment texp = TestExperiment();
	texp.run();
	return 1;
}