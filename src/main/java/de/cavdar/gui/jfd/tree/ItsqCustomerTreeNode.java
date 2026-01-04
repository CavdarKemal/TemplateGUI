package de.cavdar.gui.jfd.tree;

import de.cavdar.gui.jfd.model.ItsqCustomer;

/**
 * Tree node for a customer directory (c0x).
 */
public class ItsqCustomerTreeNode extends ItsqTreeNode {

    public ItsqCustomerTreeNode(ItsqCustomer customer) {
        super(customer);
    }

    public ItsqCustomer getItsqCustomer() {
        return (ItsqCustomer) getItsqItem();
    }
}
