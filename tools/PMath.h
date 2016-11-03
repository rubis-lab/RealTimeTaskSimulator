#ifndef __P_MATH__
#define __P_MATH__

#include <vector>
#include <cstdlib>
#include <ctime>
#include <cmath>

namespace PMath 
{
	int GCD(int a, int b);
	int GCD(std::vector<int> arr);
	int LCM(int a, int b);
	int LCM(std::vector<int> arr);
	std::vector<double> Unifast(int n, double total);
}

#endif