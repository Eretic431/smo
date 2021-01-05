package app;

public class Request {
    private final int sourceNumber;
    private final int number;
    private final double generationTime;
    private double timeInBuffer;

    public Request(final int sourceNumber, final int number, final double initialTime) {
        this.sourceNumber = sourceNumber;
        this.number = number;
        this.generationTime = initialTime;
        this.timeInBuffer = -1;
    }

    public double getGenerationTime() {
        return generationTime;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getNumber() {
        return number;
    }

    public double getTimeInBuffer() {
        return timeInBuffer;
    }

    public void setTimeInBuffer(double timeInBuffer) {
        this.timeInBuffer = timeInBuffer;
    }
}