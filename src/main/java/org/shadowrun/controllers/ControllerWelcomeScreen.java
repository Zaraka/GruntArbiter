package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.shadowrun.common.nodes.cells.CampaignCell;
import org.shadowrun.logic.AppLogic;

import java.nio.file.Path;

public class ControllerWelcomeScreen {

    private ControllerMain controllerMain;
    private AppLogic appLogic;
    private Stage stage;

    @FXML
    private ListView<Path> listView_latestCampaigns;

    @FXML
    private void createNewCampaignOnAction() {
        controllerMain.newCampaignOnAction();
    }

    @FXML
    private void openCampaignOnAction() {
        controllerMain.openCampaignOnAction();
    }

    public void setStageAndListeners(Stage stage, ControllerMain controllerMain, AppLogic appLogic) {
        this.stage = stage;
        this.controllerMain = controllerMain;
        this.appLogic = appLogic;

        listView_latestCampaigns.setItems(FXCollections.observableArrayList(appLogic.getConfig().getRecentFiles()));
        listView_latestCampaigns.setCellFactory(param -> new CampaignCell());
        listView_latestCampaigns.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path selectedItem = listView_latestCampaigns.getSelectionModel().getSelectedItem();
                if(selectedItem != null) {
                    controllerMain.openCampaign(selectedItem.toFile());
                }
            }
        });
    }

    public void remove() {

    }
}
