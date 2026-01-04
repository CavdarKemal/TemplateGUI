package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqCustomerPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqCustomerView extends ItsqCustomerPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqCustomerView() {
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
