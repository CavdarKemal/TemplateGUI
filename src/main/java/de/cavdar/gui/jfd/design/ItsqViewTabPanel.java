/*
 * Created by JFormDesigner on Sat Jan 03 22:42:47 CET 2026
 */

package de.cavdar.gui.jfd.design;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import de.cavdar.gui.jfd.view.*;

/**
 * @author kemal
 */
public class ItsqViewTabPanel extends JPanel {
    public ItsqViewTabPanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        panelRoot = new ItsqRootView();
        panelArchivBestand = new ItsqArchivBestandView();
        panelArchivBestandPhase = new ItsqArchibBestandPhaseView();
        panelRefExports = new ItsqRefExportsView();
        panelRefExportsPhase = new ItsqRefExportsPhaseView();
        panelCustomer = new ItsqCustomerView();
        panelScenario = new ItsqScenarioView();

        //======== this ========
        setBorder(new EtchedBorder());
        setLayout(new CardLayout());
        add(panelRoot, "card7");
        add(panelArchivBestand, "cardArchivBestand");
        add(panelArchivBestandPhase, "cardArchivBestandPhase");
        add(panelRefExports, "cardRefExports");
        add(panelRefExportsPhase, "cardRefExportsPhase");
        add(panelCustomer, "cardCustomer");
        add(panelScenario, "cardScenario");
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private ItsqRootView panelRoot;
    private ItsqArchivBestandView panelArchivBestand;
    private ItsqArchibBestandPhaseView panelArchivBestandPhase;
    private ItsqRefExportsView panelRefExports;
    private ItsqRefExportsPhaseView panelRefExportsPhase;
    private ItsqCustomerView panelCustomer;
    private ItsqScenarioView panelScenario;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
