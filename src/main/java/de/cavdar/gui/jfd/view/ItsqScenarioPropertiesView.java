package de.cavdar.gui.jfd.view;

import de.cavdar.gui.jfd.design.ItsqScenarioPropertiesPanel;
import de.cavdar.gui.jfd.model.ItsqItem;

public class ItsqScenarioPropertiesView extends ItsqScenarioPropertiesPanel implements ItsqItemSelectable {
    private ItsqItem selectedItem;

    public ItsqScenarioPropertiesView() {
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
