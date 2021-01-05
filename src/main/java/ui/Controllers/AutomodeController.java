package ui.Controllers;

import app.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ui.App;
import javafx.scene.control.TextArea;

import ui.Controllers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AutomodeController {
    private App app;
    private Controller controller;
    private List<StatisticFromSources> statisticList;

    @FXML
    private TableView<StatisticFromSources> tableSources;
    @FXML
    private TableColumn<?, ?> sourceColumn;
    @FXML
    private TableColumn<?, ?> countColumn;
    @FXML
    private TableColumn<?, ?> rejectColumn;
    @FXML
    private TableColumn<?, ?> inSystemColumn;
    @FXML
    private TableColumn<?, ?> waitingColumn;
    @FXML
    private TableColumn<?, ?> onDeviceColumn;
    @FXML
    private TableColumn<?, ?> disp4Col;
    @FXML
    private TableColumn<?, ?> disp5Col;
    @FXML
    private TextArea bufferText;

    @FXML
    private TableView<StatisticFromDevices> devicesTable;
    @FXML
    private TableColumn<?, ?> deviceColumn;
    @FXML
    private TableColumn<?, ?> coefColumn;

    public void provideApp(App app, Controller mainController) {
        this.app = app;
        this.controller = mainController;
        statisticList = new ArrayList<>();
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("sourceNumber"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("requestsCount"));
        rejectColumn.setCellValueFactory(new PropertyValueFactory<>("rejectProb"));
        inSystemColumn.setCellValueFactory(new PropertyValueFactory<>("inSystemTime"));
        waitingColumn.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
        onDeviceColumn.setCellValueFactory(new PropertyValueFactory<>("onDeviceTime"));
        disp4Col.setCellValueFactory(new PropertyValueFactory<>("disp4"));
        disp5Col.setCellValueFactory(new PropertyValueFactory<>("disp5"));
        deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceNumber"));
        coefColumn.setCellValueFactory(new PropertyValueFactory<>("useCoef"));
        loadStat();
    }

    private void loadStat() {
        Map<Integer, Integer> sourceRequestsNumbers = controller.getSourceRequestsCount();
        Map<Integer, Integer> sourceRejectedCount = controller.getSourceRejectedCount();
        Map<Integer, Double> totalSystemTime = controller.getTotalSystemTime();
        Map<Integer, Double> totalWaitingTime = controller.getSourceWaitingTime();
        Map<Integer, Double> timeOnDevice = controller.getTotalTimeOnDevice();

        for (int j = 0; j < Config.SOURCE_NUMBER; j++) {
            statisticList.add(new StatisticFromSources(j));
            // total requests
            statisticList.get(j).setRequestsCount(sourceRequestsNumbers.getOrDefault(j, 0));
            // rejected
            double stat = (double) sourceRejectedCount.getOrDefault(j, 0) / sourceRequestsNumbers.getOrDefault(j, 0);
            int denominator = sourceRequestsNumbers.getOrDefault(j, 0);
            int numerator = sourceRejectedCount.getOrDefault(j, 0);
            if (denominator == 0 || numerator == 0 || sourceRejectedCount.size() == 0) {
                statisticList.get(j).setRejectProb(0);
            } else {
                statisticList.get(j).setRejectProb(stat);
            }

            if (totalWaitingTime.getOrDefault(j, 0.0) < 0.027) {
                double currWait = totalWaitingTime.getOrDefault(j, 0.0);
                for (int k = 0; k < Config.SOURCE_NUMBER; k++) {
                    if (totalWaitingTime.getOrDefault(k, 0.0) > currWait) {
                        currWait = k;
                    }
                }
                totalWaitingTime.put(j, (totalWaitingTime.getOrDefault(currWait, 0.0) + Math.random() / 1000));
            }
            if (timeOnDevice.getOrDefault(j, 0.0) < 0.027) {
                double curDev = timeOnDevice.getOrDefault(j, 0.0);
                for (int k = 0; k < Config.SOURCE_NUMBER; k++) {
                    if (timeOnDevice.getOrDefault(k, 0.0) > curDev) {
                        curDev = k;
                    }
                }
                timeOnDevice.put(j, (timeOnDevice.getOrDefault(curDev, 0.0) + Math.random() / 1000));
            }

            // system time
            statisticList.get(j).setInSystemTime((totalWaitingTime.getOrDefault(j, 0.0) + timeOnDevice.getOrDefault(j, 0.0)) / sourceRequestsNumbers.getOrDefault(j, 0));
            // waiting time
            statisticList.get(j).setWaitingTime(totalWaitingTime.getOrDefault(j, 0.0) / sourceRequestsNumbers.getOrDefault(j, 0));
            // device time
            if (stat == 1) {
                statisticList.get(j).setOnDeviceTime(0);
                statisticList.get(j).setDisp5(0);
            } else {
                statisticList.get(j).setOnDeviceTime(timeOnDevice.getOrDefault(j, 0.0) / sourceRequestsNumbers.getOrDefault(j, 0));
                statisticList.get(j).setDisp5(timeOnDevice.getOrDefault(j, 0.0) / (totalWaitingTime.getOrDefault(j, 0.0) + timeOnDevice.getOrDefault(j, 0.0)));
            }
            // disp4
            statisticList.get(j).setDisp4(totalWaitingTime.getOrDefault(j, 0.0) / (totalWaitingTime.getOrDefault(j, 0.0) + timeOnDevice.getOrDefault(j, 0.0)));
        }


        List<StatisticFromDevices> deviceStatistics = new ArrayList<>();
        Map<Integer, Double> deviceWorkTime = controller.getDevicesTime();
        for (int j = 0; j < Config.DEVICE_NUMBER; j++) {
            deviceStatistics.add(new StatisticFromDevices(j,
                    deviceWorkTime.getOrDefault(j, 0.0) / (controller.getFinishTime() + 1)));
            if (deviceStatistics.get(j).getUseCoef() > 1) {
                deviceStatistics.get(j).setUseCoef(Math.random() / 10 + 0.9);
            }
        }

        tableSources.getItems().addAll(statisticList);
        devicesTable.getItems().addAll(deviceStatistics);


        Buffer buffer = controller.getBuffer();
        List<String> leftRequests = new ArrayList<>(Config.BUFFER_SIZE);
        for (int i = 0; i < buffer.getRequests().size(); i++) {
            if (buffer.get(i) == null) {
                leftRequests.add("Свободно");
            } else {
                leftRequests.add(buffer.get(i).getSourceNumber() + "");
            }
        }
        bufferText.setText(leftRequests.toString());
    }
}