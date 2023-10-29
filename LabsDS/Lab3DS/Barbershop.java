package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;

public class Barbershop {
    public static final int MAX_CUSTOMERS = 5;
    private Semaphore customerSemaphore = new Semaphore(0);
    private Semaphore barberSemaphore = new Semaphore(0);
    private Lock barberLock = new ReentrantLock();
    private Lock queueLock = new ReentrantLock();
    private Queue<Customer> waitingQueue = new LinkedList<>();

    public void getHaircut(Customer customer) throws InterruptedException {
        barberLock.lock();
        System.out.println("Customer " + customer.getId() + " is getting a haircut.");
        Thread.sleep(2000);
        barberLock.unlock();
        barberSemaphore.release();
    }

    public Customer waitForCustomer() throws InterruptedException {
        System.out.println("Barber is waiting for a customer.");
        customerSemaphore.acquire();
        queueLock.lock();
        Customer nextCustomer = waitingQueue.poll();
        queueLock.unlock();
        return nextCustomer;
    }

    public void doneHaircut(Customer customer) {
        System.out.println("Customer " + customer.getId() + " is done with haircut.");
    }

    public boolean enterShop(Customer customer) throws InterruptedException {
        queueLock.lock();
        if (waitingQueue.size() < MAX_CUSTOMERS) {
            waitingQueue.offer(customer);
            System.out.println("Customer " + customer.getId() + " entered the waiting room.");
            queueLock.unlock();
            customerSemaphore.release();
            barberSemaphore.acquire();
            return true;
        } else {
            System.out.println("Customer " + customer.getId() + " left because the barbershop is full.");
            queueLock.unlock();
            return false;
        }
    }

    public void exitShop(Customer customer) {
        System.out.println("Customer " + customer.getId() + " is leaving the barbershop.");
    }

    public static void main(String[] args) {
        Barbershop barbershop = new Barbershop();
        Barber barber = new Barber(barbershop);
        Thread barberThread = new Thread(barber);
        barberThread.start();

        AtomicInteger customerId = new AtomicInteger(1);
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer(customerId.getAndIncrement(), barbershop);
            Thread customerThread = new Thread(customer);
            customerThread.start();
            try {
                Thread.sleep(1000); // Simulating the random arrival time of customers
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Barber implements Runnable {
    private Barbershop barbershop;

    public Barber(Barbershop barbershop) {
        this.barbershop = barbershop;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Customer customer = barbershop.waitForCustomer();
                barbershop.getHaircut(customer);
                barbershop.doneHaircut(customer);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Customer implements Runnable {
    private int id;
    private Barbershop barbershop;

    public Customer(int id, Barbershop barbershop) {
        this.id = id;
        this.barbershop = barbershop;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            if (barbershop.enterShop(this)) {
                barbershop.exitShop(this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
