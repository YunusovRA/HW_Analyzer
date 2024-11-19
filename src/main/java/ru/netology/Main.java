package ru.netology;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    private static final int QUEUE_CAPACITY = 100;
    private static final int TEXT_COUNT = 10_000;
    private static final int TEXT_LENGTH = 100_000;

    private static BlockingQueue<String> queueA = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private static BlockingQueue<String> queueB = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private static BlockingQueue<String> queueC = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) {

        Thread generatorThread = new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText(TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        generatorThread.start();

        Thread analyzerA = createAnalyzerThread(queueA, 'a');
        Thread analyzerB = createAnalyzerThread(queueB, 'b');
        Thread analyzerC = createAnalyzerThread(queueC, 'c');

        analyzerA.start();
        analyzerB.start();
        analyzerC.start();
    }

    private static Thread createAnalyzerThread(BlockingQueue<String> queue, char targetChar) {
        return new Thread(() -> {
            String maxText = null;
            int maxCount = 0;

            try {
                int processedCount = 0;
                while (processedCount < TEXT_COUNT) {
                    String text = queue.take();
                    int count = countChar(text, targetChar);
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                    processedCount++;
                }
                System.out.println("Text with max '" + targetChar + "': " + maxCount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static int countChar(String text, char targetChar) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == targetChar) {
                count++;
            }
        }
        return count;
    }

    private static String generateText(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * 3);
            char c = (random == 0) ? 'a' : (random == 1) ? 'b' : 'c';
            sb.append(c);
        }
        return sb.toString();
    }
}