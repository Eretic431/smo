package ui.Controllers;

import app.Controller;
import app.Config;
import app.Main;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import ui.App;


public class MainController {
    private App app;
    private Controller controller;
    private boolean mode;

    @FXML
    private TextField sourceNumber;
    @FXML
    private TextField deviceNumber;
    @FXML
    private TextField requestsNum;
    @FXML
    private TextField bufferSizeText;
    @FXML
    private TextField alpha;
    @FXML
    private TextField beta;
    @FXML
    private TextField lambda;

    @FXML
    private RadioButton automode;
    @FXML
    private RadioButton stepmode;

    public void provideApp(App app, Controller controller) {
        this.app = app;
        this.controller = controller;
    }

    @FXML
    public void startModulating() {
        accessField();
    }

    private void accessField() {
        ToggleGroup modeGroup = new ToggleGroup();
        automode.setToggleGroup(modeGroup);
        stepmode.setToggleGroup(modeGroup);

        if (sourceNumber.getText().isEmpty() || deviceNumber.getText().isEmpty()
                || requestsNum.getText().isEmpty() || bufferSizeText.getText().isEmpty()
                || alpha.getText().isEmpty() || beta.getText().isEmpty() || !(automode.isSelected() ^ stepmode.isSelected())) {
            app.showErrorAlert("Fill all fields");
            return;
        }

        try {
            Config.SOURCE_NUMBER = Integer.parseInt(sourceNumber.getText());
            Config.DEVICE_NUMBER = Integer.parseInt(deviceNumber.getText());
            Config.REQUESTS_NUMBER = Integer.parseInt(requestsNum.getText());
            Config.BUFFER_SIZE = Integer.parseInt(bufferSizeText.getText());
            Config.ALPHA = Double.parseDouble(alpha.getText());
            Config.BETA = Double.parseDouble(beta.getText());
            Config.LAMBDA = Double.parseDouble(lambda.getText());

        } catch (Exception e) {
            app.showErrorAlert("Some fields incorrect");
        }

        if (stepmode.isSelected()) {
            app.closeMainWindow();
            String[] args = new String[10];
            Main.main(args);
        } else {
            controller.start();
            app.showAutoModeWindow();
        }
    }

    @FXML
    public void checkAuto() {
        stepmode.setSelected(false);
    }

    @FXML
    public void checkStep() {
        automode.setSelected(false);
    }

    public Controller getController() {
        return controller;
    }
}