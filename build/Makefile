TARGET=bin/rtts
CC  = g++
CFLAGS = -Wall -O1 -std=c++11 -I$(LIB_OPTION)
LIBS = 

#sources
SOURCE = \
	../src/Task.cpp	\
	../src/TaskSet.cpp	\
	../src/Experiment.cpp	\
	../src/Test.cpp

#more includes
INCLUDE = -I.
LIB_OPTION = -lpthread

#dependency dest
VPATH=../src

#objects
OBJ=$(join $(addsuffix ../obj/, $(dir $(SOURCE))), $(notdir $(SOURCE:.cpp=.o))) 

all: $(TARGET)
	@true

clean:
	@-rm -f $(TARGET) $(OBJ)
	@rm -rf ../obj

$(TARGET): $(OBJ)
	@echo "Linking the target $@"
	@$(CC) $(CFLAGS) -o $@ $^ $(LIBS)

../src/../obj/%.o: %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<" 
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)