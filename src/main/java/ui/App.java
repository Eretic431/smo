package ui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;


import app.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import ui.Controllers.AutomodeController;
import ui.Controllers.MainController;

import java.io.IOException;

public class App extends Application {

    private Stage primaryStage;
    private Controller mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainController = new Controller(this::infoCollector);
        showMainWindow();
    }

    public void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/MainController.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("SMO");
            primaryStage.show();

            MainController controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAutoModeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/AutomodeController.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Auto mode");
            primaryStage.show();
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
            primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

            AutomodeController controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showStepModeWindow() {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/view/MainController.fxml"));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.setTitle("Step mode");
            primaryStage.show();

            MainController controller = loader.getController();
            controller.provideApp(this, mainController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeMainWindow(){
        primaryStage.close();
    }
    public void showErrorAlert(String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle("Error");
        alert.setHeaderText("something went wrong");
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void infoCollector(Object object) {
        System.out.println(object);
    }
}
