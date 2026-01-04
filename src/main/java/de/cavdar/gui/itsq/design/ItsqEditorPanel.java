/*
 * Created by JFormDesigner on Sun Jan 04 13:15:18 CET 2026
 */

package de.cavdar.gui.itsq.design;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author kemal
 */
public class ItsqEditorPanel extends JPanel {
    public ItsqEditorPanel() {
        initComponents();
    }

    public JPanel getPanelControls() {
        return panelControls;
    }

    public JScrollPane getScrollPaneEditor() {
        return scrollPaneEditor;
    }

    public JEditorPane getEditorPaneEditor() {
        return editorPaneEditor;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelControls = new JPanel();
        labelTitle = new JLabel();
        panelEditor = new JPanel();
        scrollPaneEditor = new JScrollPane();
        editorPaneEditor = new JEditorPane();
        toolBarEditor = new JToolBar();
        buttonSave = new JButton();

        //======== this ========
        setLayout(new BorderLayout());

        //======== panelControls ========
        {
            panelControls.setBorder(new EtchedBorder());
            panelControls.setLayout(new GridBagLayout());
            ((GridBagLayout)panelControls.getLayout()).columnWidths = new int[] {261, 0};
            ((GridBagLayout)panelControls.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout)panelControls.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)panelControls.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- labelTitle ----
            labelTitle.setText("Datei %s");
            labelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            labelTitle.setEnabled(false);
            panelControls.add(labelTitle, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2), 0, 0));
        }
        add(panelControls, BorderLayout.PAGE_START);

        //======== panelEditor ========
        {
            panelEditor.setLayout(new BorderLayout());

            //======== scrollPaneEditor ========
            {

                //---- editorPaneEditor ----
                editorPaneEditor.setFont(new Font("Courier New", Font.PLAIN, 12));
                editorPaneEditor.setBackground(Color.lightGray);
                scrollPaneEditor.setViewportView(editorPaneEditor);
            }
            panelEditor.add(scrollPaneEditor, BorderLayout.CENTER);

            //======== toolBarEditor ========
            {

                //---- buttonSave ----
                buttonSave.setText("Save");
                buttonSave.setIcon(new ImageIcon(getClass().getResource("/icons/save.png")));
                toolBarEditor.add(buttonSave);
            }
            panelEditor.add(toolBarEditor, BorderLayout.NORTH);
        }
        add(panelEditor, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel panelControls;
    private JLabel labelTitle;
    private JPanel panelEditor;
    private JScrollPane scrollPaneEditor;
    private JEditorPane editorPaneEditor;
    private JToolBar toolBarEditor;
    private JButton buttonSave;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
