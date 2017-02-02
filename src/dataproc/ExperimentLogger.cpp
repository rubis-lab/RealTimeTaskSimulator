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

ExperimentLogger::~ExperimentLogger()
{
	delete outFile;
}

int ExperimentLogger::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> prec;

	return 1;
}

int ExperimentLogger::init()
{
	fileName = "../data/generated/" + expName + ".txt";
	std::cout<<fileName<<std::endl;
	outFile = new std::ofstream(fileName, std::ofstream::out);
	// std::ofstream(std::ofstream::out | std::ofstream::app);
	*outFile << "test output";
	outFile->close();

	std::ifstream inFile;
	inFile.open("../cfg/log.cfg");
	loadEnvironment(inFile);
	inFile.close();

	return 1;
}

int ExperimentLogger::normalizeRecord()
{
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		if(totalSetCount[i] == 0) {
			probSchedulable[i] = 0.0;
		} else {
			probSchedulable[i] = (double)schedulableSetCount[i] / (double)totalSetCount[i];
		}
	}

	return 1;
}

int ExperimentLogger::startRecord(double inc)
{
	incrementSize = inc;
	int containerSize = (int)std::ceil(pr->getNProc() / incrementSize);

	totalSetCount.resize(containerSize, 0);
	schedulableSetCount.resize(containerSize, 0);
	probSchedulable.resize(containerSize, 0.0);

	return 1;
}

int ExperimentLogger::addRecord(double util, bool sched) 
{
	int idx = (int)std::floor(util / incrementSize);
	// ntot
	totalSetCount[idx]++;
	// nsched
	if(sched) {
		schedulableSetCount[idx]++;
	}

	return 1;
}

int ExperimentLogger::printRecordLong()
{
	normalizeRecord();

	std::cout << std::setprecision(prec) << std::fixed;
	// print
	std::cout<<"ntot"<<std::endl;
	for(unsigned int i = 0; i < totalSetCount.size(); i++) {
		std::cout<<i * incrementSize<<"\t"<<totalSetCount[i]<<std::endl;
	}
	std::cout<<"nsched"<<std::endl;
	for(unsigned int i = 0; i < schedulableSetCount.size(); i++) {
		std::cout<<i * incrementSize<<"\t"<<schedulableSetCount[i]<<std::endl;
	}
	std::cout<<"probsched"<<std::endl;
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		std::cout<<i * incrementSize<<"\t"<<probSchedulable[i]<<std::endl;
	}

	return 1;
}

int ExperimentLogger::printProbSched()
{
	normalizeRecord();
	std::cout << std::setprecision(prec) << std::fixed;

	std::cout<<"probsched"<<std::endl;
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		std::cout<<i * incrementSize<<"\t"<<probSchedulable[i]<<std::endl;
	}

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