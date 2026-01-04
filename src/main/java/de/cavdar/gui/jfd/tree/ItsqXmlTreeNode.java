package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqXmlFile;

/**
 * Tree node for an XML file.
 */
public class ItsqXmlTreeNode extends ItsqTreeNode {

    public ItsqXmlTreeNode(ItsqXmlFile xmlFile) {
        super(xmlFile);
    }

    public ItsqXmlFile getItsqXmlFile() {
        return (ItsqXmlFile) getItsqItem();
    }
}
