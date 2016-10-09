#ifndef __C_RAND__
#define __C_RAND__

// Macros for tr1 library
// For windows use <random>
#ifdef _WIN32
	#include <random>
#else
	#include <tr1/random>
#endif
#include <ctime>

// Types
#define _RAND_MT_ENG	std::tr1::mt19937 eng
#define _RAND_SC_ENG	std::tr1::ranlux_base_01 eng

// Initialization
#define _RAND_SET_SEED(S)	eng.seed(S)
#define _RAND_SET_SEED_DEFAULT eng.seed((unsigned int)time(NULL))
#define _RAND_CLR(LBL)	LBL.reset()

// Distribution filters
#define _RAND_SET_UNIFORM(LBL, A, B)	std::tr1::uniform_real<double> LBL(A, B)
#define _RAND_SET_NORMAL(LBL, MU, SIG) 	std::tr1::normal_distribution<double> LBL(MU, SIG)
#define _RAND_SET_BINOMIAL(LBL, N, P)	std::tr1::binomial_distribution<int, double> LBL(N, P)
#define _RAND_SET_EXPONENTIAL(LBL, LMBD) std::tr1::exponential_distribution<double> LBL(1.0 / LMBD)

// Generation
#define _RAND_NEXT(LBL)	LBL(eng)

// Methods
class CRand
{
private:
	std::tr1::ranlux_base_01 eng;
public:
	CRand();
	CRand(int seed);
	int changeSeed(int seed);
	double uniform(double a, double b);
	double normal(double a, double b);
	double binomial(double a, double b);
	double exponential(double a, double b);
};

#endif