package com.fujitsu.hw.swing_hw.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * User: Ildar
 * Date: 24.04.13
 */
public class JFileFinder extends SwingWorker<List<File>, File> {
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
    private final String fileName;
    private final File directory;
    private FileTree fileTree;
    private JComponent source, antiSource;
    private boolean hasStoped;

    public JFileFinder(File directory, String fileName, FileTree fileTree,JComponent source,JComponent antiSource) {
        this.fileName = fileName;
        this.directory = directory;
        this.fileTree = fileTree;
        this.source = source;
        this.antiSource = antiSource;
        hasStoped = false;
    }

    @Override
    protected List<File> doInBackground() throws Exception {
        search(directory);
        return null;
    }

    //stop searcing
    public void stopSearching() {
        hasStoped = true;
    }

    private void search(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!hasStoped && file.isDirectory())
                search(file);
            else if (file.getName().contains(fileName))
                publish(file);
        }
    }

    @Override
    protected void process(List<File> chunks) {
        StringBuilder sb = new StringBuilder();
        for (File file : chunks) {
            fileTree.addFile(file);
            SwingUtilities.updateComponentTreeUI (fileTree);
            sb.append("{" + file.toString() + "}\n");
        }
        logger.info("found files for \"{}\" in {} : {}", fileName, directory, sb.toString());

    }

    @Override
    protected void done() {
        source.setEnabled(true);
        antiSource.setEnabled(false);
        JOptionPane.showMessageDialog(fileTree, MainWindow.labelRes.getString("msg_search_stop"),
                MainWindow.labelRes.getString("title_search"), JOptionPane.INFORMATION_MESSAGE);
    }
}
