package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevicesManager {
    private final Device[] devices;
    private int pointer;

    public DevicesManager(final int amountOfDevices, double lambda) {
        this.devices = new Device[amountOfDevices];
        this.pointer = 0;
        initDevices(lambda);
    }

    private void initDevices(double lambda) {
        for (int i = 0; i < devices.length; ++i) {
            devices[i] = new Device(i, lambda);
        }
    }

    public boolean isAnyDeviceFree() {
        for (final Device device : devices) {
            if (device.isFree()) {
                return true;
            }
        }
        return false;
    }

    public int executeRequest(final Request request, final double currentTime) {
        final int freeDeviceIndex = getFreeDeviceIndex();
        devices[freeDeviceIndex].execute(request, currentTime);
        return freeDeviceIndex;
    }

    private int getFreeDeviceIndex() {
        int staticPointer = pointer;
        for (int i = pointer; i < devices.length; i++) {
            if (devices[i].isFree()) {
                pointer = i;
                return i;
            }
        }
        for (int i = 0; i < staticPointer; i++) {
            if (devices[i].isFree()) {
                pointer = i;
                return i;
            }
        }

        throw new IllegalStateException("There is no free devices on invocation");
    }

    private void incrementPointer() {
        pointer++;
        if (pointer >= devices.length) {
            pointer = 0;
        }
    }

    public List<DoneRequest> getDoneRequestsWithDevices(final double currentTime) {
        final List<DoneRequest> doneList = new ArrayList<>();
        Arrays.stream(devices)
                .filter(device -> device.getDoneTime() < currentTime)
                .forEach(device -> {
                    final double startTime = device.getTimeBeg();
                    final double doneTime = device.getDoneTime();
                    final double timeOnDevice = doneTime - startTime;
                    doneList.add(new DoneRequest(device.getNumber(), device.getDoneRequest(), doneTime, timeOnDevice));
                    device.clearAfterDoneProcessing();
                });
        return doneList;
    }

    public Device getDevice(int index) {
        return devices[index];
    }

    public int getPointer() {
        return pointer;
    }
}
