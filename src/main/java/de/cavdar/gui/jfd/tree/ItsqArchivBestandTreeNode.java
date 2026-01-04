package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqArchivBestand;

/**
 * Tree node for the ARCHIV-BESTAND directory.
 */
public class ItsqArchivBestandTreeNode extends ItsqTreeNode {

    public ItsqArchivBestandTreeNode(ItsqArchivBestand archivBestand) {
        super(archivBestand);
    }

    public ItsqArchivBestand getItsqArchivBestand() {
        return (ItsqArchivBestand) getItsqItem();
    }
}
