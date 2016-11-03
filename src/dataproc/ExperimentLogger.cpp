#include "ExperimentLogger.h"

ExperimentLogger::ExperimentLogger()
{
	expName = "Default";
	init();
}

ExperimentLogger::ExperimentLogger(std::string ename, Param *paramExt)
{
	pr = paramExt;
	expName = ename;
	init();
}

int ExperimentLogger::init()
{
	fileName = "../data/generated/" + expName + ".txt";
	std::cout<<fileName<<std::endl;
	outFile = new std::ofstream(fileName, std::ofstream::out);
	// std::ofstream(std::ofstream::out | std::ofstream::app);
	*outFile << "test output";
	outFile->close();
	return 1;
}

int ExperimentLogger::printUtilVsSchedulability(std::vector<double> &tsutil, std::vector<bool> &sched, double inc)
{
	int iter = (int)sched.size();
	int containerSize = (int)std::ceil(pr->getNProc() / inc);

	// number of schedulable task sets in utilization
	std::vector<int> nsched(containerSize);
	
	// total task sets in utilization
	std::vector<int> ntot(containerSize);

	// condense by utilization
	for(int i = 0; i < iter; i++) {
		int idx = (int)std::floor(tsutil[i] / inc);
		ntot[idx]++;
		if(sched[i]) {
			nsched[idx]++;
		}
	}

	// normalization
	std::vector<double> probsched(containerSize);

	for(int i = 0; i < containerSize; i++) {
		if(ntot[i] == 0) {
			probsched[i] = 0.0;
		} else {
			probsched[i] = (double)nsched[i] / (double)ntot[i];
		}
	}
	std::cout << std::setprecision(2) << std::fixed;
	// print
	std::cout<<"ntot"<<std::endl;
	for(int i = 0; i < containerSize; i++) {
		std::cout<<i * inc<<"\t"<<ntot[i]<<std::endl;
	}
	std::cout<<"nsched"<<std::endl;
	for(int i = 0; i < containerSize; i++) {
		std::cout<<i * inc<<"\t"<<nsched[i]<<std::endl;
	}
	std::cout<<"probsched"<<std::endl;
	for(int i = 0; i < containerSize; i++) {
		std::cout<<i * inc<<"\t"<<probsched[i]<<std::endl;
	}

	return 1;
}