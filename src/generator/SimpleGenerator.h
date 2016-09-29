#ifndef __SIMPLE_GEN__
#define __SIMPLE_GEN__

class SimpleGenerator: public Generator
{
	private:
	public:
		SimpleGenerator();
		int loadConfig(std::ifstream &file);
		int saveConfig(void);
}
#endif