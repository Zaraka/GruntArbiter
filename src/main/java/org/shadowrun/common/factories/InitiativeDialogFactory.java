package org.shadowrun.common.factories;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.shadowrun.common.NumericLimitListener;
import org.shadowrun.common.constants.ICE;
import org.shadowrun.common.nodes.cells.ICeCell;
import org.shadowrun.models.Character;

import java.util.Optional;

public class InitiativeDialogFactory {
    public class Result {
        private Integer initiative;
        private Boolean applyWound;

        Result(int initiative, boolean applyWound) {
            this.applyWound = applyWound;
            this.initiative = initiative;
        }

        public Integer getInitiative() {
            return initiative;
        }

        public Boolean getApplyWound() {
            return applyWound;
        }
    }

    public Dialog<Result> createDialog(Character character, boolean applyWound) {
        Dialog<Result> dialog = new Dialog<>();
        dialog.setTitle("Set initiative");
        dialog.setHeaderText("Set initiative for " + character.getName());

        ButtonType okButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField_initiative = new TextField("0");
        textField_initiative.textProperty().addListener(new NumericLimitListener(textField_initiative, 0, null));

        CheckBox checkBox_applyWound = new CheckBox("Apply wound modifier");
        checkBox_applyWound.setSelected(applyWound);

        grid.add(new Label("Please enter initative:"), 0, 0);
        grid.add(textField_initiative, 1, 0);
        grid.add(checkBox_applyWound, 0, 1);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getClassLoader().getResource("css/dark.css").toExternalForm());

        dialogPane.setContent(grid);
        Platform.runLater(textField_initiative::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                int initiative = Integer.parseInt(textField_initiative.getText());
                int wounded = initiative - (character.getPhysicalMonitor().countWoundModifier() +
                        character.getStunMonitor().countWoundModifier());
                return new Result(
                        (checkBox_applyWound.isSelected()) ? wounded : initiative,
                        checkBox_applyWound.isSelected());
            }
            return null;
        });

        return dialog;

    }
}
