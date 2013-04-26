package com.fujitsu.hw.swing_hw.gui;

import com.fujitsu.hw.swing_hw.utils.UTF8Bundle;

import javax.swing.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * User: Ildar
 * Date: 24.04.13
 */
public class FileChooseFrame extends JFrame {
    private File choosedDir = null;
    private JFileChooser dirChooser;
    private final String titleDialog;
    private final String msgDialog;

    public File getChoosedDir() {
        return choosedDir;
    }

    // get dialog of JFileChooser
    public void startDialog() {
        switch (dirChooser.showOpenDialog(null)) {
            case JFileChooser.CANCEL_OPTION:
                setVisible(false);
                break;
            case JFileChooser.APPROVE_OPTION:
                choosedDir = dirChooser.getSelectedFile();
                setVisible(false);
                break;
            case JFileChooser.ERROR_OPTION:
                JOptionPane.showMessageDialog(FileChooseFrame.this, msgDialog,
                        titleDialog, JOptionPane.ERROR_MESSAGE);
        }
    }

    public FileChooseFrame(String title) {
        super(title);
        final ResourceBundle labelRes = ResourceBundle.getBundle("LabelsBundle", Locale.getDefault(), new UTF8Bundle());
        titleDialog = labelRes.getString("title_dir_error");
        msgDialog = labelRes.getString("error");
        //internationalize of JFileChooser
        internationalize(Locale.getDefault());
        dirChooser = new JFileChooser();
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        getContentPane().add(dirChooser);
        pack();
    }

    private static void internationalize(Locale locale) {
        if (locale.getLanguage().equals("ru")) {
            ResourceBundle b = ResourceBundle.getBundle("FileChooser", locale, new UTF8Bundle());
            for (String s : b.keySet())
                UIManager.put(s, b.getString(s));

        }
    }
}
