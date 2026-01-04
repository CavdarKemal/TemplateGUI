package de.cavdar.gui.itsq.view;

import de.cavdar.gui.itsq.design.ItsqRefExportsPhasePanel;
import de.cavdar.gui.itsq.model.ItsqItem;

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
