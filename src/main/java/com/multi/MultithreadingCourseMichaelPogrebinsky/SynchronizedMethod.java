package com.multi.MultithreadingCourseMichaelPogrebinsky;

public class SynchronizedMethod {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrimentingThread incrimentingThread = new IncrimentingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);
        
        incrimentingThread.start();
        decrementingThread.start();
        
        incrimentingThread.join();
        decrementingThread.join();
        
        System.out.println("We currently have = " + inventoryCounter.getItems() + " items");
    }
  
    private static class InventoryCounter{
        private int items = 0;
        
        public synchronized void increment() {
            items ++;
        }
        
        public synchronized void decrement() {
            items --;
        }
        
        public synchronized int getItems() {
            return items;
        }
    }
    
    public static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;
        
        public DecrementingThread(InventoryCounter inventoryCounter) {
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
        private InventoryCounter inventoryCounter;
        
        public IncrimentingThread(InventoryCounter inventoryCounter) {
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


