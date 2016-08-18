TARGET=rtts
CC  = g++
CFLAGS = -Wall -O1 -std=c++11 -I$(LIB_OPTION)
LIBS = 

#sources
SOURCE = \
	Task.cpp	\
	TaskSet.cpp	\
	Experiment.cpp	\
	Test.cpp

#more includes
INCLUDE = -I.

LIB_OPTION = -lpthread 

#dependency dest
VPATH=
OBJ=$(join $(addsuffix ../obj/, $(dir $(SOURCE))), $(notdir $(SOURCE:.cpp=.o))) 
DEPENDS=$(join $(addsuffix ../.dep/, $(dir $(SOURCE))), $(notdir $(SOURCE:.cpp=.d)))


all: $(TARGET)
	@true

clean:
	@-rm -f $(TARGET) $(OBJ) $(DEPENDS)

$(TARGET): $(OBJ)
	@echo "Linking the target $@"
	@$(CC) $(CFLAGS) -o $@ $^ $(LIBS)

%.o : %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<"
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../obj/%.o : %.cpp
	@mkdir -p $(dir $@)
	@echo "Compiling $<"
	@$(CC) $(CFLAGS) -c $< -o $@ $(LIBS)

../.dep/%.d: %.cpp
	@mkdir -p $(dir $@)
	@echo $*.o
	@$(SHELL) -ec '$(CC) -M $(CFLAGS) $< | sed "s^$*.o^../obj/$*.o^" > $@' $(LIBS)

-include $(DEPENDS)