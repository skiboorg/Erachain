package org.erachain.gui.library;

import org.erachain.controller.Controller;
import org.erachain.dbs.DBTab;
import org.erachain.dbs.DBTabImpl;
import org.erachain.gui.ObserverWaiter;
import org.erachain.lang.Lang;
import org.erachain.utils.ObserverMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class SetIntervalPanel extends JPanel implements Observer, ObserverWaiter {

    private final int RESET_EVENT;
    private final int LIST_EVENT;
    private final int ADD_EVENT;
    private final int REMOVE_EVENT;

    Logger LOGGER;

    private static final long serialVersionUID = 1L;
    DBTabImpl map;
    private long size;
    private boolean needUpdate;

    /**
     * В динамическом режиме перерисовывается при каждом прилете записи.<br>
     * Без динамического режима перерисовывается по внешнему таймеру из
     * gui.GuiTimer - только если было обновление
     */
    public SetIntervalPanel(DBTabImpl map) {
        jLabelTotal = new JLabel();
        this.map = map;
        this.size = this.map.size();

        RESET_EVENT = (Integer) map.getObservableData().get(DBTab.NOTIFY_RESET);
        LIST_EVENT = (Integer) map.getObservableData().get(DBTab.NOTIFY_LIST);
        ADD_EVENT = (Integer) map.getObservableData().get(DBTab.NOTIFY_ADD);
        REMOVE_EVENT = (Integer) map.getObservableData().get(DBTab.NOTIFY_REMOVE);

        LOGGER = LoggerFactory.getLogger(getClass());

        initComponents();

        addObservers();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelStart = new javax.swing.JLabel();
        jTextFieldStart = new javax.swing.JTextField();
        jLabelEnd = new javax.swing.JLabel();
        jTextFieldEnd = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

       // jLabelStart.setText(Lang.T("Interval") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 8, 0);
        add(jLabelStart, gridBagConstraints);

        jTextFieldStart.setText("0");
        jTextFieldStart.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldStart.setName(""); // NOI18N
        jTextFieldStart.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 8, 0);
        add(jTextFieldStart, gridBagConstraints);

        jLabelEnd.setText(Lang.T("") + ":");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 8, 0);
        add(jLabelEnd, gridBagConstraints);

        jTextFieldEnd.setText("50");
        jTextFieldEnd.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldEnd.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 8, 0);
        add(jTextFieldEnd, gridBagConstraints);

        jButtonSetInterval = new javax.swing.JButton();
        jButtonSetInterval.setText(Lang.T("View"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 0);
        add(jButtonSetInterval, gridBagConstraints);

       
        jLabelTotal.setText(Lang.T("Total") + ":" + size);
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 0);
        add(jLabelTotal, gridBagConstraints);
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JLabel jLabelEnd;
    private javax.swing.JLabel jLabelStart;
    public javax.swing.JTextField jTextFieldEnd;
    public javax.swing.JTextField jTextFieldStart;
    public javax.swing.JButton jButtonSetInterval;
    JLabel jLabelTotal;

    // End of variables declaration
    @Override
    public void update(Observable arg0, Object arg1) {
        // TODO Auto-generated method stub
        try {
            this.syncUpdate(arg0, arg1);
        } catch (Exception e) {
            // GUI ERROR
            LOGGER.error(e.getMessage(), e);
        }

    }

    @SuppressWarnings("unchecked")
    public synchronized void syncUpdate(Observable o, Object arg) {
        ObserverMessage message = (ObserverMessage) arg;

        if (message.getType() == ObserverMessage.GUI_REPAINT
                && needUpdate) {

            needUpdate = false;
            jLabelTotal.setText(Lang.T("Total") + ":" + size);

        } else if (message.getType() == RESET_EVENT) {
            size = 0;
            jLabelTotal.setText(Lang.T("Total") + ":" + size);

        } else if (message.getType() == LIST_EVENT) {
            size = map.size();
            jLabelTotal.setText(Lang.T("Total") + ":" + size);

        } else if (message.getType() == ADD_EVENT) {
            ++size;
            needUpdate = true;

        } else if (message.getType() == REMOVE_EVENT) {
            --size;
            needUpdate = true;

        }

    }

    public void addObservers() {

        if (Controller.getInstance().doesWalletDatabaseExists()) {
            Controller.getInstance().guiTimer.addObserver(this); // обработка repaintGUI
            map.addObserver(this);
        } else {
            // ожидаем открытия кошелька
            Controller.getInstance().wallet.addWaitingObserver(this);
        }
    }

    public void deleteObservers() {
        if (Controller.getInstance().doesWalletDatabaseExists()) {
            Controller.getInstance().guiTimer.deleteObserver(this); // обработка repaintGUI
            map.deleteObserver(this);
        }
    }

}
