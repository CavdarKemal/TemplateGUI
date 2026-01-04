package de.cavdar.gui.design;

import de.cavdar.gui.util.IconLoader;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * GUI panel for ItsqTreeView - displays ITSQ test files from embedded artifact.
 * Contains only layout and components, no listeners or business logic.
 * <p>
 * Extends BaseViewPanel with split pane layout containing tree and tabbed pane.
 *
 * @author TemplateGUI
 * @version 2.0
 */
public class ItsqTreeViewPanel extends BaseViewPanel {

    // Tree components
    protected DefaultMutableTreeNode rootNode;
    protected DefaultTreeModel treeModel;
    protected JTree tree;
    protected JScrollPane treeScrollPane;

    // Split pane layout
    protected JSplitPane splitPane;
    protected JPanel leftPanel;
    protected JToolBar leftToolbar;
    protected JToolBar rightToolbar;
    protected JTabbedPane tabbedPane;

    // Toolbar container
    protected JPanel toolbarContainer;

    // Toolbar components - Row 1
    protected JLabel lblItsqPath;
    protected JTextField txtItsqPath;
    protected JButton btnBrowse;

    // Row 2 components
    protected JLabel lblSource;
    protected JComboBox<String> cbSource;
    protected JLabel lblPhase;
    protected JComboBox<String> cbPhase;
    protected JButton btnRefresh;

    // Right toolbar components
    protected JLabel lblSearch;
    protected JTextField txtSearch;
    protected JButton btnSearch;

    // Tab components - Details
    protected JTextArea detailsArea;
    protected JScrollPane detailsScrollPane;

    // Tab components - File Content
    protected JTextArea fileContentArea;
    protected JScrollPane fileContentScrollPane;

    // Tab components - Statistics
    protected JPanel statsPanel;
    protected JLabel lblTotalFiles;
    protected JLabel lblTotalDirs;
    protected JLabel lblTotalSize;

    // Status labels
    protected JLabel lblStatus;

    public ItsqTreeViewPanel() {
        super();
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        initTreeComponents();
        initItsqComponents();
    }

