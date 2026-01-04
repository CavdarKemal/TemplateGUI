package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqArchivBestandPhase;

/**
 * Tree node for a PHASE-x directory under ARCHIV-BESTAND.
 */
public class ItsqArchivBestandPhaseTreeNode extends ItsqTreeNode {

    public ItsqArchivBestandPhaseTreeNode(ItsqArchivBestandPhase phase) {
        super(phase);
    }

    public ItsqArchivBestandPhase getItsqArchivBestandPhase() {
        return (ItsqArchivBestandPhase) getItsqItem();
    }
}
