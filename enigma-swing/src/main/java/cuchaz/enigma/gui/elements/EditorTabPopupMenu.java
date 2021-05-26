package cuchaz.enigma.gui.elements;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cuchaz.enigma.gui.Gui;
import cuchaz.enigma.gui.panels.EditorPanel;
import cuchaz.enigma.gui.util.Keybindable;
import cuchaz.enigma.utils.I18n;

public class EditorTabPopupMenu implements Keybindable {

	private final JPopupMenu ui;
	private final JMenuItem close;
	private final JMenuItem closeAll;
	private final JMenuItem closeOthers;
	private final JMenuItem closeLeft;
	private final JMenuItem closeRight;

	private final Gui gui;
	private EditorPanel editor;

	public EditorTabPopupMenu(Gui gui) {
		this.gui = gui;

		this.ui = new JPopupMenu();

		this.close = new JMenuItem();
		this.close.addActionListener(a -> gui.closeEditor(editor));
		this.ui.add(this.close);

		this.closeAll = new JMenuItem();
		this.closeAll.addActionListener(a -> gui.closeAllEditorTabs());
		this.ui.add(this.closeAll);

		this.closeOthers = new JMenuItem();
		this.closeOthers.addActionListener(a -> gui.closeTabsExcept(editor));
		this.ui.add(this.closeOthers);

		this.closeLeft = new JMenuItem();
		this.closeLeft.addActionListener(a -> gui.closeTabsLeftOf(editor));
		this.ui.add(this.closeLeft);

		this.closeRight = new JMenuItem();
		this.closeRight.addActionListener(a -> gui.closeTabsRightOf(editor));
		this.ui.add(this.closeRight);

		this.retranslateUi();
		this.setupKeyStrokes();
	}

	public void show(Component invoker, int x, int y, EditorPanel editorPanel) {
		this.editor = editorPanel;
		ui.show(invoker, x, y);
	}

	public void retranslateUi() {
		this.close.setText(I18n.translate("popup_menu.editor_tab.close"));
		this.closeAll.setText(I18n.translate("popup_menu.editor_tab.close_all"));
		this.closeOthers.setText(I18n.translate("popup_menu.editor_tab.close_others"));
		this.closeLeft.setText(I18n.translate("popup_menu.editor_tab.close_left"));
		this.closeRight.setText(I18n.translate("popup_menu.editor_tab.close_right"));
	}

	@Override
	public Map<JMenuItem, String> getKeybindableItems() {
		Map<JMenuItem, String> map = new LinkedHashMap<>();
		map.put(this.close, "editor_tab.close");
		map.put(this.closeAll, "editor_tab.close_all");
		map.put(this.closeOthers, "editor_tab.close_others");
		map.put(this.closeLeft, "editor_tab.close_left");
		map.put(this.closeRight, "editor_tab.close_right");
		return map;
	}
}
