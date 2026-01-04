package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqScenarioPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqScenarioView extends ItsqScenarioPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqScenarioView() {
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
