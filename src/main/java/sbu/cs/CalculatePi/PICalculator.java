package sbu.cs.CalculatePi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PICalculator {
    public static class CalculatePI implements Runnable {
        MathContext mc;
        int k;
        public CalculatePI(MathContext mc, int k) {
            this.mc = mc;
            this.k = k;
        }

        @Override
        public void run() {

            BigDecimal fraction = new BigDecimal("1");
            fraction = fraction.divide(new BigDecimal("16").pow(k), mc);
            BigDecimal a = new BigDecimal("4");
            BigDecimal b = new BigDecimal("2");
            BigDecimal c = new BigDecimal("1");
            BigDecimal d = new BigDecimal("1");
            a = a.divide(BigDecimal.valueOf(8 * k + 1), mc);
            b = b.divide(BigDecimal.valueOf(8 * k + 4), mc);
            c = c.divide(BigDecimal.valueOf(8 * k +5), mc);
            d = d.divide(BigDecimal.valueOf(8 * k + 6) , mc);
            a = a.subtract(b).subtract(c).subtract(d);
            fraction = fraction.multiply(a);

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

    public String calculate(int floatingPoint)
    {
        MathContext mc = new MathContext(floatingPoint);

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
        pi = pi.setScale(floatingPoint, RoundingMode.HALF_DOWN);
        return pi.toString();
    }

    public static void main(String[] args) {
        Instant start = Instant.now();
        PICalculator piCalculator = new PICalculator();
        System.out.println(piCalculator.calculate(1000));
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Duration: " + duration.toMillis() + " ms");
    }
}
