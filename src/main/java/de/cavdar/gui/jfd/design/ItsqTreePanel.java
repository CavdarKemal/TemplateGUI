/*
 * Created by JFormDesigner on Sat Jan 03 21:48:07 CET 2026
 */

package de.cavdar.gui.jfd.design;

import java.awt.*;
import javax.swing.*;

/**
 * @author kemal
 */
public class ItsqTreePanel extends JPanel {
    public ItsqTreePanel() {
        initComponents();
    }

    public JScrollPane getScrollPane1() {
        return scrollPane1;
    }

    public JTree getTreeItsq() {
        return treeItsq;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        scrollPane1 = new JScrollPane();
        treeItsq = new JTree();

        //======== this ========
        setLayout(new BorderLayout());

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(treeItsq);
        }
        add(scrollPane1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTree treeItsq;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
