package org.erachain.gui.items.statuses;

import org.erachain.core.block.GenesisBlock;
import org.erachain.core.item.statuses.StatusCls;
import org.erachain.gui.library.MTextPane;
import org.erachain.gui.library.Library;
import org.erachain.lang.Lang;

// Info for status
public class StatusInfo extends MTextPane {
    private static final long serialVersionUID = 476307470457045006L;

    public StatusInfo() {
        //	this.setContentType("text/html");
        //	this.setBackground(MainFrame.getFrames()[0].getBackground());
    }


    static String Get_HTML_Status_Info_001(StatusCls status) {

        String message;

        if (status == null) return message = "Empty Status";

        if (!status.isConfirmed()) {
            message = Lang.getInstance().translate("Not confirmed");
        } else {
            message = "" + status.getKey();
        }
        message = "<div><b>" + message + "</b> : " + status.viewName() + "</div>";

        message += "<div>" + Library.to_HTML(status.getDescription()) + "</div>";
        message += "<div>" + (status.isUnique() ? "UNIQUE" : "multi") + "</div>";

        String creator = GenesisBlock.CREATOR.equals(status.getOwner()) ? "GENESIS" : status.getOwner().getPersonAsString_01(false);

        message += "<div> Creator: " + (creator.length() == 0 ? status.getOwner().getAddress() : creator) + "</div>";

        return message;
    }

    public void show_001(StatusCls status) {
        set_text("<html>" + Get_HTML_Status_Info_001(status) + "</html>");
    }

    public void delay_on_Close() {
    }

}
