package cuchaz.enigma.gui.dialog;

import cuchaz.enigma.gui.Gui;
import cuchaz.enigma.gui.config.UiConfig;
import cuchaz.enigma.gui.elements.ValidatableTextField;
import cuchaz.enigma.gui.util.GridBagConstraintsBuilder;
import cuchaz.enigma.utils.I18n;
import cuchaz.enigma.utils.validation.Message;
import cuchaz.enigma.utils.validation.ValidationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class KeybindingDialog extends JDialog {
    private final Map<String, Entry> entries = new HashMap<>();

    private final Gui gui;
    private final ValidationContext vc;

    private KeybindingDialog(Gui gui) {
        super(gui.getFrame(), I18n.translate("menu.view.keybinding.title"), true);
        this.setLayout(new BorderLayout());
        this.gui = gui;
        this.vc = new ValidationContext();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        this.add(new JScrollPane(contentPanel));

        GridBagConstraintsBuilder cb = GridBagConstraintsBuilder.create().insets(10, 0, 0).fill(GridBagConstraints.HORIZONTAL);
        AtomicInteger index = new AtomicInteger();

        // menu bar items
        this.gui.getMenuBar().getKeybindableItems().forEach((k, v) -> contentPanel.add(new Entry(k.getText(), v), cb.pos(0, index.getAndIncrement()).build()));
        contentPanel.add(new JSeparator(), cb.insets(20, 100, 10).pos(0, index.getAndIncrement()).build());

        // deobf panel items
        this.gui.getDeobfPanelPopupMenu().getKeybindableItems().forEach((k, v) -> contentPanel.add(new Entry(k.getText(), v), cb.pos(0, index.getAndIncrement()).build()));
        contentPanel.add(new JSeparator(), cb.insets(20, 100, 10).pos(0, index.getAndIncrement()).build());

        // editor tab items
        this.gui.getEditorTabPopupMenu().getKeybindableItems().forEach((k, v) -> contentPanel.add(new Entry(k.getText(), v), cb.pos(0, index.getAndIncrement()).build()));
        contentPanel.add(new JSeparator(), cb.insets(20, 100, 10).pos(0, index.getAndIncrement()).build());

        // editor items
        // they are manually added because an editor popup menu is always tied to an open editor panel
        contentPanel.add(new Entry(I18n.translate("popup_menu.rename"), "editor.rename"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.javadoc"), "editor.edit_javadoc"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.inheritance"), "editor.show_inheritance"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.implementations"), "editor.show_implementations"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.calls"), "editor.show_calls"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.calls.specific"), "editor.show_calls.specific"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.declaration"), "editor.open_declaration"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.back"), "editor.open_back"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.forward"), "editor.open_forward"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.mark_deobfuscated"), "editor.toggle_obfuscation"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.zoom.in"), "editor.zoom_in"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.zoom.out"), "editor.zoom_out"), cb.pos(0, index.getAndIncrement()).build());
        contentPanel.add(new Entry(I18n.translate("popup_menu.zoom.reset"), "editor.reset_zoom"), cb.insets(10, 0, 10).pos(0, index.getAndIncrement()).build());

        // buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton(I18n.translate("prompt.cancel"));
        cancelButton.addActionListener(event -> this.dispose());
        buttonsPanel.add(cancelButton);
        JButton resetAllButton = new JButton(I18n.translate("menu.view.keybinding.reset.all"));
        resetAllButton.addActionListener(event -> this.entries.forEach((k, v) -> v.reset()));
        buttonsPanel.add(resetAllButton);
        JButton saveButton = new JButton(I18n.translate("prompt.save"));
        saveButton.addActionListener(event -> this.save());
        buttonsPanel.add(saveButton);
        this.add(buttonsPanel, BorderLayout.SOUTH);

        this.setSize(600, 500);
        this.setLocationRelativeTo(gui.getFrame());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void save() {
        this.vc.reset();

        this.entries.values().forEach(entry -> this.entries.values().forEach(other -> {
            if (!entry.equals(other) && !entry.textField.getText().isBlank() && entry.textField.getText().equals(other.textField.getText())) {
                this.vc.setActiveElement(entry.textField);
                this.vc.raise(Message.NONUNIQUE_KEYBINDING, entry.textField.getText());
            }
        }));

        if (this.vc.canProceed()) {
            this.entries.forEach((k, v) -> {
                String value = this.toKeyStrokeReadable(v.textField.getText());
                UiConfig.setKeybinding(k, value);
            });
            UiConfig.save();
            this.gui.updateKeyStrokes();
            this.dispose();
        }
    }

    private static String toKeyStrokeReadable(String text) {
        if (text.isBlank()) return "";

        String[] parts = text.split("\\+");
        int lastIndex = parts.length - 1;

        for (int i = 0; i < lastIndex; i++) {
            parts[i] = parts[i].substring(0, 1).toLowerCase() + parts[i].substring(1);
        }
        parts[lastIndex] = parts[lastIndex].toUpperCase();

        return String.join(" ", parts);
    }

    private static String fromKeyStrokeReadable(String text) {
        if (text.isBlank()) return "";

        String[] parts = text.split(" ");
        int lastIndex = parts.length - 1;

        for (int i = 0; i < lastIndex; i++) {
            parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
        }
        parts[lastIndex] = parts[lastIndex].charAt(0) + parts[lastIndex].substring(1).toLowerCase();

        return String.join("+", parts);
    }

    public static void show(Gui gui) {
        KeybindingDialog dialog = new KeybindingDialog(gui);
        dialog.setVisible(true);
    }

    class Entry extends JPanel {
        private final String defaultKeybinding;
        private final ValidatableTextField textField;
        private final JButton resetButton;

        public Entry(String labelText, String keybindingName) {
            this.setLayout(new BorderLayout(5, 0));
            this.defaultKeybinding = fromKeyStrokeReadable(UiConfig.getDefaultKeybinding(keybindingName));
            String keybinding = fromKeyStrokeReadable(UiConfig.getKeybinding(keybindingName));

            JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
            panel.add(new JLabel(labelText));

            this.textField = new ValidatableTextField();
            this.textField.setEditable(false);
            this.textField.setText(keybinding);
            this.textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if ((e.isControlDown() || e.isShiftDown()) && !e.isAltGraphDown() && !e.isAltGraphDown() && !e.isMetaDown()) {
                        textField.setText(KeyEvent.getModifiersExText(e.getModifiersEx()) + "+" + KeyEvent.getKeyText(e.getKeyCode()));
                        resetButton.setEnabled(!textField.getText().equals(defaultKeybinding));
                    }
                }
            });
            panel.add(this.textField);

            this.add(panel);

            this.resetButton = new JButton(I18n.translate("menu.view.keybinding.reset"));
            this.resetButton.setEnabled(!textField.getText().equals(defaultKeybinding));
            this.resetButton.addActionListener(event -> this.reset());
            this.add(this.resetButton, BorderLayout.EAST);

            entries.put(keybindingName, this);
        }

        public void reset() {
            this.textField.setText(this.defaultKeybinding);
            this.resetButton.setEnabled(false);
        }

        private static String replaceNumPad(String text) {
            return text.replace("NumPad -", "Subtract").replace("NumPad +", "Add")
                    .replace("NumPad *", "Multiply").replace("NumPad /", "Divide")
                    .replace("-", "");
        }
    }
}
