package de.cavdar.gui.itsq.view;

import de.cavdar.gui.itsq.design.ItsqCustomerPanel;
import de.cavdar.gui.itsq.model.ItsqItem;

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
