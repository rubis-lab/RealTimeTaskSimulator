#include "bar.h"

  bar::bar()
: _core(0), _size(0), _Uk(NULL), _Utot(0), _Csum(0), _Aupper(NULL)
{

}
  bar::~bar()
{
  if (_Uk != NULL){
    free(_Uk);
    _Uk = NULL;
  }

  if (_Aupper != NULL){
    free(_Aupper);
    _Aupper = NULL;
  }
}

/*
 * Setmodel()
 * 
 * use input as task set
 */

void bar::Setmodel(TaskSet model){
  _model.clear();
  for (int i=0;i<model.length();i++){
    if (model.getTask(i).getPeriod() != 0)
      _model.putTask(model.getTask(i));
  }
  _size = _model.length();
}

/*
 * Simulate()
 *
 * check this model is schedulable with bar algorithm
 */

int bar::Simulate(){
  if (!Check_core())
    return -1;

  Calculate_Utot();
  Calculate_Csum();
  Calculate_Aupper();

  double temp;
  double rhs;
  for (int k=0;k<_size;k++){
//    std::cout<<k<<"/"<<_size<<": " << _model[k]->compute<<" " <<_model[k]->deadline<<" "<<_model[k]->period<<" Aupper="<<_Aupper[k]<<std::endl;
    for (int A=0;A<=_Aupper[k];A++){
      temp = 0;
      for (int i=0;i<_size;i++){
        temp += Calculate_Iprime(k, i, A);
      }

      rhs = Getcore()*(A + _model.getTask(k).getDeadline() - _model.getTask(k).getExecutionTime());

      if (temp + Calculate_Iepsilon(k, A) > rhs){
          return 0;
      }
//      std::cout <<temp<< " "<<Calculate_Iepsilon(k,A) <<" < "<<Getcore()<<"* ("<<A<<" + " << _model[k]->deadline<<" - "<<_model[k]->compute<<")"<<std::endl;
    }
  }

  return 1;
}

/*
 * Calculate_Utot()
 *
 * compute Utot
 */

void bar::Calculate_Utot(){
  _Utot = 0;
  if (_Uk != NULL){
    free(_Uk);
    _Uk = NULL;
  }

  _Uk = (double *)malloc(sizeof(double)*_size);

  for (int i = 0; i<_size; i++){
    _Uk[i] = _model.getTask(i).getExecutionTime()/_model.getTask(i).getPeriod();
    _Utot += _Uk[i];
  }
}

/*
 * Calculate_Csum()
 *
 * compute CΣ
 */

void bar::Calculate_Csum(){
  _Csum=0;

  double *temp;
  int min;
  double max = 0;
  temp = (double*)malloc((_size)*sizeof(double));

  for (int i=0;i<_size;i++){
    temp[i] = _model.getTask(i).getExecutionTime();
    max += temp[i];
  }

  if (Getcore()-1 >= _size){
    for (int i=0;i<_size;i++)
      _Csum += temp[i];
  }
  else {
    for (int i=0;i<Getcore()-1;i++){
      min = 0;
      for (int j=0;j<_size;j++){
        if (temp[min] > temp[j])
          min = j;
      }

      _Csum += temp[min];
      temp[min] = max;
    }
  }
  free(temp);
}

/*
 * Calculate_Aupper()
 *
 * compute Ak upper bound in bar theorem
 */

void bar::Calculate_Aupper(){
  if (_Utot == 0){
    Calculate_Utot();
  }
  if (_Aupper != NULL){
    free(_Aupper);
    _Aupper = NULL;
  }

  _Aupper = (double *)malloc(sizeof(double)*_size);

  double temp;
  for (int i=0; i<_size; i++){
    temp = 0;
    for (int j = 0; j<_size; j++){
      temp += (_model.getTask(j).getPeriod() - _model.getTask(j).getDeadline())*_Uk[j];
    }

    temp += _Csum - _model.getTask(i).getDeadline()*(Getcore()-_Utot) + Getcore()*_model.getTask(i).getExecutionTime();
    temp /= (Getcore()- _Utot);

    _Aupper[i] = temp;
  }
}

/*
 * Calculate_dbf()
 *
 * compute dbf function from input
 */

