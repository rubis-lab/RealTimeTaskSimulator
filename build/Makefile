TARGET = bin/rtts
CC = g++
CFLAGS = -g -Wall -O1 -std=c++11 -I$(LIB_OPTION)
LIBS = 

#sources
SOURCE = \
	../src/Test.cpp	\
	../src/container/Param.cpp	\
	../src/container/Task.cpp	\
	../src/container/TaskSet.cpp	\
	../src/container/ParallelizableTaskSet.cpp	\
	../src/experiment/Experiment.cpp	\
	../src/experiment/SimpleExperiment.cpp	\
	../src/experiment/BCLExperiment.cpp	\
	../src/generator/Generator.cpp	\
	../src/generator/SimpleGenerator.cpp	\
	../src/generator/MarcoGenerator.cpp	\
	../src/ops/TaskModifier.cpp	\
	../src/ops/TaskSetModifier.cpp	\
	../src/ops/TaskUtil.cpp	\
	../src/ops/TaskSetUtil.cpp	\
	../src/test/NEC.cpp	\
	../src/test/GFB.cpp \
	../src/test/BCL.cpp \
	../tools/CRand.cpp	\
	../tools/PMath.cpp	\
	../tools/FileIO.cpp	


#more includes
INCLUDE = -I.
LIB_OPTION = -lpthread

#dependency dest
VPATH = ../src	\
	../src/container	\
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

../src/container/../obj/%.o: %.cpp
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