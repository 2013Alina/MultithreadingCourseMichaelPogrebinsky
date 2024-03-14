package com.multi.MultithreadingCourseMichaelPogrebinsky;

import java.util.ArrayList;
import java.util.List;

public class MultiExecutor {
    
    private final List<Runnable> tasks;

    public MultiExecutor(List<Runnable> tasks) {
        this.tasks = tasks;
    }
    
    public void executeAll() {
        List<Thread> threads = new ArrayList<>(tasks.size());
        
        for (Runnable task : tasks) {
            Thread thread = new Thread(task);
            threads.add(thread);
        }
        
        for(Thread thread : threads) {
            thread.start();
        }
    }
    
//    public void executeAll() {
//        for (Runnable task : tasks) {
//            Thread thread = new Thread(task);
//            thread.start();
//        }
//    }
    
    public static void main(String[] args) {
        TaskFirst taskFirst = new TaskFirst("Numbers");
        TaskSecond taskSecond = new TaskSecond("Alphabet");
        MultiExecutor multiExecutor = new MultiExecutor(List.of(taskFirst,taskSecond));
        multiExecutor.executeAll();
    }
    
    public static class TaskFirst implements Runnable {
        private String name;

        public TaskFirst(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Digit counter");
            System.out.println("Task " + name + " is executing in thread " + Thread.currentThread().getName());
            for (int i = 0; i < 50; i++) {
                System.out.println("Number = " + i);
            }
        }
    }
    
    public static class TaskSecond implements Runnable {
        private String name;

        public TaskSecond(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Thread.currentThread().setName("Letter counter");
            System.out.println("Task " + name + " is executing in thread " + Thread.currentThread().getName());
            for (char i = 'A'; i <= 'Z'; i++) {
                System.out.println("Letter = " + i);
            }
        }
    }
}
