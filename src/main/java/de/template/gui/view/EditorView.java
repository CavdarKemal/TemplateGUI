package de.template.gui.view;

import de.template.gui.design.BaseViewPanel;
import de.template.gui.design.EditorPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Editor view implementation using the BaseView framework.
 * Uses EditorPanel for GUI, this class contains only logic.
 *
 * Pattern:
 * - InternalFrameEditor: GUI only (JFormDesigner generated)
 * - EditorPanel: Wrapper providing BaseViewPanel interface
 * - EditorView: Logic and event handlers only
 *
 * @author ITSQ-Test
 * @version 1.0
 */
public class EditorView extends BaseView {
    private static final Logger LOG = LoggerFactory.getLogger(EditorView.class);

    private EditorPanel editorPanel;
    private static int instanceCount = 0;

    /**
     * Constructs a new EditorView.
     */
    public EditorView() {
        super("Editor " + (++instanceCount));
        LOG.debug("EditorView created: {}", getTitle());
    }

    @Override
    protected BaseViewPanel createPanel() {
        editorPanel = new EditorPanel();
        return editorPanel;
    }

    @Override
    protected void setupToolbarActions() {
        // No specific toolbar actions for editor yet
        // Could add save, load, format actions here
    }

    @Override
    protected void setupListeners() {
        // Checkbox listeners
        editorPanel.getCheckBoxC1().addActionListener(e -> {
            LOG.debug("Editor CheckBox C1 changed: {}", editorPanel.getCheckBoxC1().isSelected());
            onCheckBox1Changed(editorPanel.getCheckBoxC1().isSelected());
        });

        editorPanel.getCheckBoxC2().addActionListener(e -> {
            LOG.debug("Editor CheckBox C2 changed: {}", editorPanel.getCheckBoxC2().isSelected());
            onCheckBox2Changed(editorPanel.getCheckBoxC2().isSelected());
        });

        // Radio button listeners
        editorPanel.getRadioButton1().addActionListener(e -> {
            LOG.debug("Editor RadioButton 1 selected");
            onRadioButton1Selected();
        });

        editorPanel.getRadioButton2().addActionListener(e -> {
            LOG.debug("Editor RadioButton 2 selected");
            onRadioButton2Selected();
        });
    }

    // ===== ViewInfo Implementation =====

    @Override
    public String getMenuLabel() {
        return "Editor";
    }

    @Override
    public String getToolbarLabel() {
        return "Editor";
    }

    @Override
    public KeyStroke getKeyboardShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(getClass().getResource("/icons/folder_edit.png"));
    }

    @Override
    public String getMenuGroup() {
        return "Views";
    }

    // ===== Business Logic =====

    /**
     * Called when checkbox 1 state changes.
     *
     * @param selected true if selected
     */
    protected void onCheckBox1Changed(boolean selected) {
        // Override in subclass or implement logic here
    }

    /**
     * Called when checkbox 2 state changes.
     *
     * @param selected true if selected
     */
    protected void onCheckBox2Changed(boolean selected) {
        // Override in subclass or implement logic here
    }

    /**
     * Called when radio button 1 is selected.
     */
    protected void onRadioButton1Selected() {
        // Override in subclass or implement logic here
    }

    /**
     * Called when radio button 2 is selected.
     */
    protected void onRadioButton2Selected() {
        // Override in subclass or implement logic here
    }

    /**
     * Sets the text content of the editor.
     *
     * @param text the text to set
     */
    public void setText(String text) {
        editorPanel.getEditorPane().setText(text);
    }

    /**
     * Gets the text content of the editor.
     *
     * @return the text content
     */
    public String getText() {
        return editorPanel.getEditorPane().getText();
    }

    /**
     * Sets the content type of the editor.
     *
     * @param contentType the content type (e.g., "text/plain", "text/html")
     */
    public void setContentType(String contentType) {
        editorPanel.getEditorPane().setContentType(contentType);
    }

    // ===== Getters =====

    /**
     * Returns the editor panel.
     *
     * @return the editor panel
     */
    public EditorPanel getEditorPanel() {
        return editorPanel;
    }
}
