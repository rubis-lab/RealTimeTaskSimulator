#include "FileIO.h"

namespace FileIO 
{
	void goToLine(std::ifstream& file, int num) {
		file.seekg(std::ios::beg);
		for(int i = 0; i < num - 1; ++i){
			file.ignore(std::numeric_limits<std::streamsize>::max(),'\n');
		}
		return;
	}

	void jumpLine(std::ifstream& file, int num) {
		for(int i = 0; i < num; ++i){
			file.ignore(std::numeric_limits<std::streamsize>::max(),'\n');
		}
		return;
	}
}