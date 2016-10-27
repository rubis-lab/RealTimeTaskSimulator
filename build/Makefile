TARGET=bin/rtts
CC  = g++
CFLAGS = -Wall -O1 -std=c++11 -I$(LIB_OPTION)
LIBS = 

#sources
SOURCE = \
	../src/container/Param.cpp	\
	../src/container/Task.cpp	\
	../src/container/TaskSet.cpp	\
	../src/container/Experiment.cpp	\
	../src/generator/Generator.cpp	\
	../src/generator/SimpleGenerator.cpp	\
	../tools/CRand.cpp	\
	../tools/FileIO.cpp	\
	../src/test/GFB.cpp \
	../src/Test.cpp	

#more includes
INCLUDE = -I.
LIB_OPTION = -lpthread

#dependency dest
VPATH=../src:../src/container:../src/generator:../src/test:../tools

#objects
OBJ=$(join $(addsuffix ../obj/, $(dir $(SOURCE))), $(notdir $(SOURCE:.cpp=.o))) 

all: $(TARGET)
	@true

clean:
	@-rm -f $(TARGET) $(OBJ)
	@rm -rf ../obj
	@rm -rf ../src/obj
	@rm -rf bin

$(TARGET): $(OBJ)
	@echo "Linking the target $@"
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

../src/generator/../obj/%.o: %.cpp
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