package gui.library;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mapdb.Fun.Tuple2;

import controller.Controller;
import core.item.assets.Order;
import core.transaction.Transaction;
import database.wallet.TransactionMap;
import datachain.SortableList;
import lang.Lang;
import utils.ObserverMessage;
import utils.Pair;

public class SetIntervalPanel extends JPanel implements Observer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates new form SetInterval
     */
    public SetIntervalPanel(int type) {
        this.type=type;
        jLabelTotal = new JLabel();
        Controller.getInstance().addWalletListener(this);
        initComponents();
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

       // jLabelStart.setText(Lang.getInstance().translate("Interval") + ":");
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

        jLabelEnd.setText(Lang.getInstance().translate("") + ":");
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
        jButtonSetInterval.setText(Lang.getInstance().translate("View"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(6, 5, 5, 0);
        add(jButtonSetInterval, gridBagConstraints);

       
      //  jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" );
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
    private SortableList<Tuple2<String, Long>, Order> orders;
    private SortableList<Tuple2<String, String>, Transaction> transactions;
    public int type =0;

    // End of variables declaration
    @Override
    public void update(Observable arg0, Object arg1) {
        // TODO Auto-generated method stub
        try {
            this.syncUpdate(arg0, arg1);
        } catch (Exception e) {
            // GUI ERROR
        }

    }

    @SuppressWarnings("unchecked")
    public synchronized void syncUpdate(Observable o, Object arg) {
        ObserverMessage message = (ObserverMessage) arg;

        // CHECK IF NEW LIST
        // order transactions
        if (type == Transaction.CREATE_ORDER_TRANSACTION) {
            if (message.getType() == ObserverMessage.WALLET_RESET_ORDER_TYPE
                    || message.getType() == ObserverMessage.WALLET_LIST_ORDER_TYPE) {
                if (this.orders == null) {
                    this.orders = (SortableList<Tuple2<String, Long>, Order>) message.getValue();
                    this.orders.registerObserver();
                    int ff = this.orders.size();
                    jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" + this.orders.size());

                }

            } else if (message.getType() == ObserverMessage.WALLET_ADD_ORDER_TYPE) {
                // CHECK IF LIST UPDATED
                Pair<Tuple2<String, Long>, Order> item = (Pair<Tuple2<String, Long>, Order>) message.getValue();
                this.orders.add(0, item);
                jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" + this.orders.size());

            } else if (message.getType() == ObserverMessage.WALLET_REMOVE_ORDER_TYPE) {
                // CHECK IF LIST UPDATED
                this.orders.remove(0);
                jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" + this.orders.size());

            }
            // all transactions
        } else if (type  == Transaction.EXTENDED) {

            if (message.getType() == ObserverMessage.WALLET_LIST_TRANSACTION_TYPE) {
                if (this.transactions == null) {
                    this.transactions = (SortableList<Tuple2<String, String>, Transaction>) message.getValue();
                    this.transactions.registerObserver();
                    jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" + this.transactions.size());
                }
            }
            if (message.getType() == ObserverMessage.WALLET_ADD_TRANSACTION_TYPE
                    || message.getType() == ObserverMessage.WALLET_REMOVE_TRANSACTION_TYPE) {
                jLabelTotal.setText(Lang.getInstance().translate("Total") + ":" + this.transactions.size());
            }

        }

    }

    public void removeObservers() {
        if (type == Transaction.CREATE_ORDER_TRANSACTION) this.orders.removeObserver();
        if (type  == Transaction.EXTENDED)  this.transactions.removeObserver();
        Controller.getInstance().deleteWalletObserver(this);
    }

}
