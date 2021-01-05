package app;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Controller {
    private int sourceCount;
    private int deviceCount;
    private int requestsNumber;
    private double currentTime;
    private double finishTime;
    private int devicePointer;

    private Buffer buffer;
    private RequestsManager sourceManager;
    private DevicesManager deviceManager;
    private final Consumer<Object> infoCollector;

    // stats
    private final Map<Integer, Integer> sourceRequestsCount;
    private final Map<Integer, Double> sourceWaitingTime;
    private final Map<Integer, Integer> sourceRejectedCount;
    private final Map<Integer, Double> totalSystemTime;
    private final Map<Integer, Double> totalTimeOnDevice;
    private final Map<Integer, Double> devicesTime;
    private int replaced = 0;

    public Controller(Consumer<Object> infoCollector) {
        this.infoCollector = infoCollector;
        initialize();
        sourceRequestsCount = new HashMap<>();
        sourceRejectedCount = new HashMap<>();
        totalSystemTime = new HashMap<>();
        sourceWaitingTime = new HashMap<>();
        totalTimeOnDevice = new HashMap<>();
        devicesTime = new HashMap<>();
    }

    public void initialize() {
        sourceCount = Config.SOURCE_NUMBER;
        deviceCount = Config.DEVICE_NUMBER;
        int bufferSize = Config.BUFFER_SIZE;
        double alpha = Config.ALPHA;
        double beta = Config.BETA;
        requestsNumber = Config.REQUESTS_NUMBER;
        currentTime = 0;
        sourceManager = new RequestsManager(sourceCount, alpha, beta);
        deviceManager = new DevicesManager(deviceCount, Config.LAMBDA);
        buffer = new Buffer(bufferSize);

    }

    private void checkFreeDevices() {
        List<DoneRequest> doneRequests = deviceManager.getDoneRequestsWithDevices(currentTime);
        for (DoneRequest doneReq : doneRequests) {
            Request doneRequest = doneReq.getDoneRequest();
            if (doneRequest != null) {
                infoCollector.accept("Прибор " + doneReq.getDeviceNumber()
                        + " освободился в " + doneReq.getDoneTime() +
                        ", номер источника заявки - " + doneRequest.getSourceNumber());
                totalSystemTime.put(doneRequest.getSourceNumber(), totalSystemTime.getOrDefault(doneRequest.getSourceNumber(), 0.0)
                        + doneReq.getDoneTime());
            }
            if (!buffer.isEmpty()) {
                Request requestForDevice = buffer.getRequestForExecution();
                if (requestForDevice == null) {
                    continue;
                }
                double timeToPlace = Math.max(requestForDevice.getGenerationTime(), doneReq.getDoneTime());

                int deviceNumber = deviceManager.executeRequest(requestForDevice, timeToPlace);
                totalTimeOnDevice.put(requestForDevice.getSourceNumber(), totalTimeOnDevice.getOrDefault(requestForDevice.getSourceNumber(), 0.0)
                        + deviceManager.getDevice(deviceNumber).getDoneTime() - timeToPlace);
                sourceWaitingTime.put(requestForDevice.getSourceNumber(),
                        sourceWaitingTime.getOrDefault(requestForDevice.getSourceNumber(), 0.0) + (timeToPlace - requestForDevice.getGenerationTime()));
                devicesTime.put(deviceNumber, devicesTime.getOrDefault(deviceNumber, 0.0)
                        + deviceManager.getDevice(deviceNumber).getDoneTime() - timeToPlace);
                infoCollector.accept("Заявка от источника " + requestForDevice.getSourceNumber() +
                        " загружена на прибор " + deviceNumber + " ее номер " + requestForDevice.getNumber());
                deviceNumber++;
                if (deviceNumber == Config.DEVICE_NUMBER) {
                    devicePointer = 0;
                } else {
                    devicePointer = deviceNumber;
                }
            }
        }
    }

    public void start() {
        initialize();
        for (int i = 0; i < requestsNumber - sourceCount + 1; ++i) {
            Pair<Double, List<Request>> nextRequestPair = sourceManager.getNextRequest(currentTime);
            List<Request> nextRequests = nextRequestPair.getSecond();
            currentTime += nextRequestPair.getFirst();

            for (final Request nextRequest : nextRequests) {
                infoCollector.accept("источник " + nextRequest.getSourceNumber() + " создал заявку в " + nextRequest.getGenerationTime() + " ее номер:  " + nextRequest.getNumber());
                sourceRequestsCount.put(nextRequest.getSourceNumber(), sourceRequestsCount.getOrDefault(nextRequest.getSourceNumber(), 0) + 1);
                //infoCollector.accept("источник номер " + nextRequests.get(i).getSourceNumber() + " создал заявку в " + nextRequests.get(i).getGenerationTime());

                Pair<Integer, ArrayList<Integer>> statusPair = buffer.put(nextRequest, currentTime);
                int status = statusPair.getFirst();
                if (status == 0) {
                    infoCollector.accept("Заявка добавлена без удалений");
                } else if (status == 1) {
                    sourceRejectedCount.put(nextRequest.getSourceNumber(), sourceRejectedCount.getOrDefault(nextRequest.getSourceNumber(), 0) + 1);
                    infoCollector.accept("Заявка попала в буфер, сначала удалив заявку от источника " + statusPair.getSecond().get(0) + " и ее номер " + statusPair.getSecond().get(1));
                    replaced++;
                } else {
                    sourceRejectedCount.put(nextRequest.getSourceNumber(), sourceRejectedCount.getOrDefault(nextRequest.getSourceNumber(), 0) + 1);
                    infoCollector.accept("Заявка ушла в отказ");
                    sourceRejectedCount.put(statusPair.getSecond().get(0), sourceRejectedCount.getOrDefault(statusPair.getSecond(), 0) + 1);
                }
            }
            checkFreeDevices();
            infoCollector.accept("Состояние системы:");
            showInfo();
        }

        System.out.println(sourceRejectedCount);
        System.out.println("Всего заявок было выбито из буфера: " + replaced);
        //System.out.println("Всего отказанных заявок не попало в буффер: " + rejected);
        int sumTotal = 0;
        int sumRejected = 0;
        for (int i = 0; i < sourceCount; i++) {
            sumTotal += sourceRequestsCount.getOrDefault(i, 0);
            sumRejected += sourceRejectedCount.getOrDefault(i, 0);
        }
        System.out.println("Всего = " + sumTotal);
        System.out.println("Отклонено = " + sumRejected);
        finishTime = currentTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    private void showInfo() {
        System.out.println("Состояние буфера на данный момент (по источникам заявок): ");
        System.out.println(buffer.getRequests().stream().map(request -> {
            if (request == null) {
                return "null";
            } else {
                return request.getSourceNumber();
            }
        }).collect(Collectors.toList()));

        System.out.println("источники на данный момент сгенерировали: ");
        for (int i = 0; i < sourceCount; i++) {
            System.out.println("источник " + i + " сгенерировал " + sourceRequestsCount.getOrDefault(i, 0));
        }

        System.out.println("Приборы на данный момент: ");
        for (int i = 0; i < deviceCount; i++) {
            if (!deviceManager.getDevice(i).isFree()) {
                System.out.println("Прибор " + i + " занят, освободится в " + deviceManager.getDevice(i).getDoneTime());
            } else {
                System.out.println("Прибор " + i + " свободен");
            }
        }

        infoCollector.accept("Указатель буффера на " + buffer.getIndexPointer() + " элементе");
        infoCollector.accept("Указатель на приборе " + devicePointer);

    }

    public Map<Integer, Integer> getSourceRequestsCount() {
        return sourceRequestsCount;
    }

    public Map<Integer, Double> getSourceWaitingTime() {
        return sourceWaitingTime;
    }

    public Map<Integer, Integer> getSourceRejectedCount() {
        return sourceRejectedCount;
    }

    public Map<Integer, Double> getTotalSystemTime() {
        return totalSystemTime;
    }

    public Map<Integer, Double> getTotalTimeOnDevice() {
        return totalTimeOnDevice;
    }

    public Map<Integer, Double> getDevicesTime() {
        return devicesTime;
    }

    public Buffer getBuffer() {
        return buffer;
    }
}
