package de.cavdar.gui.itsq.view;

import de.cavdar.gui.design.base.BaseViewPanel;
import de.cavdar.gui.itsq.design.ItsqMainPanel;
import de.cavdar.gui.model.base.AppConfig;
import de.cavdar.gui.view.base.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import static de.cavdar.gui.util.AppConstants.*;

/**
 * ITSQ Explorer View - Coordinator for ITSQ test set management.
 *
 * Responsibilities:
 * - Control panel management (TestSet selection, filters)
 * - Coordination between ItsqTreeView and ItsqViewTabView
 *
 * Delegates to:
 * - ItsqTreeView: Tree model, expansion, selection handling
 * - ItsqViewTabView: Card switching, item display
 *
 * @author TemplateGUI
 * @version 4.0
 */
public class ItsqExplorerView extends BaseView {
    private static final Logger LOG = LoggerFactory.getLogger(ItsqExplorerView.class);

    private static final String TESTSET_HISTORY_KEY = "itsq.testset.history";
    private static final int MAX_HISTORY = 20;

    private ItsqMainPanel mainPanel;
    private final AppConfig cfg = AppConfig.getInstance();
    private DefaultComboBoxModel<String> testSetHistoryModel;
    private boolean updatingComboBox = false;
    private boolean initialLoadDone = false;
    private String lastValidSelection = null;

