package org.example;

class BinarySemaphore {
    private boolean isLocked = false;

    public synchronized void lock() throws InterruptedException {
        while (isLocked) {
            wait();
        }
        isLocked = true;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}

class HoneyPot {
    private final int CAPACITY;
    private int honeyAmount = 0;
    private final BinarySemaphore semaphore = new BinarySemaphore();

    public HoneyPot(int capacity) {
        this.CAPACITY = capacity;
    }

    public void addHoney() throws InterruptedException {
        semaphore.lock();
        if (honeyAmount < CAPACITY) {
            honeyAmount++;
            System.out.println("Bee added honey. Total honey: " + honeyAmount);
            if (honeyAmount == CAPACITY) {
                System.out.println("Honey pot is full. Bear is notified.");
            }
        }
        semaphore.unlock();
    }

    public void consumeHoney() throws InterruptedException {
        semaphore.lock();
        if (honeyAmount == CAPACITY) {
            System.out.println("Bear is consuming the honey...");
            honeyAmount = 0;
            System.out.println("Bear has consumed all the honey. Honey pot is now empty.");
        }
        semaphore.unlock();
    }
}

class Bee implements Runnable {
    private final HoneyPot honeyPot;

    public Bee(HoneyPot honeyPot) {
        this.honeyPot = honeyPot;
    }

    @Override
    public void run() {
        while (true) {
            try {
                honeyPot.addHoney();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Bear implements Runnable {
    private final HoneyPot honeyPot;

    public Bear(HoneyPot honeyPot) {
        this.honeyPot = honeyPot;
    }

    @Override
    public void run() {
        while (true) {
            try {
                honeyPot.consumeHoney();
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class WinnieThePooh {
    public static void main(String[] args) {
        HoneyPot honeyPot = new HoneyPot(5);
        Bear bear = new Bear(honeyPot);
        Thread bearThread = new Thread(bear);
        bearThread.start();

        for (int i = 0; i < 10; i++) {
            Bee bee = new Bee(honeyPot);
            Thread beeThread = new Thread(bee);
            beeThread.start();
        }
    }
}


