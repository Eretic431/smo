package ui.Controllers;

public class StatisticFromSources {
    private int sourceNumber;
    private int requestsCount;
    private double rejectProb;
    private double inSystemTime;
    private double waitingTime;
    private double onDeviceTime;
    private double disp4;
    private double disp5;

    public StatisticFromSources(int number) {
        this.sourceNumber = number;
        this.requestsCount = 0;
        this.rejectProb = 0;
        this.inSystemTime = 0;
        this.waitingTime = 0;
        this.onDeviceTime = 0;
    }

    public int getSourceNumber() {
        return sourceNumber;
    }

    public int getRequestsCount() {
        return requestsCount;
    }

    public double getRejectProb() {
        return rejectProb;
    }

    public double getInSystemTime() {
        return inSystemTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public double getOnDeviceTime() {
        return onDeviceTime;
    }

    public double getDisp4() {
        return disp4;
    }

    public double getDisp5() {
        return disp5;
    }

    public void setSourceNumber(int sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public void setRequestsCount(int requestsCount) {
        this.requestsCount = requestsCount;
    }

    public void setRejectProb(double rejectProb) {
        this.rejectProb = rejectProb;
    }

    public void setInSystemTime(double inSystemTime) {
        this.inSystemTime = inSystemTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setOnDeviceTime(double onDeviceTime) {
        this.onDeviceTime = onDeviceTime;
    }

    public void setDisp4(double disp4) {
        this.disp4 = disp4;
    }

    public void setDisp5(double disp5) {
        this.disp5 = disp5;
    }
}