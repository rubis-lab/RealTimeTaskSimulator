TARGET = bin/rtts
CC = g++

#release option
ifeq ($(RELEASE), 1)
	CFLAGS = -O1 -std=c++11 -I$(LIB_OPTION)
else
	CFLAGS = -g -Wall -std=c++11 -I$(LIB_OPTION) \
		-D__DEBUG=1
endif

#sources
SOURCE = \
	../src/Test.cpp	\
	../src/algorithm/Knapsack.cpp	\
	../src/container/Param.cpp	\
	../src/container/Task.cpp	\
	../src/container/TaskSet.cpp	\
	../src/container/Thread.cpp	\
	../src/dataproc/ExperimentLogger.cpp	\
	../src/experiment/Experiment.cpp	\
	../src/experiment/SimpleExperiment.cpp	\
	../src/experiment/BCLExperiment.cpp	\
	../src/experiment/BARExperiment.cpp	\
	../src/experiment/BARModExperiment.cpp	\
	../src/experiment/TestExperiment.cpp	\
	../src/generator/Generator.cpp	\
	../src/generator/SimpleGenerator.cpp	\
	../src/generator/MarcoGenerator.cpp	\
	../src/generator/UniFastGenerator.cpp	\
	../src/generator/NormalGenerator.cpp	\
	../src/generator/VarianceGenerator.cpp	\
	../src/ops/TaskUtil.cpp	\
	../src/ops/TaskSetUtil.cpp	\
	../src/ops/TaskParallelizer.cpp	\
	../src/ops/TaskSetParallelizer.cpp	\
	../src/test/NEC.cpp	\
	../src/test/GFB.cpp \
	../src/test/BCL.cpp \
	../src/test/BAR.cpp	\
	../src/test/BARMod.cpp	\
	../tools/CRand.cpp	\
	../tools/PMath.cpp	\
	../tools/FileIO.cpp	


#more includes
INCLUDE = -I.
LIBS = 
LIB_OPTION = -lpthread

#dependency dest
VPATH = ../src	\
	../src/algorithm	\
	../src/container	\
	../src/dataproc	\
	../src/experiment	\
	../src/generator	\
	../src/ops	\
	../src/test	\
	../tools

#objects
OBJ = $(join $(addsuffix ../obj/, $(dir $(SOURCE))), $(notdir $(SOURCE:.cpp=.o))) 

all: $(TARGET)
	@true

clean:
	@-rm -f $(TARGET) $(OBJ)
	@rm -rf ../obj
	@rm -rf ../src/obj
	@rm -rf bin

$(TARGET): $(OBJ)
	@echo "Linking ... $@"
	@mkdir -p bin
	@cd bin
	@$(CC) $(CFLAGS) -o $@ $^ $(LIBS)

../src/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../src/algorithm/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)	

../src/container/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)	

../src/dataproc/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)	

../src/experiment/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../src/generator/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../src/ops/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../src/test/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../tools/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)