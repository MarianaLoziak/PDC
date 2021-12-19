package com.company;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MutexTest {
    public Mutex mutex = new Mutex();
    public int amount = 10;
    public int max = 10;

    @Test
    void lockTest() {
        mutex.lock();
        assertEquals(mutex.getCurrentThread().get(), Thread.currentThread());
    }

    @Test
    void unlockTest() {
        mutex.lock();
        mutex.unlock();
        assertEquals(mutex.getCurrentThread().get(), null);
    }

    @Test
    void casWaitTest() {


    }

    @Test
    void casNotifyTest() throws InterruptedException {
        amount = 10;
        Thread thread1 = new Thread(() ->{
            try {
                mutex.lock();
                while (amount >= max){
                    mutex.casWait();
                }
                amount++;
                mutex.casNotifyAll();
                mutex.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread thread2= new Thread(() ->{
            try {
                mutex.lock();
                while (amount == 0){
                    mutex.casWait();
                }
                amount--;
                mutex.casNotifyAll();
                mutex.unlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertEquals(mutex.getWaitedThreads().contains(thread1), false);
    }

    @Test
    void casNotifyAllTest() {

        Thread thread1 = new Thread(() -> {
            try {
                mutex.lock();
                mutex.casWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                mutex.lock();
                mutex.casWait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread3 = new Thread(() -> {
                mutex.notifyAll();
        });
        thread1.start();
        thread2.start();
        thread3.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(0,mutex.getWaitedThreads().size());
    }

    @Test
    void MutexTest() throws InterruptedException {
        List<Thread> threads = new ArrayList<>(6);
        threads.add(new Thread(() -> {
            try {
                increment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                increment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                increment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                decrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                decrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        threads.add(new Thread(() -> {
            try {
                decrement();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(10,amount);
    }

    public  void increment() throws InterruptedException {
        mutex.lock();
        while (amount >= max){
            mutex.casWait();
        }
        amount++;
        mutex.casNotifyAll();
        mutex.unlock();

    }

    public  void decrement() throws InterruptedException {
        mutex.lock();
        while (amount == 0){
            mutex.casWait();
        }
        amount--;
        mutex.casNotifyAll();
        mutex.unlock();
    }
}