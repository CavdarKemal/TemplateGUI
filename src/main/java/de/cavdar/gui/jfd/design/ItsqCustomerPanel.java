/*
 * Created by JFormDesigner on Sat Jan 03 22:34:57 CET 2026
 */

package de.cavdar.gui.jfd.design;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * @author kemal
 */
public class ItsqCustomerPanel extends JPanel {
    public ItsqCustomerPanel() {
        initComponents();
    }

    public JPanel getPanelControls() {
        return panelControls;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("de.cavdar.gui.design.form");
        panelControls = new JPanel();
        labelTitle = new JLabel();
        button2 = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panelControls ========
        {
            panelControls.setLayout(new GridBagLayout());
            ((GridBagLayout)panelControls.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout)panelControls.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panelControls.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout)panelControls.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- labelTitle ----
            labelTitle.setText("CUSTOMER  View");
            labelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            panelControls.add(labelTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

            //---- button2 ----
            button2.setText(bundle.getString("ItsqRefExportsPanel.button2.text"));
            panelControls.add(button2, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panelControls, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelControls;
    private JLabel labelTitle;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