    /**
     * Initializes tree and split pane components.
     */
    protected void initTreeComponents() {
        // Create root node and tree model
        rootNode = new DefaultMutableTreeNode("ITSQ");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setShowsRootHandles(true);
        treeScrollPane = new JScrollPane(tree);

        // Create left panel with toolbar and tree
        leftPanel = new JPanel(new BorderLayout());
        leftToolbar = new JToolBar();
        leftToolbar.setFloatable(false);
        leftPanel.add(leftToolbar, BorderLayout.NORTH);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        // Create right panel with toolbar and tabbed pane
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightToolbar = new JToolBar();
        rightToolbar.setFloatable(false);
        rightPanel.add(rightToolbar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        rightPanel.add(tabbedPane, BorderLayout.CENTER);

        // Create split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        // Add to content panel
        contentPanel.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Initializes ITSQ-specific components.
     */
    protected void initItsqComponents() {
        setupLeftToolbarComponents();
        setupRightToolbarComponents();
        setupTabComponents();
    }

    private void setupLeftToolbarComponents() {
        toolbarContainer = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Row 0: ITSQ Path ===
        gbc.gridx = 0;
        gbc.gridy = 0;
        lblItsqPath = new JLabel("ITSQ Pfad:");
        toolbarContainer.add(lblItsqPath, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtItsqPath = new JTextField("ITSQ");
        txtItsqPath.setEditable(false);
        txtItsqPath.setToolTipText("Pfad zum ITSQ-Verzeichnis relativ zur Distribution");
        toolbarContainer.add(txtItsqPath, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        btnBrowse = new JButton("...");
        btnBrowse.setToolTipText("ITSQ-Verzeichnis waehlen");
        toolbarContainer.add(btnBrowse, gbc);

        // === Row 1: Source and Phase selection ===
        gbc.gridy = 1;
        gbc.weightx = 0;

        gbc.gridx = 0;
        lblSource = new JLabel("Quelle:");
        toolbarContainer.add(lblSource, gbc);

        gbc.gridx = 1;
        cbSource = new JComboBox<>(new String[]{"Alle", "ARCHIV-BESTAND", "REF-EXPORTS"});
        cbSource.setToolTipText("Quelle der Testfaelle");
        toolbarContainer.add(cbSource, gbc);

        gbc.gridx = 2;
        btnRefresh = new JButton("", IconLoader.load("refresh.png"));
        btnRefresh.setToolTipText("Verzeichnis neu laden");
        toolbarContainer.add(btnRefresh, gbc);

        // === Row 2: Phase filter ===
        gbc.gridy = 2;

        gbc.gridx = 0;
        lblPhase = new JLabel("Phase:");
        toolbarContainer.add(lblPhase, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbPhase = new JComboBox<>(new String[]{"Alle", "PHASE-1", "PHASE-2"});
        cbPhase.setToolTipText("Phase filtern");
        toolbarContainer.add(cbPhase, gbc);

        // Replace the leftToolbar with our container
        leftPanel.remove(leftToolbar);
        leftPanel.add(toolbarContainer, BorderLayout.NORTH);
    }

    private void setupRightToolbarComponents() {
        lblSearch = new JLabel("Suche:");
        rightToolbar.add(lblSearch);

        txtSearch = new JTextField(15);
        txtSearch.setMaximumSize(new Dimension(150, 25));
        txtSearch.setToolTipText("Nach Dateiname suchen");
        rightToolbar.add(txtSearch);

        btnSearch = new JButton("Suchen", IconLoader.load("folder_view.png"));
        rightToolbar.add(btnSearch);
    }

    private void setupTabComponents() {
        // Details tab
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        detailsScrollPane = new JScrollPane(detailsArea);
        tabbedPane.addTab("Details", detailsScrollPane);

        // File Content tab
        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);
        fileContentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        fileContentScrollPane = new JScrollPane(fileContentArea);
        tabbedPane.addTab("Datei-Inhalt", fileContentScrollPane);

        // Statistics tab
        statsPanel = createStatsPanel();
        tabbedPane.addTab("Statistik", statsPanel);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Total directories
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Verzeichnisse:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lblTotalDirs = new JLabel("0");
        lblTotalDirs.setFont(lblTotalDirs.getFont().deriveFont(Font.BOLD));
        panel.add(lblTotalDirs, gbc);

        // Total files
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Dateien:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lblTotalFiles = new JLabel("0");
        lblTotalFiles.setFont(lblTotalFiles.getFont().deriveFont(Font.BOLD));
        panel.add(lblTotalFiles, gbc);

        // Total size
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel("Gesamtgroesse:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        lblTotalSize = new JLabel("0 KB");
        lblTotalSize.setFont(lblTotalSize.getFont().deriveFont(Font.BOLD));
        panel.add(lblTotalSize, gbc);

        // Spacer
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JPanel(), gbc);

        return panel;
    }

    // ===== Tree Getters =====

    public JTree getTree() {
        return tree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JToolBar getLeftToolbar() {
        return leftToolbar;
    }

    public JToolBar getRightToolbar() {
        return rightToolbar;
    }

    // ===== ITSQ-specific Getters =====

    public JTextField getItsqPathField() {
        return txtItsqPath;
    }

    public JButton getBrowseButton() {
        return btnBrowse;
    }

    public JComboBox<String> getSourceComboBox() {
        return cbSource;
    }

    public JComboBox<String> getPhaseComboBox() {
        return cbPhase;
    }

    public JButton getRefreshButton() {
        return btnRefresh;
    }

    public JTextField getSearchField() {
        return txtSearch;
    }

    public JButton getSearchButton() {
        return btnSearch;
    }

    public JTextArea getDetailsArea() {
        return detailsArea;
    }

    public JTextArea getFileContentArea() {
        return fileContentArea;
    }

    public JLabel getTotalFilesLabel() {
        return lblTotalFiles;
    }

    public JLabel getTotalDirsLabel() {
        return lblTotalDirs;
    }

    public JLabel getTotalSizeLabel() {
        return lblTotalSize;
    }
}
