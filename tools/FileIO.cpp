#include "fileio.h"
namespace FileIO 
{
	std::ifstream& goToLine(std::ifstream& file, int num) {
		file.seekg(ios::beg);
		for(int i = 0; i < num - 1; ++i){
			file.ignore(numeric_limits<streamsize>::max(),'\n');
		}
		return file;
	}

	std::ifstream& jumpLine(std::ifstream& file, int num) {
		for(int i = 0; i < num; ++i){
			file.ignore(numeric_limits<streamsize>::max(),'\n');
		}
		return file;
	}
}