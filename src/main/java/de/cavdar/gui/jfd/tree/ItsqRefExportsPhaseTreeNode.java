package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqRefExportsPhase;

/**
 * Tree node for a PHASE-x directory under REF-EXPORTS.
 */
public class ItsqRefExportsPhaseTreeNode extends ItsqTreeNode {

    public ItsqRefExportsPhaseTreeNode(ItsqRefExportsPhase phase) {
        super(phase);
    }

    public ItsqRefExportsPhase getItsqRefExportsPhase() {
        return (ItsqRefExportsPhase) getItsqItem();
    }
}
