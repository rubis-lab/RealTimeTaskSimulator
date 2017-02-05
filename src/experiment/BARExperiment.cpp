#include "BARExperiment.h"

BARExperiment::BARExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/barexp.cfg");
	init(file);
	file.close();
}

BARExperiment::~BARExperiment()
{
	delete elNorm;
	delete elPara;
	delete ng;
	delete bar;
	delete barMod;
}

int BARExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
	set();
	return 1;
}

int BARExperiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;
	file >> buf;
	file >> oMin;
	file >> buf;
	file >> oMax;
	file >> buf;
	file >> pOverhead;
	file >> buf;
	file >> pVariance;

	return 1;
}

int BARExperiment::set()
{
	ng = new NormalGenerator(pr, cr);
	bar = new BAR(pr);
	barMod = new BARMod(pr);
	elNorm = new ExperimentLogger(expName+"Norm", pr, utilizationInc);
	//elNorm->startRecord(utilizationInc);
	elPara = new ExperimentLogger(expName+"Para", pr, utilizationInc);
	//elPara->startRecord(utilizationInc);

	return 1;
}

int BARExperiment::reset()
{
	return 1;
}

int BARExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		// Normal Tasks
		TaskSet ts = ng->nextTaskSet();
		//TaskSetUtil::printTaskInfo(ts);
		//TaskSetUtil::printTaskSet(ts);
		elNorm->addRecord(TaskSetUtil::sumUtilization(ts), bar->isSchedulable(ts));

		// Parallelized Tasks
		//TaskSet tsPara = tsp->parallelizeIntoOption(ts, 4);
		TaskSet tsPara = tsp->parallelizeIntoRandomOption(ts, oMin, oMax, pOverhead, pVariance);
		//TaskSetUtil::printTaskInfo(tsPara);
		//TaskSetUtil::printTaskSet(tsPara);
		elPara->addRecord(TaskSetUtil::sumUtilization(tsPara), bar->isSchedulable(tsPara));

		// Output
		if(i % midResult == 0) {
			elNorm->printProbSched();
			elPara->printProbSched();
		}
	}

	return 1;
}

int BARExperiment::output()
{
	elNorm->printRecordLong();
	elPara->printRecordLong();
	return 1;
}