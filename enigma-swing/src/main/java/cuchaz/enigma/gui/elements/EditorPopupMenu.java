package cuchaz.enigma.gui.elements;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cuchaz.enigma.analysis.EntryReference;
import cuchaz.enigma.gui.Gui;
import cuchaz.enigma.gui.GuiController;
import cuchaz.enigma.gui.panels.EditorPanel;
import cuchaz.enigma.gui.util.Keybindable;
import cuchaz.enigma.translation.representation.entry.ClassEntry;
import cuchaz.enigma.translation.representation.entry.Entry;
import cuchaz.enigma.translation.representation.entry.FieldEntry;
import cuchaz.enigma.translation.representation.entry.MethodEntry;
import cuchaz.enigma.utils.I18n;

public class EditorPopupMenu implements Keybindable {

	private final JPopupMenu ui = new JPopupMenu();

	private final JMenuItem renameItem = new JMenuItem();
	private final JMenuItem editJavadocItem = new JMenuItem();
	private final JMenuItem showInheritanceItem = new JMenuItem();
	private final JMenuItem showImplementationsItem = new JMenuItem();
	private final JMenuItem showCallsItem = new JMenuItem();
	private final JMenuItem showCallsSpecificItem = new JMenuItem();
	private final JMenuItem openEntryItem = new JMenuItem();
	private final JMenuItem openPreviousItem = new JMenuItem();
	private final JMenuItem openNextItem = new JMenuItem();
	private final JMenuItem toggleMappingItem = new JMenuItem();
	private final JMenuItem zoomInItem = new JMenuItem();
	private final JMenuItem zoomOutMenu = new JMenuItem();
	private final JMenuItem resetZoomItem = new JMenuItem();

	private final EditorPanel editor;
	private final Gui gui;

	public EditorPopupMenu(EditorPanel editor, Gui gui) {
		this.editor = editor;
		this.gui = gui;

		this.retranslateUi();
		this.setupKeyStrokes();

		this.ui.add(this.renameItem);
		this.ui.add(this.editJavadocItem);
		this.ui.add(this.showInheritanceItem);
		this.ui.add(this.showImplementationsItem);
		this.ui.add(this.showCallsItem);
		this.ui.add(this.showCallsSpecificItem);
		this.ui.add(this.openEntryItem);
		this.ui.add(this.openPreviousItem);
		this.ui.add(this.openNextItem);
		this.ui.add(this.toggleMappingItem);
		this.ui.addSeparator();
		this.ui.add(this.zoomInItem);
		this.ui.add(this.zoomOutMenu);
		this.ui.add(this.resetZoomItem);

		this.renameItem.setEnabled(false);
		this.editJavadocItem.setEnabled(false);
		this.showInheritanceItem.setEnabled(false);
		this.showImplementationsItem.setEnabled(false);
		this.showCallsItem.setEnabled(false);
		this.showCallsSpecificItem.setEnabled(false);
		this.openEntryItem.setEnabled(false);
		this.openPreviousItem.setEnabled(false);
		this.openNextItem.setEnabled(false);
		this.toggleMappingItem.setEnabled(false);

		this.renameItem.addActionListener(event -> gui.startRename(editor));
		this.editJavadocItem.addActionListener(event -> gui.startDocChange(editor));
		this.showInheritanceItem.addActionListener(event -> gui.showInheritance(editor));
		this.showImplementationsItem.addActionListener(event -> gui.showImplementations(editor));
		this.showCallsItem.addActionListener(event -> gui.showCalls(editor, true));
		this.showCallsSpecificItem.addActionListener(event -> gui.showCalls(editor, false));
		this.openEntryItem.addActionListener(event -> gui.getController().navigateTo(editor.getCursorReference().entry));
		this.openPreviousItem.addActionListener(event -> gui.getController().openPreviousReference());
		this.openNextItem.addActionListener(event -> gui.getController().openNextReference());
		this.toggleMappingItem.addActionListener(event -> gui.toggleMapping(editor));
		this.zoomInItem.addActionListener(event -> editor.offsetEditorZoom(2));
		this.zoomOutMenu.addActionListener(event -> editor.offsetEditorZoom(-2));
		this.resetZoomItem.addActionListener(event -> editor.resetEditorZoom());
	}

