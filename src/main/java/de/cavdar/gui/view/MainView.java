package de.cavdar.gui.view;

import de.cavdar.gui.design.MainFrame;
import de.cavdar.gui.jfd.view.ItsqExplorerView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Application entry point and main view controller.
 * Contains the main() method to start the MDI application.
 *
 * @author TemplateGUI
 * @version 1.0
 */
public class MainView {
    private static final Logger LOG = LoggerFactory.getLogger(MainView.class);

    /**
     * Application entry point.
     *
     * @param args command line arguments: [config-file-path]
     */
    public static void main(String[] args) {
        // Set config file path from command line argument (must be before AppConfig is loaded)
        if (args.length > 0 && args[0] != null && !args[0].isEmpty()) {
            System.setProperty("config.file", args[0]);
            LOG.info("Config file set from argument: {}", args[0]);
        }

        LOG.info("Starting MDI Application");
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();

            // Register views dynamically
            frame.registerView(SampleView::new);
            frame.registerView(ProzessView::new);
            frame.registerView(AnalyseView::new);
            frame.registerView(TreeView::new);
            frame.registerView(CustomerTreeView::new);
            frame.registerView(EditorView::new);
            frame.registerView(ItsqTreeView::new);
            frame.registerView(ItsqExplorerView::new);

            frame.setVisible(true);
        });
    }
}
