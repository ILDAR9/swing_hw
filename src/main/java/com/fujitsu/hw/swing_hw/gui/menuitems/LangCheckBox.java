package com.fujitsu.hw.swing_hw.gui.menuitems;

import javax.swing.*;
import java.util.Locale;

/**
 * User: Ildar
 * Date: 24.04.13
 */
public class LangCheckBox extends JRadioButtonMenuItem {

    final private Locale local;

    public LangCheckBox(Locale local) {
        super();
        this.local = local;
    }

    public Locale getRelatedLocal() {
        return local;
    }
}
