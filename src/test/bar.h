#pragma once

#include "../container/TaskSet.h"

class bar {
  public:
    bar();
    ~bar();
    void Setmodel(TaskSet);
    int Simulate(); // -1 for error, 0 for fail, 1 for success
    void Calculate_Utot();
    void Calculate_Csum();
    void Calculate_Aupper();
    double  Calculate_dbf(int i, double t); // -1 for error. return is int
    double Calculate_Iprime(int k, int i, int A); // return is int
    double Calculate_Itwoprime(int k, int i, int A); // return is int
    double Calculate_Iepsilon(int k, int A); // return is int
    void Print_Aupper();
    bool Check_core();
    bool Check_Aupper();
    void Setcore(int);
    int Getcore();
    bool Ml();
    double Md(int i, double t);
    int J(int i, double t);



  private:
    int _core;
    TaskSet _model;
    int _size;
    double *_Uk;
    double _Utot;
    double _Csum; // sum of the (m-1) largest Ck
    double *_Aupper; // upper bound of Ak
};
