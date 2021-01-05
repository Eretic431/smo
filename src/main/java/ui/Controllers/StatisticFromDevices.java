package ui.Controllers;

public class StatisticFromDevices {
    private final int deviceNumber;
    private  double useCoef;

    public StatisticFromDevices(int deviceNumber, double useCoef) {
        this.deviceNumber = deviceNumber;
        this.useCoef = useCoef;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public double getUseCoef() {
        return useCoef;
    }

    public void setUseCoef(double useCoef) {
        this.useCoef = useCoef;
    }
}
