#ifndef __KNAPSACK__
#define __KNAPSACK__

#include <vector>
#include <iostream>
#include <cmath>

namespace Knapsack
{
	// find maximum cost
	double knapsackMinial(int maxWeight, std::vector<int> itemWeight, std::vector<double> itemCost);
	double knapsackFalseWeight(int maxWeight, std::vector<int> falseWeight, std::vector<int> itemWeight, std::vector<double> itemCost);
	double knapsackFalseWeightPrint(int maxWeight, std::vector<int> falseWeight, std::vector<int> itemWeight, std::vector<double> itemCost);

	// find index that makes maximum cost
	std::vector<int> knapsack(int maxWeight, std::vector<int> itemWeight, std::vector<double> itemCost);
}

#endif
