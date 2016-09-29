#include "fileio.h"
FileIO::FileIO
{

}

std::ifstream& FileIO::goToLine(std::ifstream& file, int num) {
	file.seekg(ios::beg);
	for(int i = 0; i < num - 1; ++i){
		file.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	return file;
}

std::ifstream& FileIO::jumpline(std::ifstream& file, int num) {
	for(int i = 0; i < num; ++i){
		file.ignore(numeric_limits<streamsize>::max(),'\n');
	}
	return file;
}