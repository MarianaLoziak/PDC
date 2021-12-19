package com.company;

import java.util.ConcurrentModificationException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

class Mutex {

    public AtomicReference<Thread> getCurrentThread() {
        return currentThread;
    }

    private AtomicReference<Thread> currentThread = new AtomicReference<>();

    public LinkedBlockingQueue<Thread> getWaitedThreads() {
        return waitedThreads;
    }

    private LinkedBlockingQueue<Thread> waitedThreads = new LinkedBlockingQueue<>();



    public void lock(){

        while (!currentThread.compareAndSet(null, Thread.currentThread())){
            Thread.yield();
        }

    }
    public void unlock(){
        if(!currentThread.get().equals(Thread.currentThread())){
            throw new ConcurrentModificationException("Thread wasn't locked");
        }
        currentThread.set(null);
    }

    public void casWait() throws InterruptedException {
        Thread current = Thread.currentThread();
        if (!current.equals(currentThread.get())){
            throw new ConcurrentModificationException("Thread wasn't locked before wait");
        }
        waitedThreads.put(current);

        //System.out.println("Waiting: " + Thread.currentThread().getName());
        unlock();
        while(waitedThreads.contains(current)) {
            Thread.yield();
        }
        lock();

        //System.out.println("No waiting: " + Thread.currentThread().getName());
    }

    public void casNotify() throws InterruptedException {
        Thread current = Thread.currentThread();
        if (!current.equals(currentThread.get())){
            throw new ConcurrentModificationException();
        }

        waitedThreads.take();
        System.out.println("");
    }

    public void casNotifyAll() throws InterruptedException {

        waitedThreads.clear();
    }
}


public class Main {
    public static void main(String[] args){

    }
}




