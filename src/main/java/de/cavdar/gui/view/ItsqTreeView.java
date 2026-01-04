package de.cavdar.gui.view;

import de.cavdar.gui.design.BaseViewPanel;
import de.cavdar.gui.design.ItsqTreeViewPanel;
import de.cavdar.gui.model.AppConfig;

import static de.cavdar.gui.util.AppConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

/**
 * ITSQ Tree View - displays test files from the embedded ITSQ artifact.
 * Scans the ITSQ directory and builds a tree from the directory structure.
 * <p>
 * Structure:
 * ITSQ/
 * ├── ARCHIV-BESTAND/
 * │   ├── PHASE-1/
 * │   │   └── *.xml
 * │   └── PHASE-2/
 * │       └── *.xml
 * └── REF-EXPORTS/
 * ├── PHASE-1/
 * │   ├── c01/
 * │   │   ├── Relevanz_Negativ/
 * │   │   └── Relevanz_Positiv/
 * │   └── c02/
 * └── PHASE-2/
 * └── ...
 *
 * @author TemplateGUI
 * @version 1.0
 */
public class ItsqTreeView extends BaseView {
    private static final Logger LOG = LoggerFactory.getLogger(ItsqTreeView.class);

    private ItsqTreeViewPanel itsqPanel;
    private final AppConfig cfg = AppConfig.getInstance();

    // Statistics
    private int totalFiles = 0;
    private int totalDirs = 0;
    private long totalSize = 0;

    /**
     * Constructs a new ItsqTreeView.
     */
    public ItsqTreeView() {
        super("ITSQ Explorer");
        setSize(900, 700);

        // Load initial data
        SwingUtilities.invokeLater(this::loadItsqDirectory);

        LOG.debug("ItsqTreeView created");
    }

    @Override
    protected BaseViewPanel createPanel() {
        itsqPanel = new ItsqTreeViewPanel();
        return itsqPanel;
    }

    @Override
    protected void setupToolbarActions() {
        // Toolbar actions are set up in setupListeners
    }

    @Override
    protected void setupListeners() {
        // Browse button
        itsqPanel.getBrowseButton().addActionListener(e -> browseItsqPath());

        // Refresh button
        itsqPanel.getRefreshButton().addActionListener(e -> loadItsqDirectory());

        // Source filter
        itsqPanel.getSourceComboBox().addActionListener(e -> loadItsqDirectory());

        // Phase filter
        itsqPanel.getPhaseComboBox().addActionListener(e -> loadItsqDirectory());

        // Search
        itsqPanel.getSearchButton().addActionListener(e -> searchFiles());
        itsqPanel.getSearchField().addActionListener(e -> searchFiles());

        // Tree selection
        itsqPanel.getTree().addTreeSelectionListener(this::onTreeSelectionChanged);
    }

    // ===== ViewInfo Implementation =====

    @Override
    public String getMenuLabel() {
        return "ITSQ Explorer";
    }

    @Override
    public String getToolbarLabel() {
        return "JSON-Explorer";
    }

