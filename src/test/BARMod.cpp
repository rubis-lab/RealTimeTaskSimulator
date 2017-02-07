#include "BARMod.h"

BARMod::BARMod() : BAR()
{

}

BARMod::BARMod(Param *paramExt) : BAR(paramExt)
{
	pr = paramExt;
}

BARMod::~BARMod()
{
	
}

int BARMod::classifyThreads(TaskSet &ts, int baseTaskIndex, double extendedInterval)
{
	threadClassification.clear();
	//std::cout<<"Ak: "<<extendedInterval<<std::endl;
	Task baseTask = ts.getTask(baseTaskIndex);
	for(int i = 0; i < ts.count(); i++) {
		Task interTask = ts.getTask(i);
		double leftOverInterval = std::fmod(extendedInterval + baseTask.getDeadline(), interTask.getPeriod());
		if(interTask.getExecTime() > leftOverInterval) {
			// forced NC impossible thread
			threadClassification.push_back(false);
		} else {
			// forced NC possible thread
			threadClassification.push_back(true);
		}
	}

	return 1;
}

int BARMod::packageThreads(TaskSet &ts)
{
	packageFalseWeight.clear();
	packageTrueWeight.clear();

	int currID = 0;
	for(int i = 0; i < ts.count(); i++) {
		// task-wise iteration
		int cnt = 0;
		while(ts.getTask(i).getID() == currID) {
			// forced NC impossible thread
			if(!threadClassification[i]) {
				// pop last one, put 0 inside to distinguish
				if(cnt >= 1) {
					packageFalseWeight.pop_back();
					packageFalseWeight.push_back(0);
					packageTrueWeight.pop_back();
					packageTrueWeight.push_back(0);
				}
				cnt++;
				// last one following 0 is packaged weight.
				packageFalseWeight.push_back(cnt);
				packageTrueWeight.push_back(cnt);
			} else {
				// forced NC is possible, cnt + 1 is the false weight
				packageFalseWeight.push_back(cnt + 1);
				// but it is single thread in real
				packageTrueWeight.push_back(1);
			}
			i++;
			if(i == ts.count()) {
				break;
			}
		}
		i--;
		currID++;
	}
	// debug
	//std::cout<<"false count: "<<packageFalseWeight.size()<<std::endl;
	//std::cout<<"true count: "<<packageTrueWeight.size()<<std::endl;

	return 1;
}

int BARMod::calculatePackageCost(TaskSet &ts)
{
	packageCost.clear();

	for(int i = 0; i < ts.count(); i++) {
		// add up until encounter with non-zero weight.
		double tmp = 0.0;
		while(packageFalseWeight[i] == 0) {
			// push in 0.0 to distinguish
			tmp += iDiff[i];
			packageCost.push_back(0.0);
			i++;
		}
		tmp += iDiff[i];
		packageCost.push_back(tmp);
	}
	//std::cout<<"cost count: "<<packageCost.size()<<std::endl;
	return 1;
}

int BARMod::debugPrint(TaskSet &ts)
{
	for(unsigned int i = 0; i < threadClassification.size(); i++) {
		std::cout<<i<<": "<<threadClassification[i]<<", ("<<packageFalseWeight[i]<<", ";
		std::cout<<packageTrueWeight[i]<<", "<<packageCost[i]<<"), "<<iDiff[i]<<std::endl;
	}
	return 1;
}

double BARMod::doKnapsack(TaskSet &ts)
{
	std::vector<int> pfweight;
	std::vector<int> ptweight;
	std::vector<double> pcost;
	for(unsigned int i = 0; i < packageFalseWeight.size(); i++) {
		if(packageFalseWeight[i] != 0) {
			pfweight.push_back(packageFalseWeight[i]);
			ptweight.push_back(packageTrueWeight[i]);
			pcost.push_back(packageCost[i]);
		}
	}

	/*
	for(unsigned int i = 0; i < pfweight.size(); i++) {
		std::cout<<i<<" "<<pfweight[i]<<", "<<ptweight[i]<<", "<<pcost[i]<<std::endl;
	}
	*/
	return Knapsack::knapsackFalseWeight(pr->getNProc() - 1, pfweight, ptweight, pcost);
}

