package org.erachain.gui;

import org.erachain.controller.Controller;

public class ClosingDialog {

    public ClosingDialog() {

        AboutFrame.getInstance().setUserClose(false);
        AboutFrame.getInstance().setModal(false);

        AboutFrame.getInstance().console_Text.setVisible(true);
        AboutFrame.getInstance().setVisible(true);

        new Thread() {
            @Override
            public void run() {
                Controller.getInstance().deleteObservers();
                Controller.getInstance().addSingleObserver(AboutFrame.getInstance());
                AboutFrame.getInstance().lblAuthorsLabel.setVisible(false);
                Controller.getInstance().stopAndExit(0);
                //       aa.setVisible(false);
            }
        }.start();
    }
}
