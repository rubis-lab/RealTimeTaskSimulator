#include "experiment/BARExperiment.h"
#include <cstdlib>
#include <ctime>

int main(int argc, char *argv[])
{
	srand(time(NULL));
	BARExperiment barexp = BARExperiment();
	barexp.run();
	barexp.output();
	
	return 1;
}