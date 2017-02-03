#include "Knapsack.h"

double knapsackMinial(int maxWeight, std::vector<int> itemWeight, std::vector<double> itemCost)
{
	// 2-D Cache for iterative solution
	std::vector<std::vector<double>> knapCache;
	for(int i = 0; i < 2; i++) {
		std::vector<double> kanpCacheRow;
		kanpCacheRow.resize(maxWeight, 0.0);
		knapCache.push_back(kanpCacheRow);
	}

	for(int i = 0; i < itemWeight.size(); i++) {
		std::cout<<i<<"\t: ";
		for(int j = 1; j <= maxWeight; j++) {
			// current item cannot fit --> use previous
			if(j < itemWeight[i]) {
				knapCache[i % 2][j] = knapCache[1 - i % 2][j];
			} else {
			// can fit --> either take current item or not.
				knapCache[i % 2][j] = std::max(knapCache[1 - i % 2][j], knapCache[1 - i % 2][j - itemWeight[i]] + itemCost[i]);
			}
			std::cout<<knapCache[i % 2][j]<<" ";
		}
		std::cout<<std::endl;
	}

	return knapCache[(itemWeight.size() - 1) % 2][maxWeight];
}

std::vector<int> knapsack(double maxWeight, std::vector<int> itemWeight, std::vector<double> itemCost)
{
	std::vector<int> ret;
	return ret;
}