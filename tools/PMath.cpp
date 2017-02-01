#include "PMath.h"

int PMath::GCD(int a, int b)
{
	if(a <= 0 || b <= 0) {
		return 0;
	}
	
	while(b > 0) {
		int tmp = b;
		b = a % b;
		a = tmp;
	}

	return a;
}

int PMath::GCD(std::vector<int> arr)
{
	if(arr.size() == 0) {
		return 0;
	}
	if(arr.size() < 2) {
		return arr[0];
	}

	int ret = arr[0];
	for(unsigned int i = 1; i < arr.size(); i++) {
		ret = PMath::GCD(ret, arr[i]);
	}

	return ret;
}

int PMath::LCM(int a, int b)
{
	if(a <= 0 || b <= 0) {
		return 0;
	}

	return a * b / PMath::GCD(a, b);
}

int PMath::LCM(std::vector<int> arr)
{
	if(arr.size() == 0) {
		return 0;
	}
	if(arr.size() < 2) {
		return arr[0];
	}

	int ret = arr[0];
	for(unsigned int i = 1; i < arr.size(); i++) {
		ret = PMath::LCM(ret, arr[i]);
	}

	return ret;
}

std::vector<double> PMath::kMin(std::vector<double> arr, int k)
{
	std::vector<double> ret;

	for(int i = 0; i < k; i++) {
		std::vector<double>::iterator it = std::min_element(std::begin(arr), std::end(arr));
		ret.push_back(*it);
		arr.erase(it);
	}

	return ret;
}

std::vector<double> PMath::kMax(std::vector<double> arr, int k)
{
	std::vector<double> ret;

	for(int i = 0; i < k; i++) {
		std::vector<double>::iterator it = std::max_element(std::begin(arr), std::end(arr));
		ret.push_back(*it);
		arr.erase(it);
	}

	return ret;
}