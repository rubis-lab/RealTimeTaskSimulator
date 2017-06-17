#include "experiment/JHAhnExperiment.h"
#include <cstdlib>
#include <ctime>

int main(int argc, char *argv[])
{
	srand(time(NULL));
	JHAhnExperiment texp = JHAhnExperiment();
  texp.set();
	texp.run();
	return 1;
}
