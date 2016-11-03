#include "ExperimentLogger.h"

ExperimentLogger::ExperimentLogger()
{
	init("test");
}

ExperimentLogger::ExperimentLogger(std::string fname)
{
	init(fname);
}

int ExperimentLogger::init(std::string fname)
{
	fileName = "../data/generated/" + fname + ".txt";
	std::cout<<fileName<<std::endl;
	outFile = new std::ofstream(fileName, std::ofstream::out);
	// std::ofstream(std::ofstream::out | std::ofstream::app);
	*outFile << "test output1";
	outFile->close();
	return 1;
}
/*
int ExperimentLogger::printSchedulability()
{
	// input 1. taskSet, 2. 
	// num task
	// util - schedulability
	std::cout<<""<<std::endl;
	return 1;
}

*/