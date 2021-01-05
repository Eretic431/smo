package app;

public class DoneRequest {
    private final int deviceNumber;
    private final Request doneRequest;
    private final double doneTime;
    private final double timeOfWork;

    public DoneRequest(final int deviceNumber,
                       final Request doneRequest,
                       final double doneTime,
                       final double timeOfWork) {
        this.deviceNumber = deviceNumber;
        this.doneRequest = doneRequest;
        this.doneTime = doneTime;
        this.timeOfWork = timeOfWork;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public Request getDoneRequest() {
        return doneRequest;
    }

    public double getDoneTime() {
        return doneTime;
    }

    public double getTimeOfWork() {
        return timeOfWork;
    }
}