    @Override
    public KeyStroke getKeyboardShortcut() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK);
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

    /**
     * Loads the ITSQ directory and builds the tree.
     */
    private void loadItsqDirectory() {
        String itsqPath = itsqPanel.getItsqPathField().getText();
        File itsqDir = resolveItsqPath(itsqPath);

        if (!itsqDir.exists() || !itsqDir.isDirectory()) {
            LOG.warn("ITSQ directory not found: {}", itsqDir.getAbsolutePath());
            showError("ITSQ-Verzeichnis nicht gefunden: " + itsqDir.getAbsolutePath());
            return;
        }

        // Reset statistics
        totalFiles = 0;
        totalDirs = 0;
        totalSize = 0;

        // Get filter settings
        String sourceFilter = (String) itsqPanel.getSourceComboBox().getSelectedItem();
        String phaseFilter = (String) itsqPanel.getPhaseComboBox().getSelectedItem();

        // Build tree
        DefaultMutableTreeNode root = itsqPanel.getRootNode();
        root.removeAllChildren();
        root.setUserObject("ITSQ: " + itsqDir.getName());

        // Scan directory
        scanDirectory(itsqDir, root, sourceFilter, phaseFilter, 0);

        // Update tree
        itsqPanel.getTreeModel().reload();
        expandToLevel(itsqPanel.getTree(), 2);

        // Update statistics
        updateStatistics();

        LOG.info("Loaded ITSQ directory: {} ({} files, {} dirs)",
                itsqDir.getAbsolutePath(), totalFiles, totalDirs);
    }

    /**
     * Resolves the ITSQ path relative to the application directory.
     * Tries multiple locations in order:
     * 1. Absolute path (if provided)
     * 2. Relative to config file location
     * 3. Relative to JAR location (distribution)
     * 4. Relative to current working directory
     */
    private File resolveItsqPath(String path) {
        File file = new File(path);
        if (file.isAbsolute() && file.exists()) {
            LOG.debug("ITSQ path is absolute: {}", file);
            return file;
        }

        // Try relative to config file location
        String configPath = cfg.getFilePath();
        if (configPath != null) {
            File configFile = new File(configPath);
            File configDir = configFile.getParentFile();
            if (configDir != null) {
                File itsqDir = new File(configDir, path);
                if (itsqDir.exists()) {
                    LOG.debug("ITSQ found relative to config: {}", itsqDir);
                    return itsqDir;
                }
            }
        }

        // Try relative to JAR location (for distribution)
        try {
            String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            // On Windows, remove leading slash from /E:/...
            if (jarPath.matches("^/[A-Za-z]:.*")) {
                jarPath = jarPath.substring(1);
            }
            File jarFile = new File(jarPath);
            File jarDir = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();

            if (jarDir != null) {
                // If running from lib/, go up one level to distribution root
                if ("lib".equals(jarDir.getName())) {
                    jarDir = jarDir.getParentFile();
                }
                // If running from target/classes/, go up to find ITSQ in target/testfaelle or distribution
                if ("classes".equals(jarDir.getName())) {
                    File targetDir = jarDir.getParentFile();
                    // Check in unpacked distribution
                    File distDir = new File(targetDir, "TemplateGUI-1.0.0-SNAPSHOT");
                    if (distDir.exists()) {
                        File itsqDir = new File(distDir, path);
                        if (itsqDir.exists()) {
                            LOG.debug("ITSQ found in distribution: {}", itsqDir);
                            return itsqDir;
                        }
                    }
                    // Check in testfaelle (unpacked artifact)
                    File testfaelleDir = new File(targetDir, "testfaelle");
                    if (testfaelleDir.exists()) {
                        LOG.debug("ITSQ found in testfaelle: {}", testfaelleDir);
                        // Update the path field to show actual location
                        itsqPanel.getItsqPathField().setText(testfaelleDir.getAbsolutePath());
                        return testfaelleDir;
                    }
                }

                File itsqDir = new File(jarDir, path);
                if (itsqDir.exists()) {
                    LOG.debug("ITSQ found relative to JAR: {}", itsqDir);
                    return itsqDir;
                }
            }
        } catch (Exception e) {
            LOG.debug("Could not resolve JAR path: {}", e.getMessage());
        }

        // Try relative to current working directory
        File relative = new File(System.getProperty("user.dir"), path);
        if (relative.exists()) {
            LOG.debug("ITSQ found in working directory: {}", relative);
            return relative;
        }

        // Try target/testfaelle directly (for IDE runs)
        File targetTestfaelle = new File("target/testfaelle");
        if (targetTestfaelle.exists()) {
            LOG.debug("ITSQ found in target/testfaelle: {}", targetTestfaelle);
            itsqPanel.getItsqPathField().setText(targetTestfaelle.getAbsolutePath());
            return targetTestfaelle;
        }

        LOG.warn("ITSQ directory not found for path: {}", path);
        return relative;
    }

    /**
     * Recursively scans a directory and adds nodes to the tree.
     */
    private void scanDirectory(File dir, DefaultMutableTreeNode parentNode,
                               String sourceFilter, String phaseFilter, int depth) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }

        // Sort: directories first, then by name
        Arrays.sort(children, Comparator
                .comparing(File::isFile)
                .thenComparing(File::getName));

        for (File child : children) {
            // Apply source filter at depth 1 (ARCHIV-BESTAND, REF-EXPORTS)
            if (depth == 0 && !"Alle".equals(sourceFilter)) {
                if (!child.getName().equals(sourceFilter)) {
                    continue;
                }
            }

            // Apply phase filter at depth 2 (PHASE-1, PHASE-2)
            if (depth == 1 && !"Alle".equals(phaseFilter)) {
                if (!child.getName().equals(phaseFilter)) {
                    continue;
                }
            }

            // Create node with file info
            FileNode fileNode = new FileNode(child);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileNode);
            parentNode.add(node);

            if (child.isDirectory()) {
                totalDirs++;
                scanDirectory(child, node, sourceFilter, phaseFilter, depth + 1);
            } else {
                totalFiles++;
                totalSize += child.length();
            }
        }
    }

    /**
     * Expands tree to specified level.
     */
    private void expandToLevel(JTree tree, int level) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        expandNode(tree, root, 0, level);
    }

    private void expandNode(JTree tree, DefaultMutableTreeNode node, int currentLevel, int maxLevel) {
        if (currentLevel >= maxLevel) {
            return;
        }

        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            expandNode(tree, child, currentLevel + 1, maxLevel);
        }
    }

    // ===== Event Handlers =====

    /**
     * Handles tree selection changes.
     */
    private void onTreeSelectionChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                itsqPanel.getTree().getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        Object userObject = node.getUserObject();
        if (userObject instanceof FileNode fileNode) {
            showFileDetails(fileNode);
        } else {
            // Root node
            itsqPanel.getDetailsArea().setText("ITSQ Testfaelle\n\nWaehlen Sie einen Eintrag aus.");
            itsqPanel.getFileContentArea().setText("");
        }
    }

    /**
     * Shows details for the selected file/directory.
     */
    private void showFileDetails(FileNode fileNode) {
        File file = fileNode.getFile();
        StringBuilder sb = new StringBuilder();

        sb.append("Name: ").append(file.getName()).append("\n");
        sb.append("Pfad: ").append(file.getAbsolutePath()).append("\n");
        sb.append("Typ: ").append(file.isDirectory() ? "Verzeichnis" : "Datei").append("\n");

        if (file.isFile()) {
            sb.append("Groesse: ").append(formatSize(file.length())).append("\n");
            sb.append("Zuletzt geaendert: ").append(new java.util.Date(file.lastModified())).append("\n");

            // Show file content for small files
            if (file.length() < 100_000) {
                loadFileContent(file);
            } else {
                itsqPanel.getFileContentArea().setText("Datei zu gross fuer Vorschau (" +
                        formatSize(file.length()) + ")");
            }
        } else {
            // Directory - count children
            File[] children = file.listFiles();
            int dirs = 0, files = 0;
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) dirs++;
                    else files++;
                }
            }
            sb.append("Unterverzeichnisse: ").append(dirs).append("\n");
            sb.append("Dateien: ").append(files).append("\n");
            itsqPanel.getFileContentArea().setText("");
        }

        itsqPanel.getDetailsArea().setText(sb.toString());
    }

    /**
     * Loads and displays file content.
     */
    private void loadFileContent(File file) {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            itsqPanel.getFileContentArea().setText(content);
            itsqPanel.getFileContentArea().setCaretPosition(0);
        } catch (IOException e) {
            LOG.error("Failed to read file: {}", file, e);
            itsqPanel.getFileContentArea().setText("Fehler beim Lesen: " + e.getMessage());
        }
    }

    /**
     * Browses for ITSQ directory.
     */
    private void browseItsqPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("ITSQ-Verzeichnis waehlen");

        // Start in current ITSQ path
        File currentPath = resolveItsqPath(itsqPanel.getItsqPathField().getText());
        if (currentPath.exists()) {
            chooser.setCurrentDirectory(currentPath.getParentFile());
        }

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            itsqPanel.getItsqPathField().setText(selected.getAbsolutePath());
            loadItsqDirectory();
        }
    }

    /**
     * Searches for files matching the search text.
     */
    private void searchFiles() {
        String searchText = itsqPanel.getSearchField().getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            return;
        }

        DefaultMutableTreeNode root = itsqPanel.getRootNode();
        DefaultMutableTreeNode found = findNode(root, searchText);

        if (found != null) {
            TreePath path = new TreePath(found.getPath());
            itsqPanel.getTree().setSelectionPath(path);
            itsqPanel.getTree().scrollPathToVisible(path);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Keine Datei gefunden: " + searchText,
                    "Suche", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Finds a node matching the search text.
     */
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode node, String searchText) {
        Object userObject = node.getUserObject();
        if (userObject instanceof FileNode fileNode) {
            if (fileNode.getFile().getName().toLowerCase().contains(searchText)) {
                return node;
            }
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode found = findNode(child, searchText);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    /**
     * Updates the statistics labels.
     */
    private void updateStatistics() {
        itsqPanel.getTotalDirsLabel().setText(String.valueOf(totalDirs));
        itsqPanel.getTotalFilesLabel().setText(String.valueOf(totalFiles));
        itsqPanel.getTotalSizeLabel().setText(formatSize(totalSize));
    }

    /**
     * Formats file size in human-readable format.
     */
    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        }
        DecimalFormat df = new DecimalFormat("#.##");
        if (size < 1024 * 1024) {
            return df.format(size / 1024.0) + " KB";
        }
        if (size < 1024 * 1024 * 1024) {
            return df.format(size / (1024.0 * 1024.0)) + " MB";
        }
        return df.format(size / (1024.0 * 1024.0 * 1024.0)) + " GB";
    }

    /**
     * Shows an error message.
     */
    private void showError(String message) {
        itsqPanel.getDetailsArea().setText("Fehler: " + message);
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    // ===== Getters =====

    public ItsqTreeViewPanel getItsqPanel() {
        return itsqPanel;
    }

    // ===== Inner Class =====

    /**
     * Wrapper class for File to provide custom toString().
     */
    private static class FileNode {
        private final File file;

        public FileNode(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            // Show icon prefix based on type
            if (file.isDirectory()) {
                return file.getName();
            } else {
                // Show file with size
                String name = file.getName();
                if (file.length() > 0) {
                    return name + " (" + formatSize(file.length()) + ")";
                }
                return name;
            }
        }

        private String formatSize(long size) {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return (size / 1024) + " KB";
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
}