    public ItsqExplorerView() {
        super("ITSQ-Test-Sets Verwalten");
        setSize(1000, 700);

        // Initialize TestSet ComboBox with history
        initTestSetComboBox();

        // Setup tree selection callback -> delegate to ViewTabView
        getTreeView().setSelectionCallback(node -> getViewTabView().showCardForNode(node));

        // Load data only when view is first activated
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                if (!initialLoadDone) {
                    initialLoadDone = true;
                    SwingUtilities.invokeLater(() -> loadItsqDirectory());
                }
            }
        });

        LOG.debug("ItsqExplorerView created");
    }

    @Override
    protected BaseViewPanel createPanel() {
        mainPanel = new ItsqMainPanel();
        return new ItsqMainPanelWrapper(mainPanel);
    }

    @Override
    protected void setupToolbarActions() {
        // No additional toolbar - ItsqMainPanel has its own controls
    }

    @Override
    protected void setupListeners() {
        // Load button
        mainPanel.getButtonLoad().addActionListener(e -> browseItsqPath());

        // ComboBox selection change -> reload tree
        mainPanel.getComboBoxTestSet().addActionListener(e -> onTestSetSelectionChanged());

        // Filter ComboBox -> apply filter
        mainPanel.getComboBoxFilter().addActionListener(e -> applyFilter());
        JTextField filterEditor = (JTextField) mainPanel.getComboBoxFilter().getEditor().getEditorComponent();
        filterEditor.addActionListener(e -> applyFilter());

        // Active Only checkbox -> apply filter
        mainPanel.getCheckBoxActiveOnly().addActionListener(e -> applyFilter());

        // Source filter -> apply filter
        mainPanel.getComboBoxTestSetSource().addActionListener(e -> applyFilter());

        // Phase filter -> apply filter
        mainPanel.getComboBoxPhase().addActionListener(e -> applyFilter());
    }

    // ===== Control Panel: TestSet ComboBox =====

    @SuppressWarnings("unchecked")
    private void initTestSetComboBox() {
        testSetHistoryModel = new DefaultComboBoxModel<>();
        JComboBox<String> comboBox = mainPanel.getComboBoxTestSet();
        comboBox.setModel(testSetHistoryModel);
        comboBox.setEditable(true);

        updatingComboBox = true;
        try {
            loadTestSetHistory();
        } finally {
            updatingComboBox = false;
        }

        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.addActionListener(e -> {
            String path = editor.getText().trim();
            if (!path.isEmpty()) {
                loadTestSetPath(path);
            }
        });
    }

    private void loadTestSetHistory() {
        String history = cfg.getProperty(TESTSET_HISTORY_KEY);
        if (history != null && !history.isEmpty()) {
            for (String path : history.split("\\|")) {
                if (!path.isEmpty()) {
                    testSetHistoryModel.addElement(path);
                }
            }
        }
    }

    private void saveTestSetHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testSetHistoryModel.getSize(); i++) {
            if (i > 0) sb.append("|");
            sb.append(testSetHistoryModel.getElementAt(i));
        }
        cfg.setProperty(TESTSET_HISTORY_KEY, sb.toString());
        cfg.save();
    }

    private void addToTestSetHistory(String path) {
        if (path == null || path.isEmpty()) return;

        updatingComboBox = true;
        try {
            testSetHistoryModel.removeElement(path);
            testSetHistoryModel.insertElementAt(path, 0);
            while (testSetHistoryModel.getSize() > MAX_HISTORY) {
                testSetHistoryModel.removeElementAt(testSetHistoryModel.getSize() - 1);
            }
            testSetHistoryModel.setSelectedItem(path);
            saveTestSetHistory();
        } finally {
            updatingComboBox = false;
        }
    }

    private void onTestSetSelectionChanged() {
        if (updatingComboBox) return;

        Object selected = mainPanel.getComboBoxTestSet().getSelectedItem();
        if (selected != null) {
            String path = selected.toString().trim();
            if (!path.isEmpty()) {
                loadTestSetPath(path);
            }
        }
    }

    private void loadTestSetPath(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            cfg.setProperty(ITSQ_PATH_KEY, path);
            cfg.save();
            lastValidSelection = path;
            loadItsqDirectory();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Verzeichnis nicht gefunden: " + path,
                    "Fehler", JOptionPane.ERROR_MESSAGE);

            updatingComboBox = true;
            try {
                testSetHistoryModel.removeElement(path);
                saveTestSetHistory();
                if (lastValidSelection != null) {
                    mainPanel.getComboBoxTestSet().setSelectedItem(lastValidSelection);
                } else if (testSetHistoryModel.getSize() > 0) {
                    mainPanel.getComboBoxTestSet().setSelectedIndex(0);
                }
            } finally {
                updatingComboBox = false;
            }
        }
    }

    // ===== Control Panel: Filter =====

    private String getFilterText() {
        Object selected = mainPanel.getComboBoxFilter().getSelectedItem();
        return selected != null ? selected.toString().trim() : "";
    }

    private void applyFilter() {
        if (updatingComboBox || !initialLoadDone) return;
        loadItsqDirectory();
    }

    @SuppressWarnings("unchecked")
    private void addToFilterHistory(String filterText) {
        if (filterText == null || filterText.isEmpty()) return;

        JComboBox<String> filterCombo = mainPanel.getComboBoxFilter();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) filterCombo.getModel();

        for (int i = 0; i < model.getSize(); i++) {
            if (filterText.equals(model.getElementAt(i))) return;
        }

        model.insertElementAt(filterText, 0);
        if (model.getSize() > 20) {
            model.removeElementAt(model.getSize() - 1);
        }
    }

    // ===== Coordination: Load Directory =====

    private void loadItsqDirectory() {
        File itsqDir = resolveItsqPath();

        if (!itsqDir.exists() || !itsqDir.isDirectory()) {
            LOG.warn("ITSQ directory not found: {}", itsqDir.getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    "ITSQ-Verzeichnis nicht gefunden: " + itsqDir.getAbsolutePath(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Collect filter settings
        String filterText = getFilterText();
        boolean activeOnly = mainPanel.getCheckBoxActiveOnly().isSelected();
        String sourceFilter = (String) mainPanel.getComboBoxTestSetSource().getSelectedItem();
        String phaseFilter = (String) mainPanel.getComboBoxPhase().getSelectedItem();

        // Delegate to TreeView
        getTreeView().reload(itsqDir, filterText, activeOnly, sourceFilter, phaseFilter);

        // Show root card
        getViewTabView().showRootCard();

        // Update history
        String path = itsqDir.getAbsolutePath();
        addToTestSetHistory(path);
        lastValidSelection = path;
        addToFilterHistory(filterText);

        LOG.info("Loaded ITSQ: {} ({} files, {} dirs, filter: '{}', source: '{}', phase: '{}')",
                path, getTreeView().getTotalFiles(), getTreeView().getTotalDirs(),
                filterText, sourceFilter, phaseFilter);
    }

    private File resolveItsqPath() {
        String configPath = cfg.getProperty(ITSQ_PATH_KEY);
        if (!configPath.isEmpty()) {
            File file = new File(configPath);
            if (file.exists()) return file;
        }

        String cfgFilePath = cfg.getFilePath();
        if (cfgFilePath != null) {
            File configDir = new File(cfgFilePath).getParentFile();
            if (configDir != null) {
                File itsqDir = new File(configDir, DEFAULT_ITSQ_PATH);
                if (itsqDir.exists()) return itsqDir;
            }
        }

        File targetTestfaelle = new File("target/testfaelle");
        if (targetTestfaelle.exists()) return targetTestfaelle;

        return new File(DEFAULT_ITSQ_PATH);
    }

    // ===== Actions =====

    private void browseItsqPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("ITSQ-Verzeichnis waehlen");

        File currentPath = resolveItsqPath();
        if (currentPath.exists()) {
            chooser.setCurrentDirectory(currentPath.getParentFile());
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            cfg.setProperty(ITSQ_PATH_KEY, selected.getAbsolutePath());
            cfg.save();
            loadItsqDirectory();
        }
    }

    // ===== ViewInfo Implementation =====

    @Override
    public String getMenuLabel() {
        return "ITSQ Explorer (JFD)";
    }

    @Override
    public String getToolbarLabel() {
        return "ITSQ-Test-Set";
    }

    @Override
    public KeyStroke getKeyboardShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_DOWN_MASK);
    }

    @Override
    public Icon getIcon() {
        return new ImageIcon(getClass().getResource("/icons/folder_cubes.png"));
    }

    @Override
    public String getMenuGroup() {
        return "Verwaltung";
    }

    // ===== Accessors =====

    private ItsqTreeView getTreeView() {
        return mainPanel.getPanelItsqTree();
    }

    private ItsqViewTabView getViewTabView() {
        return mainPanel.getPanelItsqView();
    }

    public ItsqMainPanel getMainPanel() {
        return mainPanel;
    }

    // ===== Inner Classes =====

    private static class ItsqMainPanelWrapper extends BaseViewPanel {
        public ItsqMainPanelWrapper(ItsqMainPanel mainPanel) {
            super();
            viewToolbar.setVisible(false);
            getContentPanel().add(mainPanel, BorderLayout.CENTER);
        }
    }
}
