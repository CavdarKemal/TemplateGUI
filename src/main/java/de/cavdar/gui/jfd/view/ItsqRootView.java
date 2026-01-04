package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqRootPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqRootView extends ItsqRootPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqRootView() {
        super();
    }

    @Override
    public void setSelectedItem(ItsqItem item) {
        this.selectedItem = item;
    }

    public ItsqItem getSelectedItem() {
        return selectedItem;
    }
}
