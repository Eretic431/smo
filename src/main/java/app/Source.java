package app;

import java.util.Random;

public class Source {
    private final Random generator = new Random();
    private final int number;
    private final double alpha;
    private final double beta;

    public Source(int number, double alpha, double beta) {
        this.number = number;
        this.alpha = alpha;
        this.beta = beta;
    }

    public Pair<Double, Request> generate(final double currentTime, int counter) {
        final double nextRequestTime = getNextRequestTime();
        return new Pair<>(nextRequestTime, new Request(number, counter, currentTime));
    }

    private double getNextRequestTime() {
        return generator.nextDouble() * (beta - alpha) + alpha;
    }
}