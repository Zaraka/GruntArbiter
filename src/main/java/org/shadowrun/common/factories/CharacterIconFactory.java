package org.shadowrun.common.factories;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import org.shadowrun.models.Character;

import java.util.Optional;

public class CharacterIconFactory {
    public static Optional<FontAwesomeIcon> createIcon(Character character) {
        if(!character.isNpc()) {
            return Optional.of(FontAwesomeIcon.USER);
        } else if (character.isIce()) {
            return Optional.of(FontAwesomeIcon.CONNECTDEVELOP);
        } else {
            return Optional.empty();
        }
    }
}
