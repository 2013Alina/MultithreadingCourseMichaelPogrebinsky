package com.multi.MultithreadingCourseMichaelPogrebinsky;

import java.math.BigInteger;

public class ComplexCalculation {
    public BigInteger calculateResult(BigInteger base1, BigInteger power1, BigInteger base2, BigInteger power2) {
        BigInteger result;
        PowerCalculatingThread thread1 = new PowerCalculatingThread(base1, power1);
        PowerCalculatingThread thread2 = new PowerCalculatingThread(base2, power2);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = thread1.getResult().add(thread2.getResult());
        return result;
    }

    private static class PowerCalculatingThread extends Thread {
        private BigInteger result = BigInteger.ONE;
        private BigInteger base;
        private BigInteger power;
    
        public PowerCalculatingThread(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }
    
        @Override
        public void run() {
           result = base.pow(power.intValue());
        }
    
        public BigInteger getResult() { return result; }
    }
    public static void main(String[] args) {
        ComplexCalculation complexCalculation = new ComplexCalculation();
        BigInteger base1 = new BigInteger("10");
        BigInteger power1 = new BigInteger("2");
        BigInteger base2 = new BigInteger("10");
        BigInteger power2 = new BigInteger("3");
        
        BigInteger result = complexCalculation.calculateResult(base1, power1, base2, power2);
        System.out.println("Result: " + result);
    }
}
