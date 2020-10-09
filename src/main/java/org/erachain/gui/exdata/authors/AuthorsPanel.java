package org.erachain.gui.exdata.authors;

import org.erachain.gui.library.MTable;
import org.erachain.lang.Lang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthorsPanel extends JPanel {
    public final AuthorsModel authorsAuthorsModel;
    public final MTable jTableAuthors;
    private JScrollPane jScrollPaneAuthors;
    private JButton jButtonAddAuthor;
    private JButton jButtonRemoveAuthor;
    private GridBagConstraints gridBagConstraints;


    public AuthorsPanel() {

        super();
        this.setName(Lang.getInstance().translate("Authors"));
        jButtonAddAuthor = new JButton();
        jScrollPaneAuthors = new JScrollPane();
        jButtonRemoveAuthor = new JButton();
        jButtonAddAuthor.setVisible(false);
        jButtonRemoveAuthor.setVisible(true);

        authorsAuthorsModel = new AuthorsModel(0);
        jTableAuthors = new MTable(authorsAuthorsModel);

        this.jButtonRemoveAuthor.addActionListener(new ActionListener() {
            // delete row
            @Override
            public void actionPerformed(ActionEvent e) {
                int interval = 0;
                if (authorsAuthorsModel.getRowCount() > 0) {
                    int selRow = jTableAuthors.getSelectedRow();
                    if (selRow != -1 && authorsAuthorsModel.getRowCount() >= selRow) {
                        ((DefaultTableModel) authorsAuthorsModel).removeRow(selRow);

                        interval = selRow - 1;
                        if (interval < 0) interval = 0;
                    }
                }

                if (authorsAuthorsModel.getRowCount() < 1) {
                    authorsAuthorsModel.addRow(new Object[]{(int) 0, "", "", ""});
                    interval = 0;
                }

                jTableAuthors.setRowSelectionInterval(interval, interval);
                authorsAuthorsModel.fireTableDataChanged();
            }
        });

        this.setLayout(new GridBagLayout());

        jScrollPaneAuthors.setOpaque(false);
        jScrollPaneAuthors.setPreferredSize(new Dimension(0, 0));



        jTableAuthors.setVisible(true);
        jScrollPaneAuthors.setViewportView(jTableAuthors);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        this.add(jScrollPaneAuthors, gridBagConstraints);

        jButtonAddAuthor.setText(Lang.getInstance().translate("Add"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(8, 8, 8, 8);
        this.add(jButtonAddAuthor, gridBagConstraints);

        jButtonRemoveAuthor.setText(Lang.getInstance().translate("Remove"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(8, 8, 8, 8);
        this.add(jButtonRemoveAuthor, gridBagConstraints);


    }


    // table model class



}
