package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqRefExportsPhasePanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqRefExportsPhaseView extends ItsqRefExportsPhasePanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqRefExportsPhaseView() {
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
