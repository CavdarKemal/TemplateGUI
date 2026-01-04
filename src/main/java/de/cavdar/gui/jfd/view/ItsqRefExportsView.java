package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqRefExportsPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqRefExportsView extends ItsqRefExportsPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqRefExportsView() {
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
