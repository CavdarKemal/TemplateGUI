package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqXmlPanel;
import de.cavdar.gui.jfd.model.ItsqItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View for displaying XML files.
 * Implements ItsqItemSelectable to receive the selected item.
 */
public class ItsqXmlView extends ItsqXmlPanel implements ItsqItemSelectable {
    private static final Logger LOG = LoggerFactory.getLogger(ItsqXmlView.class);

    private ItsqItem selectedItem;

    public ItsqXmlView() {
        super();
    }

    @Override
    public void setSelectedItem(ItsqItem item) {
        this.selectedItem = item;
        updateView();
    }

    private void updateView() {
        if (selectedItem != null && selectedItem.getFile() != null) {
            LOG.debug("XML View: Selected file: {}", selectedItem.getFile().getAbsolutePath());
            // TODO: Load and display XML content
        }
    }

    public ItsqItem getSelectedItem() {
        return selectedItem;
    }
}
