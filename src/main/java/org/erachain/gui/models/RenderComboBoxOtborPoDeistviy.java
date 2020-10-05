package org.erachain.gui.models;

import org.erachain.core.transaction.TransactionAmount;
import sun.swing.DefaultLookup;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class RenderComboBoxOtborPoDeistviy extends DefaultListCellRenderer{

        private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        private static final Border DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        protected static Border noFocusBorder = DEFAULT_NO_FOCUS_BORDER;

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            setComponentOrientation(list.getComponentOrientation());

            Color bg = null;
            Color fg = null;

            JList.DropLocation dropLocation = list.getDropLocation();
            if (dropLocation != null
                    && !dropLocation.isInsert()
                    && dropLocation.getIndex() == index) {

                bg = DefaultLookup.getColor(this, ui, "List.dropCellBackground");
                fg = DefaultLookup.getColor(this, ui, "List.dropCellForeground");

                isSelected = true;
            }

            if (isSelected) {
                setBackground(bg == null ? list.getSelectionBackground() : bg);
                setForeground(fg == null ? list.getSelectionForeground() : fg);
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText("");
            } else {
                setIcon(null);
                setText((value == null) ? "" : getDiscriptionValue((Integer) value));
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());

            Border border = null;
            if (cellHasFocus) {
                if (isSelected) {
                    border = DefaultLookup.getBorder(this, ui, "List.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = DefaultLookup.getBorder(this, ui, "List.focusCellHighlightBorder");
                }
            } else {
                border = getNoFocusBorder();
            }
            setBorder(border);

            return this;
        }

        private String getDiscriptionValue(int value){
            switch (value) {
                case 1:
                    return "Знач 1";
                case       2:
                    return  "Знач 2";
                case  3:
                    return  "Знач 3";
                case  4:
                    return  "Знач 4";
                case  5:
                    return  "Знач 5";

            }

            return "";
        }

        private Border getNoFocusBorder() {
            Border border = DefaultLookup.getBorder(this, ui, "List.cellNoFocusBorder");
            if (System.getSecurityManager() != null) {
                if (border != null) return border;
                return SAFE_NO_FOCUS_BORDER;
            } else {
                if (border != null &&
                        (noFocusBorder == null ||
                                noFocusBorder == DEFAULT_NO_FOCUS_BORDER)) {
                    return border;
                }
                return noFocusBorder;
            }
        }

    }





