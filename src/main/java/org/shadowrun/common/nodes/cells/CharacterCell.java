package org.shadowrun.common.nodes.cells;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TableCell;
import org.shadowrun.common.factories.CharacterIconFactory;
import org.shadowrun.models.Character;

public class CharacterCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(null);
            CharacterIconFactory.createIcon(item)
                    .ifPresent(fontAwesomeIcon -> setGraphic(new FontAwesomeIconView(fontAwesomeIcon)));
        }
    }
}
