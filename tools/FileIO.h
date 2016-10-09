#ifndef __FILEIO__
#define __FILEIO__

#include <iostream>
#include <fstream>
#include <string>
#include <limits>

namespace FileIO 
{
	std::ifstream& goToLine(std::ifstream&, unsigned int);
	std::ifstream& jumpLine(std::ifstream&, unsigned int);
}

#endif