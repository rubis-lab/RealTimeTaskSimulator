#ifndef __JHAHN_GEN__
#define __JHAHN_GEN__

#include "../generator/Generator.h"
#include <math.h>


class JHAhnGenerator : public Generator
{
  private:
    int init(int number);

  public:
    JHAhnGenerator();
    JHAhnGenerator(Param *paramExt, CRand *crExt);

    Task generateTask();
    TaskSet generateTaskSet();
};
#endif




