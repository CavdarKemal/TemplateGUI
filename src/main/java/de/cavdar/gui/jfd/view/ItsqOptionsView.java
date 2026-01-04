package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqOptionsPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqOptionsView extends ItsqOptionsPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqOptionsView() {
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
