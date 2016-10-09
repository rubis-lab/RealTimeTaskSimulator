#include <CRand.h>

CRand::CRand()
{
	eng.seed((unsigned int)time(NULL));
}

CRand::CRand(int seed)
{
	eng.seed(seed);
}

int CRand::changeSeed(int seed)
{
	eng.seed(seed);
	return 1;
}

double CRand::uniform(double a, double b)
{
	std::tr1::uniform_real<double> unif(a, b);
	return unif(eng);
}

double CRand::normal(double mu, double sig)
{
	std::tr1::normal_distribution<double> norm(mu, sig);
	return norm(eng);
}

double CRand::binomial(int n, double p)
{
	std::tr1::binomial_distribution<int, double> bin(n, p);
	return bin(eng);
}

double CRand::exponential(double lmbd)
{
	std::tr1::exponential_distribution<double> expn(1.0 / lmbd);
	return expn(eng);
}