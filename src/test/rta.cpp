#include "rta.h"

rta::rta()
: _core(0), _size(0), _R(NULL), _S(NULL)
{
}

rta::~rta()
{
  if (_R != NULL){
    free(_R);
    _R = NULL;
  }

  if (_S != NULL){
    free(_S);
    _S = NULL;
  }
}

/*
 * Setmodel()
 *
 * use input as task set
 */

void rta::Setmodel(TaskSet model){
  _model.clear();
  for (int i=0;i<model.length();i++){
    if (model.getTask(i).getPeriod() != 0)
      _model.putTask(model.getTask(i));
  }
  _size = _model.length();



  if (_R != NULL){
    free(_R);
    _R = NULL;
  }

  if (_S != NULL){
    free(_S);
    _S = NULL;
  }

  _R = (double*)malloc(_size*sizeof(double));
  _S = (double*)malloc(_size*sizeof(double));

  for (int i=0;i<_size;i++){
    _R[i] = _model.getTask(i).getDeadline();
    _S[i] = 0;
  }
}

/*
 * Simulate()
 *
 * check this model is scheduable with rta algorithm
 */

int rta::Simulate(){
  double *Rp = (double*)malloc(_size*sizeof(double)); // previous is smaller than Dk
  bool diff = true;

  if (_size == 0)
    return 1;
  while(diff){
    diff = false;
    /*    k++;
          if (k>(1<<_size)){
          std::cout<<"repeated more than expected. now is "<<k<<" with model size "<<_size<<std::endl;
          }
          */  
    for (int i=0;i<_size;i++)
      Rp[i] = _R[i];

    Calculate();

    std::cout<<std::endl;
    for (int i=0;i<_size;i++){
      std::cout<<_R[i]<<" ";
    }
    std::cout<<std::endl;

    for (int i=0;i<_size;i++){
      if (_R[i] > _model.getTask(i).getDeadline())
        break;
      else if (i == _size-1){
        free(Rp);
        return 1;
      }
    }
    for (int i=0;i<_size;i++){
      if (Rp[i] != _R[i]){
        diff = true;
        break;
      }
    }
  }

  free(Rp);
  return 0;
}

/*
 * Calculate()
 *
 * Calculate all variables.
 * compute once per each variable.
 */

void rta::Calculate(){
  Calculate_S();
  Calculate_R();
}

/*
 * Calculate_R()
 *
 * Calculate Rk ub for all k
 * must be called by Calculate()
 */

void rta::Calculate_R(){
  double *temp;

  temp = (double *)malloc(_size*sizeof(double));

  for (int k=0;k<_size;k++){
    temp[k] = 0;
    for (int i=0;i<_size;i++){
      if (i==k)
        continue;

      temp[k] += Min(Calculate_W(i, _R[k]), Calculate_I(k, i), _R[k]-_model.getTask(k).getExecutionTime()+1);
    }
    temp[k] /= (double)Getcore();

    temp[k] = _model.getTask(k).getExecutionTime() + floor(temp[k]);
  }

  for (int k=0;k<_size;k++)
    _R[k] = temp[k];

  free(temp);
}

/*
 * Calculate_W()
 *
 * Calculate Wi(L)
 */

double rta::Calculate_W(int i, double l){
  double temp;
  double result;

  temp = l + _model.getTask(i).getDeadline() - _model.getTask(i).getExecutionTime() - _S[i];

  result = floor(temp/_model.getTask(i).getPeriod());
  temp = temp - result*_model.getTask(i).getPeriod();
  temp = (_model.getTask(i).getExecutionTime() < temp) ? _model.getTask(i).getExecutionTime() : temp;
//  std::cout<<"W"<<i<<", "<<l<<": "<<result * _model.getTask(i).getExecutionTime()+temp<<std::endl;
  return result * _model.getTask(i).getExecutionTime() + temp;

}

/*
 * Calculate_S()
 *
 * Calculate Sk lb for all k
 * must be called by Calculate()
 */

void rta::Calculate_S(){
  for (int i=0;i<_size;i++){
    _S[i] = _model.getTask(i).getDeadline() - _R[i]; // Di - Ri
  }
}

/*
 * Calculate_I()
 *
 * Calculate Ik i
 */

double rta::Calculate_I(int k, int i){
  double temp;
  double temp2;

    temp = floor(_model.getTask(k).getDeadline() / _model.getTask(i).getPeriod()); // [Dk/Ti]
    temp2 = temp*_model.getTask(i).getExecutionTime(); // [Dk/Ti]*Ci

    temp = _model.getTask(k).getDeadline() - temp * _model.getTask(i).getPeriod() - _S[i]; // Dk mod Ti - Si
    if (temp < 0)
      temp = 0; // (Dk mod Ti - Si)0
    double result =  temp2 + ((_model.getTask(i).getExecutionTime() < temp) ? _model.getTask(i).getExecutionTime() : temp); // [Dk/Ti]*Ci + min(Ci, (Dk mod Ti - Si)0)
    return result;
}

/*
 * Min()
 *
 * return minimum input
 */

double rta::Min(double a, double b, double c){
  double result = (a < b) ? a : b;

  return (result < c) ? result : c;
}

void rta::Setcore(int core){
  _core = core;
}

int rta::Getcore(){
  return _core;
}

bool rta::Ml(){
  int period_max = _model.getTask(0).getPeriod();
  for (int i=0; i<_size; i++){
    period_max = (period_max > _model.getTask(i).getPeriod()) ? period_max : _model.getTask(i).getPeriod();
  }

  double temp;

  for (int t=1; t<50*period_max; t++){
    temp = 0;
    for (int i=0; i<_size; i++){
      temp += Md(i, t)/t;
    }

    if (temp > _core){
      return false;
    }
  }

  return true;
}

double rta::Md(int i, double t){
  int j = J(i,t);

  double result = j * _model.getTask(i).getExecutionTime();

  double temp = t - (j * _model.getTask(i).getPeriod() + _model.getTask(i).getDeadline() - _model.getTask(i).getExecutionTime());

  result += (0 > temp) ? 0 : temp;

  return result;
}

int rta::J(int i, double t){
  int temp = floor((t-_model.getTask(i).getDeadline())/_model.getTask(i).getPeriod()) + 1;
  return (0 > temp) ? 0 : temp;
}

