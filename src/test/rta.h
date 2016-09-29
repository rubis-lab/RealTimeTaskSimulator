#pragma once

#include "../container/TaskSet.h"

class rta {
  public:
    rta();
    ~rta();
    void Setmodel(TaskSet);
    int Simulate(); // -1 for error, 0 for fail, 1 for success
    void Calculate();
    void Calculate_R();
    double Calculate_W(int i, double l);
    void Calculate_S();
    double Calculate_I(int k, int i);
    double Min(double, double, double);
    void Setcore(int);
    int Getcore();
    bool Ml();
    double Md(int i, double t);
    int J(int i, double t);



  private:
    int _core;
    TaskSet _model;
    int _size;
    double *_R;
    double *_S;
};
