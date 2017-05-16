#include "ExperimentLogger.h"

ExperimentLogger::ExperimentLogger(std::string ename, Param *paramExt, double cap, double inc)
{
	pr = paramExt;
	expName = ename;
	capacity = cap;
	incSize = inc;
	containerSize = (int)std::ceil(capacity / inc);
	init();
}

ExperimentLogger::~ExperimentLogger()
{
	outFile.close();
}

int ExperimentLogger::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> outPrecision;

	return 1;
}

int ExperimentLogger::init()
{
	fileName = "../data/generated/" + expName + ".txt";
	std::cout<<fileName<<std::endl;

	outFile.open(fileName, std::ofstream::out);
	outFile<<"***"<<expName<<std::endl;
	outFile<<"core\t\t"<<pr->getNProc()<<std::endl;
	outFile<<"seed\t\t"<<pr->getSeed()<<std::endl;
	outFile<<"capacity\t"<<capacity<<std::endl;
	outFile<<"incSize\t\t"<<incSize<<std::endl;
	outFile<<"containerSize\t"<<containerSize<<std::endl;
	outFile<<"----------------"<<std::endl;

	std::ifstream inFile;
	inFile.open("../cfg/ExperimentLogger.cfg");
	loadEnvironment(inFile);
	inFile.close();

	totalSetCount = std::vector<int>(containerSize);
	schedulableSetCount = std::vector<int>(containerSize);
	probSchedulable = std::vector<double>(containerSize);

	for (int i = 0; i < containerSize; i++)
	{
		totalSetCount[i] = 0;
		schedulableSetCount[i] = 0;
		probSchedulable[i] = 0.0;
	}

	return 1;
}

int ExperimentLogger::normalizeRecord()
{
	for(int i = 0; i < containerSize; i++) {
		if(totalSetCount[i] == 0) {
			probSchedulable[i] = 0.0;
		} else {
			probSchedulable[i] = (double)schedulableSetCount[i] / (double)totalSetCount[i];
		}
	}

	return 1;
}

int ExperimentLogger::addRecord(double itemIndex, bool sched) 
{
	if(itemIndex >= capacity) {
		std::cout<<"ExperimentLogger::addRecord: Index out of bound"<<std::endl;
		return 0;
	}
	int idx = (int)std::floor(itemIndex / incSize);
	// ntot
	totalSetCount[idx]++;
	// nsched
	if(sched) {
		schedulableSetCount[idx]++;
	}
//	std::cout << incSize <<" "<< idx <<  " : " << totalSetCount[idx] << "/" << schedulableSetCount[idx] << std::endl;	

	return 1;
}

int ExperimentLogger::printRecordLong()
{
	normalizeRecord();
	std::cout<<std::setprecision(4)<<std::fixed;
	// print
	std::cout<<"ntot"<<std::endl;
	for(unsigned int i = 0; i < totalSetCount.size(); i++) {
		std::cout<<i * incSize<<"\t"<<totalSetCount[i]<<std::endl;
	}
	std::cout<<"nsched"<<std::endl;
	for(unsigned int i = 0; i < schedulableSetCount.size(); i++) {
		std::cout<<i * incSize<<"\t"<<schedulableSetCount[i]<<std::endl;
	}
	std::cout<<"probsched"<<std::endl;
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		std::cout<<i * incSize<<"\t"<<probSchedulable[i]<<std::endl;
	}

	// roll-back cout precision
	std::cout.copyfmt(std::ios(NULL));

	return 1;
}

int ExperimentLogger::outputRecord(std::string str)
{
	normalizeRecord();

	outFile<<str<<std::endl;
	outFile<<std::setprecision(4)<<std::fixed;

	// print
	outFile<<"ntot"<<std::endl;
	for(unsigned int i = 0; i < totalSetCount.size(); i++) {
		outFile<<i * incSize<<"\t"<<totalSetCount[i]<<std::endl;
	}
	outFile<<"nsched"<<std::endl;
	for(unsigned int i = 0; i < schedulableSetCount.size(); i++) {
		outFile<<i * incSize<<"\t"<<schedulableSetCount[i]<<std::endl;
	}
	outFile<<"probsched"<<std::endl;
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		outFile<<i * incSize<<"\t"<<probSchedulable[i]<<std::endl;
	}
	outFile<<"----------------"<<std::endl;

	// roll-back cout precision
	outFile.copyfmt(std::ios(NULL));

	return 1;
}

double ExperimentLogger::getProbSchedAtIdx(int idx)
{
	if(idx >= containerSize) {
		std::cout<<"ExperimentLogger::getProbSchedAtIdx: Index out of bound"<<std::endl;
		return 0.0;
	}
	return probSchedulable[idx];
}

double ExperimentLogger::getInc()
{
	return incSize;
}

unsigned int ExperimentLogger::getSize()
{
	return probSchedulable.size();
}

std::ofstream& ExperimentLogger::getOutFile()
{
	return outFile;
}

int ExperimentLogger::printProbSched()
{
	normalizeRecord();
	std::cout << std::setprecision(4) << std::fixed;

	std::cout<<"probsched"<<std::endl;
	for(unsigned int i = 0; i < probSchedulable.size(); i++) {
		std::cout<<i * incSize<<"\t"<<probSchedulable[i]<<std::endl;
	}
	std::cout.copyfmt(std::ios(NULL));
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
