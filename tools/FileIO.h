#ifndef __FILEIO__
#define __FILEIO__

#include <iostream>
#include <fstream>
#include <string>
#include <limits>

namespace FileIO 
{
	void goToLine(std::ifstream& file, int num);
	void jumpLine(std::ifstream& file, int num);
}

#endif