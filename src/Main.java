import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static ArrayBlockingQueue<String> findA = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> findB = new ArrayBlockingQueue<>(100);
    public static ArrayBlockingQueue<String> findC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread generatingStrings = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String texts = generateText("abc", 100_000);
                try {
                    for (int j = 0; j < "abc".length(); j++) {
                        findA.put(texts);
                        findB.put(texts);
                        findC.put(texts);
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        generatingStrings.start();


        List<Thread> threads = new ArrayList<>();
        Thread a = new Thread(() -> {
            char ch = 'a';
            int max = countingChar(findA, ch);
            System.out.println("Максимальное количество букв " + ch + " : " + max);
        });
        threads.add(a);
        a.start();


        Thread b = new Thread(() -> {
            char ch = 'b';
            int max = countingChar(findB, ch);
            System.out.println("Максимальное количество букв " + ch + " : " + max);
        });
        threads.add(b);
        b.start();


        Thread c = new Thread(() -> {
            char ch = 'c';
            int max = countingChar(findC, ch);

            System.out.println("Максимальное количество букв " + ch + " : " + max);
        });
        threads.add(c);
        c.start();

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static int countingChar(ArrayBlockingQueue<String> findA, char f) {
        String str;
        int max = 0;
        int counter = 0;
        try {
            for (int i = 0; i < 10_000; i++) {
                str = findA.take();
                for (char c : str.toCharArray()) {
                    if (f == c) {
                        counter++;
                    }
                }
                if (counter > max) {
                    max = counter;
                    counter = 0;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Thread has been interrupted");
        }
        return max;
    }

    private static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}