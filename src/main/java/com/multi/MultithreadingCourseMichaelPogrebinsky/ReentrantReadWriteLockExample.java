package com.multi.MultithreadingCourseMichaelPogrebinsky;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ReentrantReadWriteLockExample {
    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDataBase = new InventoryDatabase();

        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            inventoryDataBase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread write = new Thread(() -> {
            while (true) {
                inventoryDataBase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDataBase.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {

                }
            }
        });

        write.setDaemon(true);
        write.start();

        int numberOfReaderThreads = 7;
        List<Thread> readers = new ArrayList<>();

        for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 10000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    inventoryDataBase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();

        for (Thread reader : readers) {
            reader.start();
        }

        for (Thread reader : readers) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.println(String.format("Reading took %d ms", endReadingTime - startReadingTime));

    }

    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>(); // пары:цена и количество продуктов

        private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(); // Reading took 167 ms!!!!!!!В 1.5 раза быстрее
        private ReadLock readLock = rwLock.readLock();
        private WriteLock writeLock = rwLock.writeLock();

        private ReentrantLock lock = new ReentrantLock(); // Reading took 246 ms

        // Метод возвращает количество предметов, цена которых находится в этом диапазоне.
        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            // lock.lock();
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                // возвращает наименьший ключ, который больше или равен lowerBound
                Integer toKey = priceToCountMap.floorKey(upperBound);
                // возвращает наибольший ключ, который меньше или равен upperBound
                if (fromKey == null || toKey == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                // отображение, представляющее подмножество ключей от fromKey (включительно) до toKey (включительно)
                int sum = 0;
                for (int numberOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numberOfItemsForPrice;
                }
                return sum;
            } finally {
                // lock.unlock();
                readLock.unlock();
            }
        }

        // Этот метод принимает один параметр price, который представляет цену нового предмета, и обновляет количество
        // предметов для этой цены в priceToCountMap
        public void addItem(int price) {
            // lock.lock();
            writeLock.lock();
            try {

                Integer numberOfItemsForPrice = priceToCountMap.get(price); // получить текущее количество предметов для
                                                                            // указанной цены из priceToCountMap
                if (numberOfItemsForPrice == null) {
                    // это означает, что предмет с такой ценой добавляется впервые
                    priceToCountMap.put(price, 1); // Если предмет с такой ценой добавляется впервые, он добавляется в
                                                   // карту
                                                   // с количеством 1
                } else {
                    priceToCountMap.put(price, numberOfItemsForPrice + 1); // Если предмет с такой ценой уже существует,
                                                                           // количество предметов для этой цены
                                                                           // увеличивается на 1
                }
            } finally {
                // lock.unlock();
                writeLock.unlock();
            }
        }
        // Метод addItem гарантирует, что для каждой цены в priceToCountMap ведется корректный учет количества
        // предметов. Если предмет с указанной ценой добавляется впервые, создается новая запись с количеством 1. Если
        // предмет с такой ценой уже существует, количество увеличивается на 1.

        public void removeItem(int price) {
            // lock.lock();
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceToCountMap.get(price);
                if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                    // Если цена отсутствует в карте или количество предметов равно 1
                    priceToCountMap.remove(price);
                } else {
                    // Если количество предметов для этой цены больше 1, количество уменьшается на 1
                    priceToCountMap.put(price, numberOfItemsForPrice - 1);
                }
            } finally {
                // lock.unlock();
                writeLock.unlock();
            }
        }

    }

}
