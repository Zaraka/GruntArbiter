package org.shadowrun.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.shadowrun.common.nodes.cells.CampaignCell;
import org.shadowrun.logic.AppLogic;

import java.nio.file.Path;

public class WelcomeScreen {

    private Main main;
    private AppLogic appLogic;
    private Stage stage;

    @FXML
    private ListView<Path> listView_latestCampaigns;

    @FXML
    private void createNewCampaignOnAction() {
        main.newCampaignOnAction();
    }

    @FXML
    private void openCampaignOnAction() {
        main.openCampaignOnAction();
    }

    public void setStageAndListeners(Stage stage, Main main, AppLogic appLogic) {
        this.stage = stage;
        this.main = main;
        this.appLogic = appLogic;

        listView_latestCampaigns.setItems(FXCollections.observableArrayList(appLogic.getConfig().getRecentFiles()));
        listView_latestCampaigns.setCellFactory(param -> new CampaignCell(main));
        listView_latestCampaigns.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path selectedItem = listView_latestCampaigns.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    main.closeCampaignOnAction();
                    main.openCampaign(selectedItem.toFile());
                }
            }
        });
    }

    public void remove() {

    }
}
