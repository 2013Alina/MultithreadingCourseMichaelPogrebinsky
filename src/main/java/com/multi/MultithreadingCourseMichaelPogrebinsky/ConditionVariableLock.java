package com.multi.MultithreadingCourseMichaelPogrebinsky;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionVariableLock {
    // How to use condition variable?
    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        String username = null;
        String password = null;

        // 1. await() method
        lock.lock();
        try {
            while (username == null || password == null) {
                condition.await(); // разблокирует блокировку и переведет поток в спящий режим, ждать сигнала
            }
        } finally {
            lock.unlock();
        }

        // 2. signal() method
        lock.lock();
        try {
            // username = userTextbox.getText();
            // password = passwordTextbox.getText();
            if (username != null || password != null) {
                condition.signal(); // пробуждает один поток, ожидающий condition variable
            }
        } finally {
            lock.unlock();
        }

        // 3. awaitNanos() method
        condition.awaitNanos(5);
        // ждать не дольше чем nanosTimeout

        // 4. boolean await(long time, TimeUnit unit) method - ждать не дольше в заданых единицах времени
        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Thread 1 waiting");
                condition.await(5, TimeUnit.SECONDS); // ждем 5 секунд
                System.out.println("Thread 1 resumed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Thread 2 signaling");
                condition.signal();
            } finally {
                lock.unlock();
            }
        });

        t1.start();
        try {
            Thread.sleep(1000); // ждем 1 секунду перед подачей сигнала
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.start();

        // 5. boolean awaitUntil(Date deadline) method - проснуться раньше Даты крайнего срока
        // Поток, который будет ожидать до определенного времени
        Thread waitingThread = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Thread 1: Waiting until a specific time...");

                // Устанавливаем крайний срок ожидания (например, через 5 секунд)
                Date deadline = new Date(System.currentTimeMillis() + 5000);

                // Ожидаем сигнала до указанного времени
                boolean signaled = condition.awaitUntil(deadline);

                if (signaled) {
                    System.out.println("Thread 1: Received a signal before the deadline!");
                } else {
                    System.out.println("Thread 1: The deadline passed without receiving a signal.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });

        // Поток, который текущий поток, который подаст сигнал через 3 секунды
        Thread signalingThread = new Thread(() -> {
            try {
                Thread.sleep(3000); // Ждем 3 секунды перед подачей сигнала
                lock.lock();
                try {
                    System.out.println("Thread 2: Sending a signal.");
                    condition.signal();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // Запуск потоков
        waitingThread.start();
        signalingThread.start();

        try {
            waitingThread.join();
            signalingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
