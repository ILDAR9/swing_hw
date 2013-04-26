package com.fujitsu.hw.swing_hw.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * User: Ildar
 * Date: 25.04.13
 */
public class GUIThemes {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);

    public static void setModernLookAndFeel(JFrame jFrame) {
        String modifier = "org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel";
        changeLookAndFell(jFrame, modifier);
    }

    public static void changeLookAndFell(JFrame jFrame, String theme) {
        try {
            UIManager.setLookAndFeel(theme);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException ex) {
            logger.error("{} was caught while trying to customize gui by look and feel", ex.getClass().getName(), ex);
        } catch (IllegalAccessException ex) {
            logger.error("{} was caught while trying to customize gui by look and feel", ex.getClass().getName(), ex);
        } catch (UnsupportedLookAndFeelException ex) {
            logger.error("{} was caught while trying to customize gui by look and feel", ex.getClass().getName(), ex);
        }
        SwingUtilities.updateComponentTreeUI(jFrame);
    }

    public static void setDefaultLookAndFeel(JFrame jFrame) {
        final String modifier = "javax.swing.plaf.metal.MetalLookAndFeel";
        changeLookAndFell(jFrame, modifier);
    }

    public static void setSystemLookAndFeel(JFrame jFrame) {
        final String modifier = UIManager.getSystemLookAndFeelClassName();
        changeLookAndFell(jFrame, modifier);
    }
}
