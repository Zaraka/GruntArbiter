package org.shadowrun.common.nodes.cells;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.shadowrun.models.PlayerCharacter;

public class PlayerImageCell extends TableCell<PlayerCharacter, Image> {
    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);

        if(!empty && item != null) {
            if(getGraphic() == null) {
                setGraphic(new ImageView(item));
            } else {
                ImageView imageView = ((ImageView)getGraphic());
                if(!item.equals(imageView.getImage())) {
                    imageView.setImage(item);
                    updateBounds();
                }
            }
        } else {
            setGraphic(null);
        }
        getTableView().refresh();
    }
}
