package de.cavdar.gui.itsq.view;

import de.cavdar.gui.design.base.BaseViewPanel;
import de.cavdar.gui.itsq.design.ItsqMainPanel;
import de.cavdar.gui.itsq.model.ItsqItem;
import de.cavdar.gui.itsq.tree.*;
import de.cavdar.gui.model.base.AppConfig;
import de.cavdar.gui.view.base.BaseView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import static de.cavdar.gui.util.AppConstants.*;

/**
 * ITSQ Explorer View using JFormDesigner GUI.
 * Displays ITSQ test files with CardLayout-based detail views.
 *
 * Uses typed tree nodes for type-safe tree handling:
 * - ItsqRootTreeNode -> ItsqRootView
 * - ItsqArchivBestandTreeNode -> ItsqArchivBestandView
 * - ItsqArchivBestandPhaseTreeNode -> ItsqArchibBestandPhaseView
 * - ItsqRefExportsTreeNode -> ItsqRefExportsView
 * - ItsqRefExportsPhaseTreeNode -> ItsqRefExportsPhaseView
 * - ItsqCustomerTreeNode -> ItsqCustomerView
 * - ItsqScenarioTreeNode -> ItsqScenarioView
 * - ItsqXmlTreeNode -> ItsqEditorView (auch fuer .xml, Options.cfg, .properties)
 * - ItsqOptionsTreeNode -> ItsqEditorView
 * - ItsqPropertiesTreeNode -> ItsqEditorView
 *
 * @author TemplateGUI
 * @version 3.1
 */
public class ItsqExplorerView extends BaseView {
    private static final Logger LOG = LoggerFactory.getLogger(ItsqExplorerView.class);

    // Card names (must match ItsqViewTabPanel)
    private static final String CARD_ROOT = "cardItsqRoot";
    private static final String CARD_ARCHIV_BESTAND = "cardArchivBestand";
    private static final String CARD_ARCHIV_BESTAND_PHASE = "cardArchivBestandPhase";
    private static final String CARD_REF_EXPORTS = "cardRefExports";
    private static final String CARD_REF_EXPORTS_PHASE = "cardRefExportsPhase";
    private static final String CARD_SCENARIO = "cardScenario";
    private static final String CARD_CUSTOMER = "cardCustomer";
    private static final String CARD_XML = "cardXml";

    private static final String TESTSET_HISTORY_KEY = "itsq.testset.history";
    private static final int MAX_HISTORY = 20;

    private ItsqMainPanel mainPanel;
    private final AppConfig cfg = AppConfig.getInstance();
    private ItsqTreeModel treeModel;
    private DefaultComboBoxModel<String> testSetHistoryModel;
    private boolean updatingComboBox = false;
    private boolean initialLoadDone = false;
    private String lastValidSelection = null;

    public ItsqExplorerView() {
        super("ITSQ-Test-Sets Verwalten");
        setSize(1000, 700);

        // Initialize empty tree model
        treeModel = new ItsqTreeModel(null);
        getTree().setModel(treeModel);

        // Initialize TestSet ComboBox with history (no loading yet)
        initTestSetComboBox();

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
        // Wrap ItsqMainPanel in BaseViewPanel
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

        // Filter ComboBox -> apply filter on tree
        mainPanel.getComboBoxFilter().addActionListener(e -> applyFilter());
        JTextField filterEditor = (JTextField) mainPanel.getComboBoxFilter().getEditor().getEditorComponent();
        filterEditor.addActionListener(e -> applyFilter());

        // Active Only checkbox -> apply filter on tree
        mainPanel.getCheckBoxActiveOnly().addActionListener(e -> applyFilter());

        // Source filter (Quelle) -> apply filter on tree
        mainPanel.getComboBoxTestSetSource().addActionListener(e -> applyFilter());

        // Phase filter -> apply filter on tree
        mainPanel.getComboBoxPhase().addActionListener(e -> applyFilter());

        // Tree selection -> switch card
        getTree().addTreeSelectionListener(this::onTreeSelectionChanged);
    }

