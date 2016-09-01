// Downloaded from
// http://www.bogotobogo.com/cplusplus/multithreaded2.php

#include <iostream>
#include <memory>
#include <cassert>
#include <windows.h>
#include <process.h>

class Runnable {
public:
	virtual void* run() = 0;
	virtual ~Runnable() = 0;
};

// Pure virtual destructor: function body required
Runnable::~Runnable(){};

class Thread {
public:
	Thread(std::auto_ptr<Runnable> run);
	Thread();
	virtual ~Thread();
	void start();
	void* join();
private:
	HANDLE hThread;
	unsigned wThreadID;
	// runnable object will be deleted automatically
	std::auto_ptr<Runnable> runnable;
	Thread(const Thread&);
	const Thread& operator=(const Thread&);
	// called when run() completes
	void setCompleted();
	// stores return value from run()
	void* result;
	virtual void* run() {return 0;}
	static unsigned WINAPI startThreadRunnable(LPVOID pVoid);
	static unsigned WINAPI startThread(LPVOID pVoid);
	void printError(LPTSTR lpszFunction, LPSTR fileName, int lineNumber);
};

Thread::Thread(std::auto_ptr<Runnable> r) : runnable(r) {
	if(!runnable.get())
		printError("Thread(std::auto_ptr<Runnable> r) failed at ",
				__FILE__, __LINE__);
	hThread = 
		(HANDLE)_beginthreadex(NULL,0,Thread::startThreadRunnable,
				(LPVOID)this, CREATE_SUSPENDED, &wThreadID;);
	if(!hThread)
		printError("_beginthreadex failed at ",__FILE__, __LINE__);
}

Thread::Thread() : runnable(NULL) {
	hThread = 
		(HANDLE)_beginthreadex(NULL,0,Thread::startThread,
				(LPVOID)this, CREATE_SUSPENDED, &wThreadID;);
	if(!hThread)
		printError("_beginthreadex failed at ",__FILE__, __LINE__);
}

unsigned WINAPI Thread::startThreadRunnable(LPVOID pVoid) {
	Thread* runnableThread = static_cast<Thread*>(pVoid);
	runnableThread->result = runnableThread->runnable->run();
	runnableThread->setCompleted();
	return reinterpret_cast<unsigned>(runnableThread->result);
}

unsigned WINAPI Thread::startThread(LPVOID pVoid) {
	Thread* aThread = static_cast<Thread*>(pVoid);
	aThread->result = aThread->run();
	aThread->setCompleted();
	return reinterpret_cast<unsigned>(aThread->result);
}

Thread::~Thread() {
	if(wThreadID != GetCurrentThreadId()) {
		DWORD rc = CloseHandle(hThread);
		if(!rc) printError
			("CloseHandle failed at ",__FILE__, __LINE__);
	}
}

void Thread::start() {
	assert(hThread);
	DWORD rc = ResumeThread(hThread);
	// thread created is in suspended state, 
	// so this starts it running
	if(!rc) printError
			("ResumeThread failed at ",__FILE__, __LINE__);
}

void* Thread::join() {
	// A thread calling T.join() waits until thread T completes.
	return result;
}

void Thread::setCompleted() {
	// Notify any threads that are waiting in join()
}

void Thread::printError(LPSTR lpszFunction, LPSTR fileName, int lineNumber)
{
	TCHAR szBuf[256];
	LPSTR lpErrorBuf;
	DWORD errorCode=GetLastError();
	FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER||
		FORMAT_MESSAGE_FROM_SYSTEM,
		NULL,
		errorCode,
		MAKELANGID(LANG_NEUTRAL,SUBLANG_DEFAULT),
		(LPTSTR)&lpErrorBuf;,
		0,
		NULL);
	wsprintf(szBuf,"%s failed at line %d in %s with error %d: %s", 
		     lpszFunction, lineNumber, fileName, errorCode, lpErrorBuf);
	DWORD numWritten; 
	WriteFile(GetStdHandle(STD_ERROR_HANDLE),
		szBuf,
		strlen(reinterpret_cast <const char *> (szBuf)),
		&numWritten;,
		FALSE);
	LocalFree(lpErrorBuf);
	exit(errorCode);
}

class simpleRunnable: public Runnable {
public:
	simpleRunnable(int ID) : myID(ID) {}
	virtual void* run() {
		std::cout << "Thread " << myID << " is running" << std::endl;
		return reinterpret_cast<void*>(myID);
	}
private:
	int myID;
};

class simpleThread: public Thread {
public:
	simpleThread(int ID) : myID(ID) {}
	virtual void* run() {
		std::cout << "Thread " << myID << " is running" << std::endl;
		return reinterpret_cast<void*>(myID);
	}
private:
	int myID;
};

int main() {
	// thread1 and thread2 are created on the heap
	// thread3 is created on the stack
	// The destructor for thread1 and thread2 will automatically 
	// delete the thread objects.
	std::auto_ptr<Runnable> r(new simpleRunnable(1));
	std::auto_ptr<Thread> thread1(new Thread(r));
	thread1->start();
	std::auto_ptr<simpleThread> thread2(new simpleThread(2));
	thread2->start();
	simpleThread thread3(3);
	thread3.start();
	// wait for the threads to finish
	int result1 = reinterpret_cast<int>(thread1->join());
	int result2 = reinterpret_cast<int>(thread2->join());
	int result3 = reinterpret_cast<int>(thread3.join());
	std::cout << result1 << ' ' << result2 << ' ' << result3 
		<< std::endl;
	return 0;
}