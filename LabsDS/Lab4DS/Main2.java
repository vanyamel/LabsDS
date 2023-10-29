package org.example;

import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;


enum PlantState {
    HEALTHY, WILTING, DEAD;
}

class Garden {
    private PlantState[][] garden;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public Garden(int size) {
        this.garden = new PlantState[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                garden[i][j] = PlantState.HEALTHY;
            }
        }
    }

    public void affectNature() {
        lock.writeLock().lock();
        try {

            int i = (int) (Math.random() * garden.length);
            int j = (int) (Math.random() * garden[i].length);
            if (garden[i][j] == PlantState.HEALTHY) {
                garden[i][j] = PlantState.WILTING;
            } else if (garden[i][j] == PlantState.WILTING) {
                garden[i][j] = PlantState.DEAD;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void tendGarden() {
        lock.writeLock().lock();
        try {
            for (int i = 0; i < garden.length; i++) {
                for (int j = 0; j < garden[i].length; j++) {
                    if (garden[i][j] == PlantState.WILTING) {
                        garden[i][j] = PlantState.HEALTHY;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void printGardenToFile() throws IOException {
        lock.readLock().lock();
        try {
            try (PrintWriter out = new PrintWriter(new FileWriter("garden.txt", true))) {
                for (int i = 0; i < garden.length; i++) {
                    for (int j = 0; j < garden[i].length; j++) {
                        out.print(garden[i][j] + " ");
                    }
                    out.println();
                }
                out.println("------------");
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void displayGarden() {
        lock.readLock().lock();
        try {
            for (int i = 0; i < garden.length; i++) {
                for (int j = 0; j < garden[i].length; j++) {
                    System.out.print(garden[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("------------");
        } finally {
            lock.readLock().unlock();
        }
    }
}

public class Main2 {
    public static void main(String[] args) {
        Garden garden = new Garden(5);

        Thread gardener = new Thread(() -> {
            while (true) {
                garden.tendGarden();
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
            }
        });

        Thread nature = new Thread(() -> {
            while (true) {
                garden.affectNature();
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            }
        });

        Thread monitor1 = new Thread(() -> {
            while (true) {
                try {
                    garden.printGardenToFile();
                    Thread.sleep(10000);
                } catch (IOException | InterruptedException ignored) {}
            }
        });

        Thread monitor2 = new Thread(() -> {
            while (true) {
                garden.displayGarden();
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        });

        gardener.start();
        nature.start();
        monitor1.start();
        monitor2.start();
    }
}

