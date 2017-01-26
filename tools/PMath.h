#ifndef __P_MATH__
#define __P_MATH__

#include <vector>
#include <cstdlib>
#include <algorithm>
#include <ctime>
#include <cmath>

namespace PMath 
{
	int GCD(int a, int b);
	int GCD(std::vector<int> arr);
	int LCM(int a, int b);
	int LCM(std::vector<int> arr);
	std::vector<double> Unifast(int n, double total);
	std::vector<double> kMin(std::vector<double> arr, int k);
	std::vector<double> kMax(std::vector<double> arr, int k);
}

#endif