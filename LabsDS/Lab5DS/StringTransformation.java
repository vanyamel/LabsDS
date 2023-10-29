package org.example;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class StringTransformation {

    private static final int THREAD_COUNT = 4;
    private static CountDownLatch latch = new CountDownLatch(3);

    public static void main(String[] args) {
        String[] strings = {
                "ABCD",
                "DCBA",
                "AADD",
                "BBCC"
        };

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(new StringWorker(strings, i)).start();
        }
    }

    static class StringWorker implements Runnable {
        private final String[] sharedStrings;
        private final int index;

        StringWorker(String[] sharedStrings, int index) {
            this.sharedStrings = sharedStrings;
            this.index = index;
        }

        @Override
        public void run() {
            while (latch.getCount() > 0) {
                char[] chars = sharedStrings[index].toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A') {
                        chars[i] = 'C';
                    } else if (chars[i] == 'C') {
                        chars[i] = 'A';
                    } else if (chars[i] == 'B') {
                        chars[i] = 'D';
                    } else if (chars[i] == 'D') {
                        chars[i] = 'B';
                    }
                }
                sharedStrings[index] = new String(chars);

                // Log the current state of the string
                System.out.println("Thread " + index + " transformed string to: " + sharedStrings[index]);

                // Check condition
                int count = 0;
                for (String s : sharedStrings) {
                    long aAndBCount = s.chars().filter(ch -> ch == 'A' || ch == 'B').count();
                    if (aAndBCount == s.length() / 2) {
                        count++;
                    }
                }
                if (count >= 3) {
                    System.out.println("Thread " + index + " detected 3 strings with equal count of A and B. Stopping...");
                    latch.countDown();
                }

                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
