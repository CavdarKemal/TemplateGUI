package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqOptionsFile;

/**
 * Tree node for an Options.cfg file.
 */
public class ItsqOptionsTreeNode extends ItsqTreeNode {

    public ItsqOptionsTreeNode(ItsqOptionsFile optionsFile) {
        super(optionsFile);
    }

    public ItsqOptionsFile getItsqOptionsFile() {
        return (ItsqOptionsFile) getItsqItem();
    }
}
