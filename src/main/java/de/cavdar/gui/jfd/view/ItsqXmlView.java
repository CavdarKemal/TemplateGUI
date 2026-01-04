package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqXmlPanel;
import de.cavdar.gui.jfd.model.ItsqItem;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * View for displaying and editing XML files with syntax highlighting.
 * Uses RSyntaxTextArea for:
 * - XML syntax highlighting
 * - Line numbers
 * - Code folding
 * - Find/Replace
 * - Copy/Paste (built-in)
 */
public class ItsqXmlView extends ItsqXmlPanel implements ItsqItemSelectable {
    private static final Logger LOG = LoggerFactory.getLogger(ItsqXmlView.class);

    private ItsqItem selectedItem;
    private RSyntaxTextArea textArea;
    private RTextScrollPane rTextScrollPane;
    private JToolBar toolbar;
    private JButton buttonSave;
    private JComboBox<String> searchComboBox;
    private DefaultComboBoxModel<String> searchHistoryModel;
    private JLabel statusLabel;
    private boolean modified = false;
    private static final int MAX_SEARCH_HISTORY = 20;

    public ItsqXmlView() {
        super();
        initSyntaxEditor();
        setupToolbar();
        setupKeyboardShortcuts();
    }

    /**
     * Initializes RSyntaxTextArea and replaces the default JEditorPane.
     */
    private void initSyntaxEditor() {
        // Create RSyntaxTextArea
        textArea = new RSyntaxTextArea(20, 60);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.setAutoIndentEnabled(true);
        textArea.setBracketMatchingEnabled(true);
        textArea.setCloseCurlyBraces(true);
        textArea.setMarkOccurrences(true);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        // Create scroll pane with line numbers
        rTextScrollPane = new RTextScrollPane(textArea);
        rTextScrollPane.setLineNumbersEnabled(true);
        rTextScrollPane.setFoldIndicatorEnabled(true);

        // Get references before replacing scrollPane
        Container parent = getScrollPaneEditor().getParent();
        if (parent != null) {
            // Find the toolbar (it's the first component at NORTH)
            for (Component c : parent.getComponents()) {
                if (c instanceof JToolBar) {
                    toolbar = (JToolBar) c;
                    // Save button is the first component
                    if (toolbar.getComponentCount() > 0 && toolbar.getComponent(0) instanceof JButton) {
                        buttonSave = (JButton) toolbar.getComponent(0);
                    }
                    break;
                }
            }
            // Replace the scrollPane
            parent.remove(getScrollPaneEditor());
            parent.add(rTextScrollPane, BorderLayout.CENTER);
        }

        // Track modifications
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { setModified(true); }
        });
    }

    /**
     * Sets up the toolbar with additional buttons.
     */
    private void setupToolbar() {
        if (toolbar == null) {
            LOG.warn("Toolbar not found - cannot add search buttons");
            return;
        }

        toolbar.addSeparator();

        // Search combo box with history
        searchHistoryModel = new DefaultComboBoxModel<>();
        searchComboBox = new JComboBox<>(searchHistoryModel);
        searchComboBox.setEditable(true);
        searchComboBox.setMaximumSize(new Dimension(200, 25));
        searchComboBox.setToolTipText("Suchbegriff eingeben, Enter zum Suchen (Strg+F)");

        // Get the editor component for key handling
        JTextField editorField = (JTextField) searchComboBox.getEditor().getEditorComponent();
        editorField.addActionListener(e -> {
            findNext();
            addToSearchHistory(getSearchText());
        });
        toolbar.add(searchComboBox);

        // Find Next button
        JButton findNextButton = new JButton("\u25BC"); // Down arrow
        findNextButton.setToolTipText("Weitersuchen (F3)");
        findNextButton.addActionListener(e -> findNext());
        toolbar.add(findNextButton);

        // Find Previous button
        JButton findPrevButton = new JButton("\u25B2"); // Up arrow
        findPrevButton.setToolTipText("Rückwärts suchen (Shift+F3)");
        findPrevButton.addActionListener(e -> findPrevious());
        toolbar.add(findPrevButton);

        toolbar.addSeparator();

        // Reload button
        JButton reloadButton = new JButton();
        reloadButton.setIcon(loadIcon("/icons/refresh.png"));
        reloadButton.setToolTipText("Datei neu laden");
        reloadButton.addActionListener(e -> reloadFile());
        toolbar.add(reloadButton);

        // Spacer
        toolbar.add(Box.createHorizontalGlue());

        // Status label
        statusLabel = new JLabel("");
        toolbar.add(statusLabel);

        // Configure Save button
        if (buttonSave != null) {
            buttonSave.addActionListener(e -> saveFile());
        }
    }

    /**
     * Sets up keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        // Ctrl+F - Find
        KeyStroke findKey = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
        textArea.getInputMap().put(findKey, "find");
        textArea.getActionMap().put("find", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchComboBox.requestFocusInWindow();
                searchComboBox.getEditor().selectAll();
            }
        });

        // F3 - Find Next
        KeyStroke f3Key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
        textArea.getInputMap().put(f3Key, "findNext");
        textArea.getActionMap().put("findNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { findNext(); }
        });

        // Shift+F3 - Find Previous
        KeyStroke shiftF3Key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK);
        textArea.getInputMap().put(shiftF3Key, "findPrev");
        textArea.getActionMap().put("findPrev", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { findPrevious(); }
        });

        // Ctrl+S - Save
        KeyStroke saveKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        textArea.getInputMap().put(saveKey, "save");
        textArea.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { saveFile(); }
        });

        // Ctrl+G - Go to Line
        KeyStroke gotoKey = KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK);
        textArea.getInputMap().put(gotoKey, "gotoLine");
        textArea.getActionMap().put("gotoLine", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { showGoToLineDialog(); }
        });
    }

    // ===== ItsqItemSelectable Implementation =====

    @Override
    public void setSelectedItem(ItsqItem item) {
        // Check for unsaved changes
        if (modified && selectedItem != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Änderungen in " + selectedItem.getName() + " speichern?",
                    "Ungespeicherte Änderungen",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        this.selectedItem = item;
        loadFile();
    }

    public ItsqItem getSelectedItem() {
        return selectedItem;
    }

    // ===== File Operations =====

    /**
     * Loads the selected XML file into the editor.
     */
    private void loadFile() {
        if (selectedItem == null || selectedItem.getFile() == null) {
            textArea.setText("");
            updateTitle(null);
            return;
        }

        File file = selectedItem.getFile();
        if (!file.exists() || !file.isFile()) {
            textArea.setText("Datei nicht gefunden: " + file.getAbsolutePath());
            updateTitle(file.getName() + " (nicht gefunden)");
            return;
        }

        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            textArea.setText(content);
            textArea.setCaretPosition(0);
            setModified(false);
            updateTitle(file.getName());
            updateStatus("Geladen: " + file.getName());
            LOG.info("Loaded XML file: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to load file: {}", file.getAbsolutePath(), e);
            textArea.setText("Fehler beim Laden: " + e.getMessage());
            updateTitle(file.getName() + " (Fehler)");
        }
    }

    /**
     * Reloads the current file.
     */
    private void reloadFile() {
        if (modified) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Änderungen verwerfen und neu laden?",
                    "Neu laden",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        loadFile();
    }

    /**
     * Saves the current content to file.
     */
    private void saveFile() {
        if (selectedItem == null || selectedItem.getFile() == null) {
            JOptionPane.showMessageDialog(this, "Keine Datei ausgewählt");
            return;
        }

        File file = selectedItem.getFile();
        try {
            Files.writeString(file.toPath(), textArea.getText(), StandardCharsets.UTF_8);
            setModified(false);
            updateStatus("Gespeichert: " + file.getName());
            LOG.info("Saved XML file: {}", file.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to save file: {}", file.getAbsolutePath(), e);
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Speichern: " + e.getMessage(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Search Operations =====

    /**
     * Focuses the search combo box.
     */
    private void showFindDialog() {
        searchComboBox.requestFocusInWindow();
        searchComboBox.getEditor().selectAll();
    }

    /**
     * Gets the current search text from the combo box.
     */
    private String getSearchText() {
        Object item = searchComboBox.getEditor().getItem();
        return item != null ? item.toString().trim() : "";
    }

    /**
     * Adds a search term to the history (at the top, avoiding duplicates).
     */
    private void addToSearchHistory(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        // Remove if already exists (to move to top)
        searchHistoryModel.removeElement(searchText);

        // Add at the beginning
        searchHistoryModel.insertElementAt(searchText, 0);

        // Limit history size
        while (searchHistoryModel.getSize() > MAX_SEARCH_HISTORY) {
            searchHistoryModel.removeElementAt(searchHistoryModel.getSize() - 1);
        }

        // Keep the text in the editor
        searchComboBox.getEditor().setItem(searchText);
    }

    /**
     * Finds the next occurrence of the search text.
     */
    private void findNext() {
        String searchText = getSearchText();
        if (searchText.isEmpty()) {
            return;
        }

        SearchContext context = new SearchContext(searchText);
        context.setMatchCase(false);
        context.setWholeWord(false);
        context.setSearchForward(true);

        boolean found = SearchEngine.find(textArea, context).wasFound();
        if (!found) {
            // Wrap around
            textArea.setCaretPosition(0);
            found = SearchEngine.find(textArea, context).wasFound();
        }

        if (!found) {
            updateStatus("Nicht gefunden: " + searchText);
        } else {
            addToSearchHistory(searchText);
            updateStatus("");
        }
    }

    /**
     * Finds the previous occurrence of the search text.
     */
    private void findPrevious() {
        String searchText = getSearchText();
        if (searchText.isEmpty()) {
            return;
        }

        SearchContext context = new SearchContext(searchText);
        context.setMatchCase(false);
        context.setWholeWord(false);
        context.setSearchForward(false);

        boolean found = SearchEngine.find(textArea, context).wasFound();
        if (!found) {
            // Wrap around
            textArea.setCaretPosition(textArea.getText().length());
            found = SearchEngine.find(textArea, context).wasFound();
        }

        if (!found) {
            updateStatus("Nicht gefunden: " + searchText);
        } else {
            addToSearchHistory(searchText);
            updateStatus("");
        }
    }

    /**
     * Shows dialog to go to a specific line.
     */
    private void showGoToLineDialog() {
        String input = JOptionPane.showInputDialog(this,
                "Zeilennummer (1-" + textArea.getLineCount() + "):",
                "Gehe zu Zeile",
                JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                int line = Integer.parseInt(input) - 1;
                if (line >= 0 && line < textArea.getLineCount()) {
                    textArea.setCaretPosition(textArea.getLineStartOffset(line));
                    textArea.requestFocusInWindow();
                }
            } catch (NumberFormatException | javax.swing.text.BadLocationException e) {
                // Ignore invalid input
            }
        }
    }

    // ===== UI Helpers =====

    private void setModified(boolean modified) {
        this.modified = modified;
        if (selectedItem != null) {
            updateTitle(selectedItem.getName() + (modified ? " *" : ""));
        }
    }

    private void updateTitle(String title) {
        // Update the label in panelControls
        Component[] components = getPanelControls().getComponents();
        for (Component c : components) {
            if (c instanceof JLabel label) {
                label.setText(title != null ? title : "Keine Datei");
                break;
            }
        }
    }

    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    private Icon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        }
        return null;
    }

    // ===== Public Accessors =====

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public boolean isModified() {
        return modified;
    }
}
