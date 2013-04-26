package com.fujitsu.hw.swing_hw.gui;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * User: Ildar
 * Date: 24.04.13
 */
public class FileTree extends JTree {

    public void addFile(File file) {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        if (root == null) {
            root = new DefaultMutableTreeNode(".");
        }
        String[] splittedFileName = file.getAbsolutePath().split("[\\\\/]");
        DefaultMutableTreeNode cur = root.getNextNode();
        int i = 0;
        if (cur != null) {
            DefaultMutableTreeNode node = null;
            String curFile = null;
            boolean hasSame;
            for (; cur != null && i < splittedFileName.length; cur = cur.getNextNode(), i++) {
                node = cur;
                curFile = splittedFileName[i];
                hasSame = false;
                do {
                    if (node.toString().equals(curFile)) {
                        cur = node;
                        hasSame = true;
                        break;
                    }
                } while (((node = node.getNextSibling()) != null));
                if (!hasSame) {
                    cur = cur.getPreviousNode();
                    break;
                }
            }

        } else
            cur = root;
        while (i < splittedFileName.length) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(splittedFileName[i++]);
            cur.add(node);
            cur = node;
        }
    }

    public void refreshStart() {
        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        model.reload(root);
    }

    public FileTree(DefaultMutableTreeNode root) {
        super(root);
    }
}
