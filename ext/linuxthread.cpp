// Downloaded from
// http://www.bogotobogo.com/cplusplus/multithreading_pthread.php

#include <iostream>
#include <pthread.h>
#include <cassert>
#include <error.h>

using namespace std;

class Runnable {
public:
	virtual void* run() = 0;
	virtual ~Runnable() = 0;
};

// Pure virtual destructor: function body required
Runnable::~Runnable(){};

class Thread {
public:
	Thread(auto_ptr<Runnable> run, bool isDetached = false);
	Thread(bool isDetached = false);
	virtual ~Thread();
	void start();
	void* join();
private:
	// thread ID
	pthread_t PthreadThreadID;
	// true if thread created in detached state
	bool detached;
	pthread_attr_t threadAttribute;
	// runnable object will be deleted automatically
	auto_ptr<Runnable> runnable;
	Thread(const Thread&);
	const Thread& operator=(const Thread&);
	// called when run() completes
	void setCompleted();
	// stores return value from run()
	void* result;
	virtual void* run() {}
	static void* startThreadRunnable(void* pVoid);
	static void* startThread(void* pVoid);
	void printError(char * msg, int status, char* fileName, int lineNumber);
};

Thread::Thread(auto_ptr<Runnable> r, bool isDetached) : 
		runnable(r), detached(isDetached) {
	if(!runnable.get()){
		cout << "Thread::Thread(auto_ptr<Runnable> r, bool isDetached)"\
		"failed at " << " " << __FILE__ <<":" << __LINE__ << "-" <<
		" runnable is NULL" << endl;
		exit(-1);
	}
}

Thread::Thread(bool isDetached) : runnable(NULL), detached(isDetached) {}

void* Thread::startThreadRunnable(void* pVoid) {
	// thread start function when a Runnable is involved
	Thread* runnableThread = static_cast<Thread*>(pVoid);
	assert(runnableThread);
	runnableThread->result = runnableThread->runnable->run();
	runnableThread->setCompleted();
	return runnableThread->result;
}

void* Thread::startThread(void* pVoid) {
	// thread start function when no Runnable is involved
	Thread* aThread = static_cast<Thread*>(pVoid);
	assert(aThread);
	aThread->result = aThread->run();
	aThread->setCompleted();
	return aThread->result;
}

Thread::~Thread() {}

void Thread::start() {
	// initialize attribute object
	int status = pthread_attr_init(&threadAttribute;);
	if(status) {
		printError("pthread_attr_init failed at", status,
			__FILE__, __LINE__);
		exit(status);
	}

	// set the scheduling scope attribute
	status = pthread_attr_setscope(&threadAttribute;,
					PTHREAD_SCOPE_SYSTEM);
	if(status) {
		printError("pthread_attr_setscope failed at", status,
			__FILE__, __LINE__);
		exit(status);
	}

	if(!detached) {
		if(!runnable.get()) {
			status = pthread_create(&PthreadThreadID;, &threadAttribute;,
				Thread::startThread, (void*)this);	
			if(status) {
				printError("pthread_create failed at", status,
					__FILE__, __LINE__);
				exit(status);
			}
		}
		else {
			status = pthread_create(&PthreadThreadID;, &threadAttribute;,
				Thread::startThreadRunnable, (void*)this);	
			if(status) {
				printError("pthread_create failed at", status,
					__FILE__, __LINE__);
				exit(status);
			}
		}
	}
	else {
		// set the detachstate attribute to detached
		status = pthread_attr_setdetachstate(&threadAttribute;,
						PTHREAD_CREATE_DETACHED);	
		if(status) {
			printError("pthread_attr_setdetachstate failed at", status,
			__FILE__, __LINE__);
			exit(status);
		}

		if(!runnable.get()) {
			status = pthread_create(&PthreadThreadID;, &threadAttribute;,
				Thread::startThread, (void*)this);	
			if(status) {
				printError("pthread_create failed at", status,
					__FILE__, __LINE__);
				exit(status);
			}
		}
		else {
			status = pthread_create(&PthreadThreadID;, &threadAttribute;,
				Thread::startThreadRunnable, (void*)this);	
			if(status) {
				printError("pthread_create failed at", status,
					__FILE__, __LINE__);
				exit(status);
			}
		}
	}
	status = pthread_attr_destroy(&threadAttribute;);
	if(status) {
		printError("pthread_attr_destroy failed at", status,
			__FILE__, __LINE__);
		exit(status);
	}
}


void* Thread::join() {
	// A thread calling T.join() waits until thread T completes.
	int status = pthread_join(PthreadThreadID, NULL);
	// result was already saved by thread start function
	if(status) {
		printError("pthread_join failed at", status,
			__FILE__, __LINE__);
		exit(status);
	}
	return result;
}

void Thread::setCompleted() {
// completion handled by pthread_join()
}

void Thread::printError(char * msg, int status, char* fileName, int lineNumber) {
	cout << msg << " " << fileName << ":" << lineNumber <<
		"-" << strerror(status) << endl;
}


// shared variable
int s = 0;

class communicatingThread: public Thread {
public:
	communicatingThread(int ID) : myID(ID) {}
	virtual void* run();
private:
	int myID;
};

void* communicatingThread::run() {
	cout << "Thread " << myID << " is running!" << endl;
	// increment s by million times
	for (int i = 0; i < 1000000; i++) s+=1;
	return 0;
}

int main() {

	auto_ptr<communicatingThread> thread1(new communicatingThread(1));
	auto_ptr<communicatingThread> thread2(new communicatingThread(2));
	thread1->start();
	thread2->start();
	thread1->join();
	thread2->join();

	cout << "s = " << s << endl; 
	return 0;
}