package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqRefExports;

/**
 * Tree node for the REF-EXPORTS directory.
 */
public class ItsqRefExportsTreeNode extends ItsqTreeNode {

    public ItsqRefExportsTreeNode(ItsqRefExports refExports) {
        super(refExports);
    }

    public ItsqRefExports getItsqRefExports() {
        return (ItsqRefExports) getItsqItem();
    }
}
