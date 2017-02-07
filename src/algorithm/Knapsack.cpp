#include "Knapsack.h"

double Knapsack::knapsackMinial(int maxWeight, std::vector<int> itemWeight, std::vector<double> itemCost)
{
	// 2-D Cache for iterative solution
	std::vector<std::vector<double>> knapCache;
	for(int i = 0; i < 2; i++) {
		std::vector<double> knapCacheRow;
		for(int i = 0; i < maxWeight; i++) {
			knapCacheRow.push_back(0.0);
		}
		knapCache.push_back(knapCacheRow);
	}

	for(unsigned int i = 0; i < itemWeight.size(); i++) {
		//std::cout<<i<<"\t: ";
		for(int j = 1; j <= maxWeight; j++) {
			// current item cannot fit --> use previous
			if(j < itemWeight[i]) {
				knapCache[i % 2][j - 1] = knapCache[1 - i % 2][j - 1];
			} else {
			// can fit --> either take current item or not.
				knapCache[i % 2][j - 1] = std::max(knapCache[1 - i % 2][j - 1], knapCache[1 - i % 2][j - 1 - itemWeight[i]] + itemCost[i]);
			}
			//std::cout<<knapCache[i % 2][j]<<" ";
		}
		//std::cout<<std::endl;
	}

	return knapCache[(itemWeight.size() - 1) % 2][maxWeight - 1];
}

double Knapsack::knapsackFalseWeight(int maxWeight, std::vector<int> falseWeight, std::vector<int> itemWeight, std::vector<double> itemCost)
{
	// Items have false weight, 
	// For example, item (2, 1, 10) will be included after pass 2
	// but uses actual weight capacity of 1.
	// Item A(1, 1, 5), Item B(2, 1, 4)
	// B will always be included after A is included. 
	// this behavior can be used to bundle B with A.

	// 2-D Cache for iterative solution
	std::vector<std::vector<double>> knapCache;
	for(int i = 0; i < 2; i++) {
		std::vector<double> knapCacheRow;
		for(int j = 0; j <= maxWeight; j++) {
			knapCacheRow.push_back(0.0);
		}
		knapCache.push_back(knapCacheRow);
	}

	for(unsigned int i = 0; i < itemWeight.size(); i++) {
		//std::cout<<i<<"\t: ";
		knapCache[i % 2][0] = 0.0;
		for(int j = 1; j <= maxWeight; j++) {
			// current item cannot fit --> use previous
			if(j < falseWeight[i]) {
				knapCache[i % 2][j] = knapCache[1 - i % 2][j];
			} else {
			// can fit --> either take current item or not.
				knapCache[i % 2][j] = std::max(knapCache[1 - i % 2][j], knapCache[1 - i % 2][j - itemWeight[i]] + itemCost[i]);
			}
			//std::cout<<knapCache[i % 2][j]<<" ";
		}
		//std::cout<<std::endl;
	}

	return knapCache[(itemWeight.size() - 1) % 2][maxWeight];

}

double Knapsack::knapsackFalseWeightPrint(int maxWeight, std::vector<int> falseWeight, std::vector<int> itemWeight, std::vector<double> itemCost)
{
	// Items have false weight, 
	// For example, item (2, 1, 10) will be included after pass 2
	// but uses actual weight capacity of 1.
	// Item A(1, 1, 5), Item B(2, 1, 4)
	// B will always be included after A is included. 
	// this behavior can be used to bundle B with A.

	// 2-D Cache for iterative solution
	std::vector<std::vector<double>> knapCache;
	for(int i = 0; i < 2; i++) {
		std::vector<double> knapCacheRow;
		for(int j = 0; j <= maxWeight; j++) {
			knapCacheRow.push_back(0.0);
		}
		knapCache.push_back(knapCacheRow);
	}

	for(unsigned int i = 0; i < itemWeight.size(); i++) {
		std::cout<<i<<"\t: ";
		knapCache[i % 2][0] = 0.0;
		std::cout<<knapCache[i % 2][0]<<" ";
		for(int j = 1; j <= maxWeight; j++) {
			// current item cannot fit --> use previous
			if(j < falseWeight[i]) {
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