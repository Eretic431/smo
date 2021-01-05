package app;

import java.util.Random;

public class Device {
    private final int number;
    private final Random generator = new Random();
    private Request requestOnDevice;
    private double timeProcessing;
    private double timeBeg;
    private final double lambda;

    public Device(final int number, double lambda) {
        this.number = number;
        this.timeBeg = -1;
        this.timeProcessing = -1;
        this.requestOnDevice = null;
        this.lambda = lambda;
    }

    public int getNumber() {
        return number;
    }

    public boolean isFree() {
        return requestOnDevice == null;
    }

    public void execute(final Request request, final double currentTime) {
        final double timeToDo = getTimeOnDevice();
        this.requestOnDevice = request;
        this.timeBeg = currentTime;
        this.timeProcessing = currentTime + timeToDo;
    }

    public void clearAfterDoneProcessing() {
        this.requestOnDevice = null;
        this.timeBeg = -1;
        this.timeProcessing = -1;
    }

    public Request getDoneRequest() {
        return requestOnDevice;
    }

    public double getTimeBeg() {
        return timeBeg;
    }

    public double getDoneTime() {
        return timeProcessing;
    }

    private double getTimeOnDevice() {
        return Math.exp(generator.nextDouble() * lambda);
    }
}