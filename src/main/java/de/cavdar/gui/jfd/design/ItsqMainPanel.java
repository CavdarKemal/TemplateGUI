/*
 * Created by JFormDesigner on Sat Jan 03 23:15:24 CET 2026
 */

package de.cavdar.gui.jfd.design;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import de.cavdar.gui.jfd.view.*;

/**
 * @author kemal
 */
public class ItsqMainPanel extends JPanel {
    public ItsqMainPanel() {
        initComponents();
    }

    public JPanel getPanelControls() {
        return panelControls;
    }

    public JLabel getLabelConfig() {
        return labelConfig;
    }

    public JComboBox getComboBoxConfig() {
        return comboBoxConfig;
    }

    public JButton getButtonLoad() {
        return buttonLoad;
    }

    public JButton getButtonSave() {
        return buttonSave;
    }

    public JButton getButtonRefresh() {
        return buttonRefresh;
    }

    public JLabel getLabelFilter() {
        return labelFilter;
    }

    public JComboBox getComboBoxFilter() {
        return comboBoxFilter;
    }

    public JCheckBox getCheckBoxActiveOnly() {
        return checkBoxActiveOnly;
    }

    public JSplitPane getSplitPaneItsq() {
        return splitPaneItsq;
    }

    public ItsqPanelTree getPanelItsqTree() {
        return panelItsqTree;
    }

    public ItsqViewTabView getPanelItsqView() {
        return panelItsqView;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelControls = new JPanel();
        labelConfig = new JLabel();
        comboBoxConfig = new JComboBox();
        buttonLoad = new JButton();
        buttonSave = new JButton();
        buttonRefresh = new JButton();
        labelFilter = new JLabel();
        comboBoxFilter = new JComboBox();
        checkBoxActiveOnly = new JCheckBox();
        splitPaneItsq = new JSplitPane();
        panelItsqTree = new ItsqPanelTree();
        panelItsqView = new ItsqViewTabView();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panelControls ========
        {
            panelControls.setBorder(new EtchedBorder());
            panelControls.setLayout(new GridBagLayout());
            ((GridBagLayout)panelControls.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout)panelControls.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout)panelControls.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0, 1.0, 1.0E-4};
            ((GridBagLayout)panelControls.getLayout()).rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- labelConfig ----
            labelConfig.setText("Config:");
            panelControls.add(labelConfig, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 4), 0, 0));
            panelControls.add(comboBoxConfig, new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 2), 0, 0));

            //---- buttonLoad ----
            buttonLoad.setText("Load");
            buttonLoad.setIcon(new ImageIcon(getClass().getResource("/icons/folder_gear.png")));
            panelControls.add(buttonLoad, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 4), 0, 0));

            //---- buttonSave ----
            buttonSave.setText("Save");
            buttonSave.setIcon(new ImageIcon(getClass().getResource("/icons/save.png")));
            panelControls.add(buttonSave, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 4), 0, 0));

            //---- buttonRefresh ----
            buttonRefresh.setText("Refrsh");
            buttonRefresh.setIcon(new ImageIcon(getClass().getResource("/icons/refresh.png")));
            panelControls.add(buttonRefresh, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 2), 0, 0));

            //---- labelFilter ----
            labelFilter.setText("Filter:");
            panelControls.add(labelFilter, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 4), 0, 0));

            //---- comboBoxFilter ----
            comboBoxFilter.setEditable(true);
            panelControls.add(comboBoxFilter, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 4), 0, 0));

            //---- checkBoxActiveOnly ----
            checkBoxActiveOnly.setText("Active Only");
            panelControls.add(checkBoxActiveOnly, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 4, 2), 0, 0));
        }
        add(panelControls, BorderLayout.NORTH);

        //======== splitPaneItsq ========
        {
            splitPaneItsq.setDividerLocation(120);

            //---- panelItsqTree ----
            panelItsqTree.setBorder(new EtchedBorder());
            splitPaneItsq.setLeftComponent(panelItsqTree);

            //---- panelItsqView ----
            panelItsqView.setBorder(new EtchedBorder());
            splitPaneItsq.setRightComponent(panelItsqView);
        }
        add(splitPaneItsq, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelControls;
    private JLabel labelConfig;
    private JComboBox comboBoxConfig;
    private JButton buttonLoad;
    private JButton buttonSave;
    private JButton buttonRefresh;
    private JLabel labelFilter;
    private JComboBox comboBoxFilter;
    private JCheckBox checkBoxActiveOnly;
    private JSplitPane splitPaneItsq;
    private ItsqPanelTree panelItsqTree;
    private ItsqViewTabView panelItsqView;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
