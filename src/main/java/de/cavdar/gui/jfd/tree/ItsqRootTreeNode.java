package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqRoot;

/**
 * Tree node for the ITSQ root directory.
 */
public class ItsqRootTreeNode extends ItsqTreeNode {

    public ItsqRootTreeNode(ItsqRoot root) {
        super(root);
    }

    public ItsqRoot getItsqRoot() {
        return (ItsqRoot) getItsqItem();
    }
}