    /**
     * Initializes the TestSet ComboBox with history model.
     */
    @SuppressWarnings("unchecked")
    private void initTestSetComboBox() {
        testSetHistoryModel = new DefaultComboBoxModel<>();
        JComboBox<String> comboBox = mainPanel.getComboBoxTestSet();
        comboBox.setModel(testSetHistoryModel);
        comboBox.setEditable(true);

        // Load history from config (suppress ActionListener)
        updatingComboBox = true;
        try {
            loadTestSetHistory();
        } finally {
            updatingComboBox = false;
        }

        // Handle Enter in the editor
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.addActionListener(e -> {
            String path = editor.getText().trim();
            if (!path.isEmpty()) {
                loadTestSetPath(path);
            }
        });
    }

    /**
     * Loads TestSet history from config.
     */
    private void loadTestSetHistory() {
        String history = cfg.getProperty(TESTSET_HISTORY_KEY);
        if (history != null && !history.isEmpty()) {
            String[] paths = history.split("\\|");
            for (String path : paths) {
                if (!path.isEmpty()) {
                    testSetHistoryModel.addElement(path);
                }
            }
        }
    }

    /**
     * Saves TestSet history to config.
     */
    private void saveTestSetHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testSetHistoryModel.getSize(); i++) {
            if (i > 0) sb.append("|");
            sb.append(testSetHistoryModel.getElementAt(i));
        }
        cfg.setProperty(TESTSET_HISTORY_KEY, sb.toString());
        cfg.save();
    }

    /**
     * Adds a path to the TestSet history (at top, no duplicates).
     */
    private void addToTestSetHistory(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }

        updatingComboBox = true;
        try {
            // Remove if already exists
            testSetHistoryModel.removeElement(path);

            // Add at beginning
            testSetHistoryModel.insertElementAt(path, 0);

            // Limit size
            while (testSetHistoryModel.getSize() > MAX_HISTORY) {
                testSetHistoryModel.removeElementAt(testSetHistoryModel.getSize() - 1);
            }

            // Select the new item
            testSetHistoryModel.setSelectedItem(path);

            // Save to config
            saveTestSetHistory();
        } finally {
            updatingComboBox = false;
        }
    }

    /**
     * Called when TestSet ComboBox selection changes.
     */
    private void onTestSetSelectionChanged() {
        if (updatingComboBox) {
            return;
        }

        Object selected = mainPanel.getComboBoxTestSet().getSelectedItem();
        if (selected != null) {
            String path = selected.toString().trim();
            if (!path.isEmpty()) {
                loadTestSetPath(path);
            }
        }
    }

    /**
     * Loads the specified TestSet path.
     */
    private void loadTestSetPath(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            // Update config
            cfg.setProperty(ITSQ_PATH_KEY, path);
            cfg.save();

            // Save as last valid selection
            lastValidSelection = path;

            // Reload
            loadItsqDirectory();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Verzeichnis nicht gefunden: " + path,
                    "Fehler", JOptionPane.ERROR_MESSAGE);

            // Remove invalid entry from ComboBox
            updatingComboBox = true;
            try {
                testSetHistoryModel.removeElement(path);
                saveTestSetHistory();

                // Restore last valid selection
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

    // ===== Directory Loading =====

    private void loadItsqDirectory() {
        File itsqDir = resolveItsqPath();

        if (!itsqDir.exists() || !itsqDir.isDirectory()) {
            LOG.warn("ITSQ directory not found: {}", itsqDir.getAbsolutePath());
            JOptionPane.showMessageDialog(this,
                    "ITSQ-Verzeichnis nicht gefunden: " + itsqDir.getAbsolutePath(),
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get filter settings
        String filterText = getFilterText();
        boolean activeOnly = mainPanel.getCheckBoxActiveOnly().isSelected();
        String sourceFilter = (String) mainPanel.getComboBoxTestSetSource().getSelectedItem();
        String phaseFilter = (String) mainPanel.getComboBoxPhase().getSelectedItem();

        // Reload tree model with all filters
        treeModel.reload(itsqDir, filterText, activeOnly, sourceFilter, phaseFilter);

        // Expand tree to level 2
        expandToLevel(getTree(), 2);

        // Show root card
        showCard(CARD_ROOT, null);

        // Add to history and save as last valid selection
        String path = itsqDir.getAbsolutePath();
        addToTestSetHistory(path);
        lastValidSelection = path;

        // Add filter to history if not empty
        addToFilterHistory(filterText);

        LOG.info("Loaded ITSQ directory: {} ({} files, {} dirs, filter: '{}', source: '{}', phase: '{}', activeOnly: {})",
                path, treeModel.getTotalFiles(), treeModel.getTotalDirs(), filterText, sourceFilter, phaseFilter, activeOnly);
    }

    /**
     * Gets the current filter text from the filter ComboBox.
     */
    private String getFilterText() {
        Object selected = mainPanel.getComboBoxFilter().getSelectedItem();
        return selected != null ? selected.toString().trim() : "";
    }

    /**
     * Applies the current filter settings to reload the tree.
     */
    private void applyFilter() {
        if (updatingComboBox || !initialLoadDone) {
            return;
        }
        loadItsqDirectory();
    }

    /**
     * Adds a filter text to the filter history (if not empty and not already present).
     */
    @SuppressWarnings("unchecked")
    private void addToFilterHistory(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return;
        }

        JComboBox<String> filterCombo = mainPanel.getComboBoxFilter();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) filterCombo.getModel();

        // Check if already exists
        for (int i = 0; i < model.getSize(); i++) {
            if (filterText.equals(model.getElementAt(i))) {
                return;
            }
        }

        // Add to model (limit to 20 entries)
        model.insertElementAt(filterText, 0);
        if (model.getSize() > 20) {
            model.removeElementAt(model.getSize() - 1);
        }
    }

    private File resolveItsqPath() {
        // Try config value
        String configPath = cfg.getProperty(ITSQ_PATH_KEY);
        if (!configPath.isEmpty()) {
            File file = new File(configPath);
            if (file.exists()) {
                return file;
            }
        }

        // Try relative to config file
        String cfgFilePath = cfg.getFilePath();
        if (cfgFilePath != null) {
            File configDir = new File(cfgFilePath).getParentFile();
            if (configDir != null) {
                File itsqDir = new File(configDir, DEFAULT_ITSQ_PATH);
                if (itsqDir.exists()) {
                    return itsqDir;
                }
            }
        }

        // Try target/testfaelle (IDE development)
        File targetTestfaelle = new File("target/testfaelle");
        if (targetTestfaelle.exists()) {
            return targetTestfaelle;
        }

        // Default
        return new File(DEFAULT_ITSQ_PATH);
    }

    private void expandToLevel(JTree tree, int level) {
        Object root = treeModel.getRoot();
        if (root instanceof ItsqTreeNode rootNode) {
            expandNode(tree, rootNode, 0, level);
        }
    }

    private void expandNode(JTree tree, ItsqTreeNode node, int currentLevel, int maxLevel) {
        if (currentLevel >= maxLevel) {
            return;
        }
        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);

        for (int i = 0; i < node.getChildCount(); i++) {
            if (node.getChildAt(i) instanceof ItsqTreeNode child) {
                expandNode(tree, child, currentLevel + 1, maxLevel);
            }
        }
    }

    // ===== Tree Selection -> Card Switching =====

    private void onTreeSelectionChanged(TreeSelectionEvent e) {
        Object selectedComponent = getTree().getLastSelectedPathComponent();
        if (!(selectedComponent instanceof ItsqTreeNode node)) {
            return;
        }

        // Determine card based on node type
        String cardName = determineCardForNode(node);

        // Get the ItsqItem from the node
        ItsqItem item = node.getItsqItem();

        // Show card and pass selected item to view
        showCard(cardName, item);

        LOG.debug("Tree selection: {} -> Card: {}",
                item != null ? item.getName() : "null", cardName);
    }

    /**
     * Determines which card to show based on the node type.
     * Uses instanceof checks for type-safe dispatch.
     */
    private String determineCardForNode(ItsqTreeNode node) {
        if (node instanceof ItsqRootTreeNode) {
            return CARD_ROOT;
        } else if (node instanceof ItsqArchivBestandTreeNode) {
            return CARD_ARCHIV_BESTAND;
        } else if (node instanceof ItsqArchivBestandPhaseTreeNode) {
            return CARD_ARCHIV_BESTAND_PHASE;
        } else if (node instanceof ItsqRefExportsTreeNode) {
            return CARD_REF_EXPORTS;
        } else if (node instanceof ItsqRefExportsPhaseTreeNode) {
            return CARD_REF_EXPORTS_PHASE;
        } else if (node instanceof ItsqCustomerTreeNode) {
            return CARD_CUSTOMER;
        } else if (node instanceof ItsqScenarioTreeNode) {
            return CARD_SCENARIO;
        } else if (node instanceof ItsqXmlTreeNode
                || node instanceof ItsqOptionsTreeNode
                || node instanceof ItsqPropertiesTreeNode) {
            return CARD_XML;
        }
        return CARD_ROOT;
    }

    /**
     * Shows the specified card and passes the selected item to the view.
     *
     * @param cardName the card to show
     * @param item the selected ItsqItem (may be null)
     */
    private void showCard(String cardName, ItsqItem item) {
        ItsqViewTabView viewTabPanel = getViewTabPanel();
        if (viewTabPanel == null) {
            return;
        }

        // Switch card
        CardLayout cardLayout = (CardLayout) viewTabPanel.getLayout();
        cardLayout.show(viewTabPanel, cardName);

        // Pass selected item to the view
        JPanel panel = getViewPanelForCard(cardName, viewTabPanel);
        if (panel instanceof ItsqItemSelectable selectable) {
            selectable.setSelectedItem(item);
        }
    }

    /**
     * Gets the view panel for the given card name.
     */
    private JPanel getViewPanelForCard(String cardName, ItsqViewTabView viewTabPanel) {
        return switch (cardName) {
            case CARD_ROOT -> viewTabPanel.getPanelRoot();
            case CARD_ARCHIV_BESTAND -> viewTabPanel.getPanelArchivBestand();
            case CARD_ARCHIV_BESTAND_PHASE -> viewTabPanel.getPanelArchivBestandPhase();
            case CARD_REF_EXPORTS -> viewTabPanel.getPanelRefExports();
            case CARD_REF_EXPORTS_PHASE -> viewTabPanel.getPanelRefExportsPhase();
            case CARD_CUSTOMER -> viewTabPanel.getPanelCustomer();
            case CARD_SCENARIO -> viewTabPanel.getPanelScenario();
            case CARD_XML -> viewTabPanel.getPanelEditor();
            default -> null;
        };
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

    // ===== Accessors =====

    private JTree getTree() {
        return mainPanel.getPanelItsqTree().getTreeItsq();
    }

    private ItsqViewTabView getViewTabPanel() {
        return mainPanel.getPanelItsqView();
    }

    public ItsqMainPanel getMainPanel() {
        return mainPanel;
    }

    public ItsqTreeModel getTreeModel() {
        return treeModel;
    }

    // ===== Inner Classes =====

    /**
     * Wrapper to make ItsqMainPanel compatible with BaseView.
     */
    private static class ItsqMainPanelWrapper extends BaseViewPanel {

        public ItsqMainPanelWrapper(ItsqMainPanel mainPanel) {
            super();
            // Hide the default toolbar since ItsqMainPanel has its own controls
            viewToolbar.setVisible(false);
            // Add mainPanel to the content area
            getContentPanel().add(mainPanel, BorderLayout.CENTER);
        }
    }
}
