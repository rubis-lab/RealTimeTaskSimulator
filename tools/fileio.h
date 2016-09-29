#ifndef __FILEIO__
#define __FILEIO__

#include <iostream>
#include <fstream>
#include <string>
#include <limits>

class FileIO 
{
private:

public:
	FileIO();
	std::ifstream& gotoline(std::ifstream&, unsigned int);
	std::ifstream& jumpline(std::ifstream&, unsigned int);
}


#endif