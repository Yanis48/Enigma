package cuchaz.enigma.gui.util;

import cuchaz.enigma.gui.config.UiConfig;

import javax.swing.*;
import java.util.Map;

/**
 * Represents a component which contains menu items associated to keybindings.
 */
public interface Keybindable {

    /**
     * Returns a map composed of the keybindable menu items of this component
     * and the name they should be saved with in the config.
     */
    Map<JMenuItem, String> getKeybindableItems();

    /**
     * Sets the accelerator of each keybindable item of this component with
     * the value set in the config.
     */
    default void setupKeyStrokes() {
        this.getKeybindableItems().forEach((k, v) -> k.setAccelerator(KeyStroke.getKeyStroke(UiConfig.getKeybinding(v))));
    }
}
