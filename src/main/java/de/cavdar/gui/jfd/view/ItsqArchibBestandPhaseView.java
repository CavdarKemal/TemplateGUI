package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqArchivBestandPhasePanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqArchibBestandPhaseView extends ItsqArchivBestandPhasePanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqArchibBestandPhaseView() {
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
