package com.multi.MultithreadingCourseMichaelPogrebinsky;


public class LockForObject {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter1 inventoryCounter1 = new InventoryCounter1();
        IncrimentingThread incrimentingThread = new IncrimentingThread(inventoryCounter1);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter1);
        
        incrimentingThread.start();
        decrementingThread.start();
        
        incrimentingThread.join();
        decrementingThread.join();
        
        System.out.println("We currently have = " + inventoryCounter1.getItems() + " items");
    }
  
    private static class InventoryCounter1{
        private int items = 0;
        
        Object lock = new Object();
        
        public void increment() {
            synchronized (this.lock) {
                items ++;
            }
        }
        
        public void decrement() {
            synchronized (this.lock) {
                items --;
            }
        }
        
        public int getItems() {
            synchronized (this.lock) {
                return items;
            }
        }
    }
    
    public static class DecrementingThread extends Thread {
        private InventoryCounter1 inventoryCounter;
        
        public DecrementingThread(InventoryCounter1 inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }
        
        @Override
        public void run() {
            for(int i = 0; i < 1000; i ++) {
                inventoryCounter.decrement();
            }
        }
    }
    
    public static class IncrimentingThread extends Thread {
        private InventoryCounter1 inventoryCounter;
        
        public IncrimentingThread(InventoryCounter1 inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }
        
        @Override
        public void run() {
            for(int i = 0; i < 1000; i ++) {
                inventoryCounter.increment();
            }
        }
    }
}
