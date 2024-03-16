package com.multi.MultithreadingCourseMichaelPogrebinsky;

public class DebugThread {
    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i <= 50; i++) {
                    System.out.println("Count = " + i);
                }
                throw new RuntimeException("EXEPTION!!!");
            }
        });
        thread.setName("BOBO");
        
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
               System.out.println("Error happened in thread = " + t.getName());
               System.out.println("Error is = " + e.getMessage()); 
            }
        });
        
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

}