double bar::Calculate_dbf(int i, double t){

  return ((floor((t-_model.getTask(i).getDeadline())/_model.getTask(i).getPeriod()))+1)*_model.getTask(i).getExecutionTime();
}

/*
 * Calculate_Iprime()
 *
 * compute I'k(τi)
 * MIN(first, second)
 */

double bar::Calculate_Iprime(int k, int i, int A){
  double first = Calculate_dbf(i, (A+_model.getTask(k).getDeadline()));
  double second;

  if (i != k){
    second = A+_model.getTask(k).getDeadline() - _model.getTask(k).getExecutionTime();
  }
  else {
    first -= _model.getTask(k).getExecutionTime();
    second = A;
  }

  return (first < second) ? first : second;
}

/*
 * Calculate_Itworpime()
 * 
 * compute I''k(τi)
 * MIN(first + MIN(first_first, first_second), second)
 */

double bar::Calculate_Itwoprime(int k, int i, int A){
    double first_first = _model.getTask(i).getExecutionTime();
    double first_second = ((A+_model.getTask(k).getDeadline()) - floor((A+_model.getTask(k).getDeadline())/_model.getTask(i).getPeriod())*_model.getTask(i).getPeriod());
    double first = (floor((A+_model.getTask(k).getDeadline())/_model.getTask(i).getPeriod()))*_model.getTask(i).getExecutionTime();
    double second;

    if (i != k){
      first = first + ((first_first<first_second) ? first_first : first_second);
      second = A+_model.getTask(k).getDeadline() - _model.getTask(k).getExecutionTime();
    }
    else {
      first = first + ((first_first<first_second) ? first_first : first_second) - _model.getTask(k).getExecutionTime();
      second = A;
    }

    return (first < second) ? first : second;
}

/*
 * Calculate_Iepsilon()
 * 
 * compute Iεk
 */

double bar::Calculate_Iepsilon(int k, int A){
  double *temp;
  double result = 0;
  double max = 0;
  int min;
  temp = (double*)malloc((_size)*sizeof(double));

  for (int i=0; i<_size; i++){
    temp[i] = Calculate_Itwoprime(k, i, A) - Calculate_Iprime(k, i, A);
  }

  if (Getcore()-1 >= _size){
    for (int i=0;i<_size;i++)
      result += temp[i];
  } else {
    for (int i=0;i<_size;i++)
      max += temp[i];

    // find i-th smallest 
    for (int i=0;i<Getcore()-1;i++){
      min = 0;
      for (int j=0;j<_size;j++){
        if (temp[min] > temp[j])
          min = j;
      }

      result += temp[min];
      temp[min] = max;
    }
  }

  free(temp);

  return result;
}

/*
 * Print_Aupper()
 * 
 * print Aupper
 */

void bar::Print_Aupper(){
  if (!Check_Aupper())
    return;

  for (int i=0 ;i<_size; i++){
    std::cout << _Aupper[i] << std::endl;
  }
}

/*
 * Check_core()
 * 
 * check core has set or not
 */

bool bar::Check_core(){
  if (Getcore() < 1){
    std::cout<< "(bar.cpp) Check core number :" << Getcore()<<std::endl;
    return false;
  }

  return true;
}

/*
 * Check_Aupper()
 * 
 * check Aupper has calculated or not
 */

bool bar::Check_Aupper(){
  if (_Aupper==NULL){
    std::cout<<"(bar.cpp) uncalculated Aupper"<<std::endl;
    return false;
  }

  return true;
}

void bar::Setcore(int core){
  _core = core;
}

int bar::Getcore(){
  return _core;
}

bool bar::Ml(){
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

double bar::Md(int i, double t){
  int j = J(i,t);

  double result = j * _model.getTask(i).getExecutionTime();

  double temp = t - (j * _model.getTask(i).getPeriod() + _model.getTask(i).getDeadline() - _model.getTask(i).getExecutionTime());

  result += (0 > temp) ? 0 : temp;

  return result;
}

int bar::J(int i, double t){
  int temp = floor((t-_model.getTask(i).getDeadline())/_model.getTask(i).getPeriod()) + 1;
  return (0 > temp) ? 0 : temp;
}
