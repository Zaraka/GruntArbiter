package org.shadowrun.common.nodes.rows;

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import org.shadowrun.common.constants.CompanionType;
import org.shadowrun.common.utils.CSSUtils;
import org.shadowrun.models.Character;
import org.shadowrun.models.Companion;

public class CompanionTableRow extends TableRow<Companion> {
    @Override
    protected void updateItem(Companion item, boolean empty) {
        super.updateItem(item, empty);

        if (!empty && item.getCompanionType().equals(CompanionType.CHARACTER)) {
            ObservableList<String> classes = getStyleClass();
            CSSUtils.setCharacterBackground(((Character) item.getCompanion()), classes);
        } else {
            setStyle(null);
        }
    }
}

