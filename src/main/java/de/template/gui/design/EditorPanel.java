package de.template.gui.design;

import javax.swing.*;
import java.awt.*;

/**
 * Wrapper panel for InternalFrameEditor (JFormDesigner-generated).
 * Provides unified access to components through BaseViewPanel interface.
 *
 * Note: InternalFrameEditor has no progress bar or cancel button,
 * so this panel adds a status panel at the bottom with these components.
 *
 * Pattern:
 * - InternalFrameEditor: GUI only (JFormDesigner generated)
 * - EditorPanel: Wrapper providing BaseViewPanel interface
 * - EditorView: Logic and event handlers
 *
 * @author ITSQ-Test
 * @version 1.0
 */
public class EditorPanel extends BaseViewPanel {

    private InternalFrameEditor editor;

    // Status bar components (not in JFD panel)
    private JPanel statusPanel;
    private JProgressBar progressBar;
    private JButton cancelButton;

    /**
     * Constructs the EditorPanel with embedded InternalFrameEditor.
     */
    public EditorPanel() {
        super();
    }

    @Override
    protected void initComponents() {
        // Create and add the JFormDesigner-generated editor
        editor = new InternalFrameEditor();
        add(editor, BorderLayout.CENTER);

        // Add status panel at bottom (not in JFD panel)
        statusPanel = new JPanel(new BorderLayout());

        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        cancelButton = new JButton("Cancel");
        cancelButton.setVisible(false);

        statusPanel.add(progressBar, BorderLayout.CENTER);
        statusPanel.add(cancelButton, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    // ===== BaseViewPanel Implementation =====

    @Override
    public JToolBar getViewToolbar() {
        return editor.getToolBarMain();
    }

    @Override
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    public JButton getCancelButton() {
        return cancelButton;
    }

    // ===== Delegated Getters for Editor-specific components =====

    /**
     * Returns the embedded InternalFrameEditor.
     *
     * @return the editor panel
     */
    public InternalFrameEditor getEditor() {
        return editor;
    }

    /**
     * Returns the editor pane for text editing.
     *
     * @return the editor pane
     */
    public JEditorPane getEditorPane() {
        return editor.getEditorPane();
    }

    /**
     * Returns the first checkbox in toolbar.
     *
     * @return checkbox 1
     */
    public JCheckBox getCheckBoxC1() {
        return editor.getCheckBoxC1();
    }

    /**
     * Returns the second checkbox in toolbar.
     *
     * @return checkbox 2
     */
    public JCheckBox getCheckBoxC2() {
        return editor.getCheckBoxC2();
    }

    /**
     * Returns the first radio button in toolbar.
     *
     * @return radio button 1
     */
    public JRadioButton getRadioButton1() {
        return editor.getRadioButton1();
    }

    /**
     * Returns the second radio button in toolbar.
     *
     * @return radio button 2
     */
    public JRadioButton getRadioButton2() {
        return editor.getRadioButton2();
    }

    /**
     * Returns the scroll pane containing the editor.
     *
     * @return the scroll pane
     */
    public JScrollPane getScrollPane1() {
        return editor.getScrollPane1();
    }

    /**
     * Returns the status panel at the bottom.
     *
     * @return the status panel
     */
    public JPanel getStatusPanel() {
        return statusPanel;
    }
}
