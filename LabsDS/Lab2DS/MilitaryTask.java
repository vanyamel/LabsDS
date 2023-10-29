package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MilitaryTask {

    private static final int MAX_ITEMS = 10;

    private static final Queue<String> storageToTruck = new LinkedList<>();
    private static final Queue<String> truckToAccounting = new LinkedList<>();

    public static void main(String[] args) {
        new Ivanov().start();
        new Petrov().start();
        new Nechiporchuk().start();
    }

    static class Ivanov extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    while (storageToTruck.size() == MAX_ITEMS) {
                        Thread.yield();
                    }
                    String item = "item";
                    storageToTruck.add(item);
                    System.out.println("Іванов виносить " + item);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Petrov extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    while (storageToTruck.isEmpty() || truckToAccounting.size() == MAX_ITEMS) {
                        Thread.yield();
                    }
                    String item = storageToTruck.poll();
                    truckToAccounting.add(item);
                    System.out.println("Петров вантажить " + item);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Nechiporchuk extends Thread {
        Random random = new Random();

        @Override
        public void run() {
            try {
                while (true) {
                    while (truckToAccounting.isEmpty()) {
                        Thread.yield();
                    }
                    String item = truckToAccounting.poll();
                    int itemValue = random.nextInt(1000);
                    System.out.println("Нечипорчук підраховує вартість " + item + ": " + itemValue + " грн.");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