double BARMod::doKnapsackPrint(TaskSet &ts)
{
	std::vector<int> pfweight;
	std::vector<int> ptweight;
	std::vector<double> pcost;
	for(unsigned int i = 0; i < packageFalseWeight.size(); i++) {
		if(packageFalseWeight[i] != 0) {
			pfweight.push_back(packageFalseWeight[i]);
			ptweight.push_back(packageTrueWeight[i]);
			pcost.push_back(packageCost[i]);
		}
	}

	
	for(unsigned int i = 0; i < pfweight.size(); i++) {
		std::cout<<i<<" "<<pfweight[i]<<", "<<ptweight[i]<<", "<<pcost[i]<<std::endl;
	}
	std::cout<<"ts count: "<<ts.count()<<std::endl;
	std::cout<<"false count: "<<packageFalseWeight.size()<<std::endl;
	std::cout<<"true count: "<<packageTrueWeight.size()<<std::endl;
	std::cout<<"cost count: "<<packageCost.size()<<std::endl;

	return Knapsack::knapsackFalseWeightPrint(pr->getNProc() - 1, pfweight, ptweight, pcost);
}

double BARMod::calcSumIDiff(TaskSet &ts, int baseTaskIndex, double extendedInterval)
{
	//std::cout<<"interval: "<<extendedInterval + ts.getTask(baseTaskIndex).getDeadline()<<std::endl;
	classifyThreads(ts, baseTaskIndex, extendedInterval);
	packageThreads(ts);
	calculatePackageCost(ts);
	//debugPrint(ts);
	return doKnapsack(ts);
	//return Knapsack::knapsackFalseWeight(pr->getNProc() - 1, packageFalseWeight, packageTrueWeight, packageCost);
}

bool BARMod::isSchedulable(TaskSet &ts)
{
	// Ak bound
	std::vector<double> extIntervalBoundList = calcExtendedIntervalBound(ts);

	for(int baseTaskIndex = 0; baseTaskIndex < ts.count(); baseTaskIndex++) {
		
		// Ak <= 0, don't need to check
		//std::cout<<baseTaskIndex<<"/"<<ts.count() - 1<<" ";
		//std::cout<<"ext length: "<<extIntervalBoundList[baseTaskIndex]<<std::endl;
		if(extIntervalBoundList[baseTaskIndex] <= 0.0) {
			continue;
		}
		// RHS m(Ak + Dk - Ck)
		double rhs = ts.getTask(baseTaskIndex).getDeadline() - ts.getTask(baseTaskIndex).getExecTime();
		
		// iterate with Ak
		double extInterval = 0.0;
		while(extInterval < extIntervalBoundList[baseTaskIndex]) {
			
			iNC.clear();
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// non carry-in
				iNC.push_back(calcNCInterference(ts, baseTaskIndex, interTaskIndex, extInterval));
			}

			iCI.clear();
			for(int interTaskIndex = 0; interTaskIndex < ts.count(); interTaskIndex++) {
				// carry-in
				iCI.push_back(calcCIInterference(ts, baseTaskIndex, interTaskIndex, extInterval));
			}
			
			// IDiff
			calcIDiff();

			// print info
			//debugPrintIDiff();

			// Sum I
			double isum = 0.0;
			for(unsigned int i = 0; i < iNC.size(); i++) {
				isum += iNC[i];
			}

			// find m - 1 carry-ins
			double sum = calcSumIDiff(ts, baseTaskIndex, extInterval);
			//std::cout<<"diff = "<<sum<<std::endl;
			// vector init error.
			if(sum < 1.0) {
				sum = 0.0;
			}
			isum += sum;

			// for checking
			std::vector<double> iKMaxCI = PMath::kMax(iDiff, pr->getNProc() - 1);
			double sumtmp = 0.0;
			for(unsigned int i = 0; i < iKMaxCI.size(); i++) {
				sumtmp += iKMaxCI[i];
			}

			if(sumtmp < sum) {
				// this should not happen
				std::cout<<"error"<<std::endl;
				std::cout<<"BAR sum "<<sumtmp<<std::endl;
				std::cout<<"BAR mod sum "<<sum<<std::endl;
				debugPrintIDiff();
				debugPrint(ts);
				doKnapsackPrint(ts);
				int a;
				std::cin>>a;
			}
			

			// unschedule condition
			/*
			std::cout<<"diff: "<<sum<<std::endl;
			std::cout<<"sum: "<<isum<<std::endl;
			std::cout<<"compare with: "<<pr->getNProc() * (rhs + extInterval)<<std::endl;
			int a;
			std::cin>>a;
			*/
			if(isum > pr->getNProc() * (rhs + extInterval)) {
				//std::cout<<"****unsched"<<std::endl;
				return false;
			}

			// next A (for now)
			// can be checked everytime DBF changes
			extInterval += 1.0;
		}
	}
	//std::cout<<"****shced"<<std::endl;
	return true;
}