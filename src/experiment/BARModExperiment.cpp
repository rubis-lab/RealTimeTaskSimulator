#include "BARModExperiment.h"

BARModExperiment::BARModExperiment() : Experiment()
{
	std::ifstream file;
	file.open("../cfg/exp/barexp.cfg");
	init(file);
	file.close();
}

BARModExperiment::~BARModExperiment()
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

int BARModExperiment::init(std::ifstream &file)
{
	loadEnvironment(file);
	set();
	return 1;
}

int BARModExperiment::loadEnvironment(std::ifstream &file)
{
	FileIO::goToLine(file, 4);

	std::string buf;
	file >> buf;
	file >> expName;

	return 1;
}

int BARModExperiment::set()
{
	vg = new VarianceGenerator(pr, cr);
	ng = new NormalGenerator(pr, cr);

	gfb = new GFB(pr);
	bar = new BAR(pr);
	barMod = new BARMod(pr);
		
	elPara = new ExperimentLogger(expName+"Para", pr, pr->getNProc(), utilizationInc);
	elMod = new ExperimentLogger(expName+"Mod", pr, pr->getNProc(), utilizationInc);
	printInfo(elPara->getOutFile());
	vg->printInfo(elPara->getOutFile());
	tsp->printInfo(elPara->getOutFile());
	return 1;
}

int BARModExperiment::reset()
{
	return 1;
}

int BARModExperiment::printInfo(std::ofstream &file)
{
	file<<"***BARModExperiment"<<std::endl;
	file<<"Iteration\t"<<iter<<std::endl;
	file<<"ResEvery\t"<<midResult<<std::endl;
	file<<"Inc\t\t"<<utilizationInc<<std::endl;
	file<<"----------------"<<std::endl;
	return 1;
}

int BARModExperiment::run()
{
	for(int i = 0; i < iter; i++) {
		TaskSet ts = vg->nextTaskSet();
		TaskSet tsPara = tsp->parallelizeIntoRandomOption(ts);
		//elPara->addRecord(TaskSetUtil::sumUtilization(tsPara), bar->isSchedulable(tsPara));
		//elMod->addRecord(TaskSetUtil::sumUtilization(tsPara), barMod->isSchedulable(tsPara));
		bool modSched = barMod->isSchedulable(tsPara);
		bool refSched = bar->isSchedulable(tsPara);
		if(modSched && !refSched) {
			std::cout<<"iter#\t"<<i<<std::endl;
			__LINE
			__PRINT(original)
			TaskSetUtil::printTaskInfo(ts);
			TaskSetUtil::printTaskSet(ts);
			__LINE
			__PRINT(parallelized)
			TaskSetUtil::printTaskInfo(tsPara);
			TaskSetUtil::printTaskSet(tsPara);
			__LINE
			barMod->isSchedulablePrint(tsPara);
			__LINE
			bar->isSchedulablePrint(tsPara);

			int a;
			std::cin>>a;
		}
	}

	return 1;
}

int BARModExperiment::output()
{	
	return 1;
}