	// TODO have editor redirect key event to menu so that the actions get
	//  	triggered without having to hardcode them here, because this
	//		is a hack
	public boolean handleKeyEvent(KeyEvent event) {
		if (event.isControlDown()) {
			switch (event.getKeyCode()) {
				case KeyEvent.VK_I:
					this.showInheritanceItem.doClick();
					return true;
				case KeyEvent.VK_M:
					this.showImplementationsItem.doClick();
					return true;
				case KeyEvent.VK_N:
					this.openEntryItem.doClick();
					return true;
				case KeyEvent.VK_P:
					this.openPreviousItem.doClick();
					return true;
				case KeyEvent.VK_E:
					this.openNextItem.doClick();
					return true;
				case KeyEvent.VK_C:
					if (event.isShiftDown()) {
						this.showCallsSpecificItem.doClick();
					} else {
						this.showCallsItem.doClick();
					}
					return true;
				case KeyEvent.VK_O:
					this.toggleMappingItem.doClick();
					return true;
				case KeyEvent.VK_R:
					this.renameItem.doClick();
					return true;
				case KeyEvent.VK_D:
					this.editJavadocItem.doClick();
					return true;
			}
		}
		return false;
	}

	public void updateUiState() {
		EntryReference<Entry<?>, Entry<?>> ref = this.editor.getCursorReference();
		Entry<?> referenceEntry = ref == null ? null : ref.entry;
		GuiController controller = this.gui.getController();

		boolean isClassEntry = referenceEntry instanceof ClassEntry;
		boolean isFieldEntry = referenceEntry instanceof FieldEntry;
		boolean isMethodEntry = referenceEntry instanceof MethodEntry && !((MethodEntry) referenceEntry).isConstructor();
		boolean isConstructorEntry = referenceEntry instanceof MethodEntry && ((MethodEntry) referenceEntry).isConstructor();
		boolean isRenamable = ref != null && controller.project.isRenamable(ref);

		this.renameItem.setEnabled(isRenamable);
		this.editJavadocItem.setEnabled(isRenamable);
		this.showInheritanceItem.setEnabled(isClassEntry || isMethodEntry || isConstructorEntry);
		this.showImplementationsItem.setEnabled(isClassEntry || isMethodEntry);
		this.showCallsItem.setEnabled(isClassEntry || isFieldEntry || isMethodEntry || isConstructorEntry);
		this.showCallsSpecificItem.setEnabled(isMethodEntry);
		this.openEntryItem.setEnabled(isRenamable && (isClassEntry || isFieldEntry || isMethodEntry || isConstructorEntry));
		this.openPreviousItem.setEnabled(controller.hasPreviousReference());
		this.openNextItem.setEnabled(controller.hasNextReference());
		this.toggleMappingItem.setEnabled(isRenamable);

		if (referenceEntry != null && this.gui.getController().project.getMapper().extendedDeobfuscate(referenceEntry).isDeobfuscated()) {
			this.toggleMappingItem.setText(I18n.translate("popup_menu.reset_obfuscated"));
		} else {
			this.toggleMappingItem.setText(I18n.translate("popup_menu.mark_deobfuscated"));
		}
	}

	public void retranslateUi() {
		this.renameItem.setText(I18n.translate("popup_menu.rename"));
		this.editJavadocItem.setText(I18n.translate("popup_menu.javadoc"));
		this.showInheritanceItem.setText(I18n.translate("popup_menu.inheritance"));
		this.showImplementationsItem.setText(I18n.translate("popup_menu.implementations"));
		this.showCallsItem.setText(I18n.translate("popup_menu.calls"));
		this.showCallsSpecificItem.setText(I18n.translate("popup_menu.calls.specific"));
		this.openEntryItem.setText(I18n.translate("popup_menu.declaration"));
		this.openPreviousItem.setText(I18n.translate("popup_menu.back"));
		this.openNextItem.setText(I18n.translate("popup_menu.forward"));
		this.toggleMappingItem.setText(I18n.translate("popup_menu.mark_deobfuscated"));
		this.zoomInItem.setText(I18n.translate("popup_menu.zoom.in"));
		this.zoomOutMenu.setText(I18n.translate("popup_menu.zoom.out"));
		this.resetZoomItem.setText(I18n.translate("popup_menu.zoom.reset"));
	}

	@Override
	public Map<JMenuItem, String> getKeybindableItems() {
		Map<JMenuItem, String> map = new LinkedHashMap<>();
		map.put(this.renameItem, "editor.rename");
		map.put(this.editJavadocItem, "editor.edit_javadoc");
		map.put(this.showInheritanceItem, "editor.show_inheritance");
		map.put(this.showImplementationsItem, "editor.show_implementations");
		map.put(this.showCallsItem, "editor.show_calls");
		map.put(this.showCallsSpecificItem, "editor.show_calls.specific");
		map.put(this.openEntryItem, "editor.open_declaration");
		map.put(this.openPreviousItem, "editor.open_back");
		map.put(this.openNextItem, "editor.open_forward");
		map.put(this.toggleMappingItem, "editor.toggle_obfuscation");
		map.put(this.zoomInItem, "editor.zoom_in");
		map.put(this.zoomOutMenu, "editor.zoom_out");
		map.put(this.resetZoomItem, "editor.reset_zoom");
		return map;
	}

	public JPopupMenu getUi() {
		return ui;
	}

}
