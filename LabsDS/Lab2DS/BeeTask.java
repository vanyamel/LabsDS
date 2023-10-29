package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class BeeTask {

    public static void main(String[] args) {
        Forest forest = new Forest(10);
        for (int i = 0; i < 1; i++) {
            new Bee(forest, "рій бджіл " + (i + 1)).start();
        }
    }

    static class Forest {
        private final Queue<Integer> tasks;
        private boolean isWinnieFound = false;

        public Forest(int sections) {
            tasks = new LinkedList<>();
            for (int i = 0; i < sections; i++) {
                tasks.add(i);
            }
        }

        public synchronized Integer getNextSection() {
            if (isWinnieFound || tasks.isEmpty()) {
                return null;
            }
            return tasks.poll();
        }

        public synchronized void foundWinnie() {
            isWinnieFound = true;
        }

        public synchronized boolean isWinnieAlreadyFound() {
            return isWinnieFound;
        }
    }

    static class Bee extends Thread {
        private final Forest forest;

        public Bee(Forest forest, String name) {
            super(name);
            this.forest = forest;
        }

        @Override
        public void run() {
            Random random = new Random();

            while (true) {
                Integer section = forest.getNextSection();
                if (section == null) {
                    break;
                }

                System.out.println(getName() + " перевіряє ділянку " + section);


                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!forest.isWinnieAlreadyFound() && random.nextInt(10) == 0) {
                    forest.foundWinnie();
                    System.out.println(getName() + " знайшов Вінні на ділянці " + section + "!");
                    break;
                }
            }
        }
    }
}
