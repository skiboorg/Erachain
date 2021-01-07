package org.erachain.gui.transaction;

import org.erachain.core.item.unions.UnionCls;
import org.erachain.core.transaction.IssueUnionRecord;
import org.erachain.gui.library.MTextPane;
import org.erachain.lang.Lang;
import org.erachain.utils.MenuPopupUtil;

import javax.swing.*;

@SuppressWarnings("serial")
public class IssueUnionDetailsFrame extends RecDetailsFrame {
    public IssueUnionDetailsFrame(IssueUnionRecord unionIssue) {
        super(unionIssue, false);

        //LABEL NAME
        ++labelGBC.gridy;
        JLabel nameLabel = new JLabel(Lang.T("Name") + ":");
        this.add(nameLabel, labelGBC);

        //NAME
        ++detailGBC.gridy;
        JTextField name = new JTextField(unionIssue.getItem().viewName());
        name.setEditable(false);
        MenuPopupUtil.installContextMenu(name);
        this.add(name, detailGBC);

        UnionCls union = (UnionCls) unionIssue.getItem();

        //LABEL DESCRIPTION
        ++labelGBC.gridy;
        JLabel descriptionLabel = new JLabel(Lang.T("Description") + ":");
        this.add(descriptionLabel, labelGBC);

        //DESCRIPTION
        ++detailGBC.gridy;
        MTextPane txtAreaDescription = new MTextPane(unionIssue.getItem().getDescription());
        //txtAreaDescription.setRows(4);
        txtAreaDescription.setBorder(name.getBorder());
        //txtAreaDescription.setEditable(false);
        MenuPopupUtil.installContextMenu(txtAreaDescription);
        this.add(txtAreaDescription, detailGBC);

        //LABEL Birthday
        ++labelGBC.gridy;
        JLabel birthdayLabel = new JLabel(Lang.T("Birthday") + ":");
        this.add(birthdayLabel, labelGBC);

        //Birthday
        ++detailGBC.gridy;
        //JTextField birtday = new JTextField(new Date(union.getBirthday()).toString());
        JTextField birtday = new JTextField(union.getBirthdayStr());
        birtday.setEditable(false);
        this.add(birtday, detailGBC);

        //LABEL PARENT
        ++labelGBC.gridy;
        JLabel parentLabel = new JLabel(Lang.T("Parent") + ":");
        this.add(parentLabel, labelGBC);

        //PARENT
        ++detailGBC.gridy;
        JTextField parent = new JTextField(String.valueOf(union.getParent()));
        parent.setEditable(false);
        //MenuPopupUtil.installContextMenu(gender);
        this.add(parent, detailGBC);

        //PACK
//		this.pack();
        //      this.setResizable(false);
        //       this.setLocationRelativeTo(null);
        this.setVisible(true);

    }
}
