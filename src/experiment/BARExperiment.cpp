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
	//delete elNorm;
	//delete elMod;
	//delete elPara;
	delete vg;
	delete ng;
	delete gfb;
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

	return 1;
}

int BARExperiment::set()
{
	vg = new VarianceGenerator(pr, cr);
	ng = new NormalGenerator(pr, cr);

	gfb = new GFB(pr);
	bar = new BAR(pr);
	barMod = new BARMod(pr);
	//elNorm = new ExperimentLogger(expName+"Norm", pr, utilizationInc);
	
	elPara = new ExperimentLogger(expName+"Para", pr, utilizationInc);
	elMod = new ExperimentLogger(expName+"Mod", pr, utilizationInc);
	printInfo(elPara->getOutFile());
	vg->printInfo(elPara->getOutFile());
	tsp->printInfo(elPara->getOutFile());
	return 1;
}

int BARExperiment::reset()
{
	return 1;
}

int BARExperiment::printInfo(std::ofstream &file)
{
	file<<"***BARExperiment"<<std::endl;
	file<<"Iteration\t"<<iter<<std::endl;
	file<<"ResEvery\t"<<midResult<<std::endl;
	file<<"Inc\t\t"<<utilizationInc<<std::endl;
	file<<"----------------"<<std::endl;
	return 1;
}

int BARExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		/*
		// Normal Tasks
		TaskSet ts = ng->nextTaskSet();
		//TaskSetUtil::printTaskInfo(ts);
		//TaskSetUtil::printTaskSet(ts);
		//elNorm->addRecord(TaskSetUtil::sumUtilization(ts), bar->isSchedulable(ts));

		// Parallelized Tasks
		//TaskSet tsPara = tsp->parallelizeIntoOption(ts, 4);
		TaskSet tsPara = tsp->parallelizeIntoRandomOption(ts, oMin, oMax, pOverhead, pVariance);

		//TaskSetUtil::printTaskInfo(tsPara);
		//TaskSetUtil::printTaskSet(tsPara);
		
		elPara->addRecord(TaskSetUtil::sumUtilization(tsPara), bar->isSchedulable(tsPara));
		elMod->addRecord(TaskSetUtil::sumUtilization(tsPara), barMod->isSchedulable(tsPara));
		// Output
		*/
		TaskSet ts = vg->nextTaskSet();
		TaskSet tsPara = tsp->parallelizeIntoRandomOption(ts);
		elPara->addRecord(TaskSetUtil::sumUtilization(tsPara), gfb->isSchedulable(tsPara));
		elMod->addRecord(TaskSetUtil::sumUtilization(tsPara), barMod->isSchedulable(tsPara));
		if(i % midResult == 0) {
			//elNorm->printProbSched();
			elPara->printProbSched();
			elMod->printProbSched();
		}
		
		/*
		TaskSet ts = ng->nextTaskSet();
		//TaskSetUtil::printTaskInfo(ts);
		//TaskSetUtil::printTaskSet(ts);
		TaskSet tsPara = tsp->parallelizeIntoRandomOption(ts);
		//TaskSetUtil::printTaskInfo(tsPara);
		//TaskSetUtil::printTaskSet(tsPara);
		//int a;
		//std::cin>>a;
		elPara->addRecord(TaskSetUtil::sumUtilization(tsPara), bar->isSchedulable(tsPara));
		elMod->addRecord(TaskSetUtil::sumUtilization(tsPara), barMod->isSchedulable(tsPara));
		e

		if(modSched && !paraSched) {
			TaskSet savTaskSet = tsPara;
			TaskSetUtil::printTaskInfo(savTaskSet);
			TaskSetUtil::printTaskSet(savTaskSet);
			barMod->isSchedulablePrint(tsPara);
			bar->isSchedulablePrint(tsPara);

			int a;
			std::cin>>a;
		}
		*/

	}

	return 1;
}

int BARExperiment::output()
{	
	//elNorm->printRecordLong();
	elPara->printRecordLong();
	elMod->printRecordLong();
	std::cout<<"Result\tPara\tMod\tDiff"<<std::endl;
	std::cout << std::setprecision(4) << std::fixed;
		for(unsigned int j = 0; j < elPara->getSize(); j++) {
			std::cout<<elPara->getInc() * j<<"\t";
			std::cout<<elPara->getProbSchedAtIdx(j)<<"\t";
			std::cout<<elMod->getProbSchedAtIdx(j)<<"\t";
			std::cout<<elMod->getProbSchedAtIdx(j) - elPara->getProbSchedAtIdx(j)<<std::endl;
	}
	std::cout.copyfmt(std::ios(NULL));
	return 1;
}