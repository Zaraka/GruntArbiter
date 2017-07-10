package org.shadowrun.common.cells;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.TableCell;
import org.shadowrun.models.Character;

public class CharacterCell extends TableCell<Character, Character> {

    @Override
    protected void updateItem(Character item, boolean empty) {
        super.updateItem(item, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            if(!item.isNpc()) {
                setGraphic(new FontAwesomeIconView(FontAwesomeIcon.USER));
            } else if (item.isIce()) {
                setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CONNECTDEVELOP));
            } else {
                setGraphic(null);
            }
        }
    }
}
