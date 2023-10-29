package org.example;



public class RecruitsSimulation {
    public static final int NUM_RECRUITS = 100;
    public static final int RECRUITS_PER_THREAD = 50;

    public static void main(String[] args) {
        Recruit[] recruits = new Recruit[NUM_RECRUITS];
        for (int i = 0; i < NUM_RECRUITS; i++) {
            recruits[i] = new Recruit(i);
        }

        SimpleSynchronizer synchronizer = new SimpleSynchronizer(NUM_RECRUITS / RECRUITS_PER_THREAD);

        for (int i = 0; i < NUM_RECRUITS; i += RECRUITS_PER_THREAD) {
            new Thread(new RecruitGroup(recruits, i, i + RECRUITS_PER_THREAD, synchronizer)).start();
        }
    }
}
class SimpleSynchronizer {
    private final int totalThreads;
    private int currentThreads = 0;

    public SimpleSynchronizer(int totalThreads) {
        this.totalThreads = totalThreads;
    }

    public synchronized void await() throws InterruptedException {
        currentThreads++;
        if (currentThreads == totalThreads) {
            notifyAll();
        } else {
            while (currentThreads < totalThreads) {
                wait();
            }
        }
    }
}
class Recruit {
    private boolean turnedRight = Math.random() < 0.5;
    private final int id;

    public Recruit(int id) {
        this.id = id;
    }

    public synchronized boolean isFacing(Recruit other) {
        return (this.turnedRight && !other.turnedRight) || (!this.turnedRight && other.turnedRight);
    }

    public synchronized void turn() {
        turnedRight = !turnedRight;
        System.out.println("Recruit " + id + " turned " + (turnedRight ? "right" : "left"));
    }
}

class RecruitGroup implements Runnable {
    private final Recruit[] recruits;
    private final int start;
    private final int end;
    private final SimpleSynchronizer synchronizer;

    public RecruitGroup(Recruit[] recruits, int start, int end, SimpleSynchronizer synchronizer) {
        this.recruits = recruits;
        this.start = start;
        this.end = end;
        this.synchronizer = synchronizer;
    }

    @Override
    public void run() {
        while (true) {
            for (int i = start; i < end - 1; i++) {
                if (recruits[i].isFacing(recruits[i + 1])) {
                    recruits[i].turn();
                    recruits[i + 1].turn();
                }
            }
            try {
                synchronizer.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


