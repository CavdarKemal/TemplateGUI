/*
 * Created by JFormDesigner on Sat Jan 03 12:11:58 CET 2026
 */

package de.cavdar.gui.design;

import javax.swing.*;
import java.awt.*;

/**
 * JFormDesigner-generated editor panel.
 *
 * @author kemal
 */
public class InternalFrameEditor extends JPanel {
    public InternalFrameEditor() {
        initComponents();
    }

    public JToolBar getToolBarMain() {
        return toolBarMain;
    }

    public JCheckBox getCheckBoxC1() {
        return checkBoxC1;
    }

    public JCheckBox getCheckBoxC2() {
        return checkBoxC2;
    }

    public JRadioButton getRadioButton1() {
        return radioButton1;
    }

    public JRadioButton getRadioButton2() {
        return radioButton2;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JScrollPane getScrollPane1() {
        return scrollPane1;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelToolbar = new JPanel();
        toolBarMain = new JToolBar();
        checkBoxC1 = new JCheckBox();
        checkBoxC2 = new JCheckBox();
        radioButton1 = new JRadioButton();
        radioButton2 = new JRadioButton();
        scrollPane1 = new JScrollPane();
        editorPane = new JEditorPane();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panelToolbar ========
        {
            panelToolbar.setLayout(null);

            //======== toolBarMain ========
            {
                toolBarMain.setRollover(true);

                //---- checkBoxC1 ----
                checkBoxC1.setText("Checkt 1");
                toolBarMain.add(checkBoxC1);

                //---- checkBoxC2 ----
                checkBoxC2.setText("Check 2");
                toolBarMain.add(checkBoxC2);

                //---- radioButton1 ----
                radioButton1.setText("Opt 1");
                toolBarMain.add(radioButton1);

                //---- radioButton2 ----
                radioButton2.setText("Opt 2");
                toolBarMain.add(radioButton2);
            }
            panelToolbar.add(toolBarMain);
            toolBarMain.setBounds(0, 0, 585, toolBarMain.getPreferredSize().height);

            {
                // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panelToolbar.getComponentCount(); i++) {
                    Rectangle bounds = panelToolbar.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panelToolbar.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panelToolbar.setMinimumSize(preferredSize);
                panelToolbar.setPreferredSize(preferredSize);
            }
        }
        add(panelToolbar, BorderLayout.PAGE_START);

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(editorPane);
        }
        add(scrollPane1, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelToolbar;
    private JToolBar toolBarMain;
    private JCheckBox checkBoxC1;
    private JCheckBox checkBoxC2;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JScrollPane scrollPane1;
    private JEditorPane editorPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
