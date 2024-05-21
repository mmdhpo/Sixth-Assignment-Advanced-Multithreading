package sbu.cs.CalculatePi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChudnovskyAlgorithm {
    public static class CalculatePI implements Runnable {
        MathContext mc;
        int k;
        public CalculatePI(MathContext mc, int k) {
            this.mc = mc;
            this.k = k;
        }

        @Override
        public void run() {

            BigDecimal sign = new BigDecimal(1);
            if (k%2 == 1) {
                sign = new BigDecimal(-1);
            }

            BigDecimal fraction = factorial(6 * k);
            fraction = fraction.multiply(new BigDecimal(13591409 + 545140134 * k));
            fraction = fraction.divide(factorial(3 * k), mc);
            fraction = fraction.divide(factorial(k).pow(3), mc);
            fraction = fraction.divide(new BigDecimal(640320).pow(3 * k + 1), mc);
            fraction = fraction.divide(new BigDecimal(Math.sqrt(640320)), mc);
            fraction = fraction.multiply(new BigDecimal("12"), mc);
            fraction = fraction.multiply(sign, mc);

            addToPI(fraction);
        }


        public BigDecimal factorial(int n){
            BigDecimal temp = new BigDecimal(1);
            for (int i = 1; i <= n; i++) {
                temp = temp.multiply(new BigDecimal(i), mc);
            }

            return temp;
        }
    }

    public static BigDecimal pi;

    public static synchronized void addToPI(BigDecimal value){
        pi = pi.add(value);
    }

    /**
     * Calculate pi and represent it as a BigDecimal object with the given floating point number (digits after . )
     * There are several algorithms designed for calculating pi, it's up to you to decide which one to implement.
     Experiment with different algorithms to find accurate results.

     * You must design a multithreaded program to calculate pi. Creating a thread pool is recommended.
     * Create as many classes and threads as you need.
     * Your code must pass all of the test cases provided in the test folder.

     * @param floatingPoint the exact number of digits after the floating point
     * @return pi in string format (the string representation of the BigDecimal object)
     */

    public static String calculate(int floatingPoint)
    {
        MathContext mc = new MathContext(floatingPoint + 1);

        ExecutorService threadPool = Executors.newFixedThreadPool(100);


        pi = new BigDecimal("0");

        for (int i = 0; i < 10000; i++) {
            CalculatePI task = new CalculatePI(mc ,i);
            threadPool.execute(task);
        }

        threadPool.shutdown();

        try {
            threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pi = new BigDecimal("1").divide(pi, mc);
        pi = pi.setScale(floatingPoint, RoundingMode.DOWN);
        return pi.toString();
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        System.out.println(calculate(1000));
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Duration: " + duration.toMillis() + " ms");
    }
}
