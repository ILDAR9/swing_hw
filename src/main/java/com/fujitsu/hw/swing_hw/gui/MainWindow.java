package com.fujitsu.hw.swing_hw.gui;

import com.fujitsu.hw.swing_hw.gui.menuitems.LangCheckBox;
import com.fujitsu.hw.swing_hw.utils.UTF8Bundle;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * User: Ildar
 * Date: 24.04.13
 */
public class MainWindow extends JFrame {
    private static final String propertyFile = "/gui.properties", resouceBundleFile = "LabelsBundle";
    private Properties properties;
    static ResourceBundle labelRes;
    private File searchDir;
    private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
    private JMenuItem menuTheme, menuLang, menuExit;
    private JTextField textDir, textFileName;
    private JLabel labelFileName, labelDir, labelSearchResults;
    private FileChooseFrame chooseDirFrame;
    private JButton btnChooseDir, btnStartSearch, btnStopSearch;
    private JRadioButtonMenuItem itemDefaultTheme, itemSystemTheme, itemModernTheme;
    private JRadioButtonMenuItem[] guiThemes;
    private LangCheckBox[] langs;
    private final int EN = 0, RU = 1, FR = 2, DEFAULT_THEME = 0, MODERN_THEME = 1, SYSTEM_THEME = 2;
    private FileTree fileTree;
    private JFileFinder fileFinderWorker;

     private ImageIcon createIcon(String path) {
        URL imgURL = MainWindow.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            logger.error("image: {} doesn't exist", path);
            return null;
        }
    }

    private void changeLocal(Locale locale) {
        Locale.setDefault(locale);
    }

    private JComponent createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        //Main menu
        JMenu mainMenu = new JMenu();
        mainMenu.setIcon(createIcon("images/mainmenu.png"));
        menuExit = new JMenuItem();
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
        //Settings
        JMenu settings = new JMenu();
        settings.setIcon(createIcon("images/settings.png"));
        //---languages
        menuLang = new JMenu();
        menuTheme = new JMenu();
        langs = new LangCheckBox[3];
        langs[RU] = new LangCheckBox(Locale.forLanguageTag("ru"));
        langs[RU].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        langs[EN] = new LangCheckBox(Locale.forLanguageTag("en"));
        langs[FR] = new LangCheckBox(Locale.forLanguageTag("fr"));
        ItemListener langListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Locale locale = null;
                    for (int i = 0; i < langs.length; i++)
                        if (langs[i].isSelected()) {
                            locale = langs[i].getRelatedLocal();
                            properties.setProperty("language_code", Integer.toString(i));
                            saveProperties();
                            break;
                        }
                    if (locale != null) {
                        internalization(locale);
                        setComponentNames();
                    }
                }
            }
        };
        //---Themes
        guiThemes = new JRadioButtonMenuItem[3];
        guiThemes[MODERN_THEME] = new JRadioButtonMenuItem();
        guiThemes[DEFAULT_THEME] = new JRadioButtonMenuItem();
        guiThemes[SYSTEM_THEME] = new JRadioButtonMenuItem();
        ItemListener themeListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    for (int i = 0; i < guiThemes.length; i++)
                        if (guiThemes[i].isSelected()) {
                            switch (i) {
                                case (MODERN_THEME):
                                    GUIThemes.setModernLookAndFeel(MainWindow.this);
                                    logger.info("GUITheme was changed to MODERN");
                                    break;
                                case (SYSTEM_THEME):
                                    GUIThemes.setSystemLookAndFeel(MainWindow.this);
                                    logger.info("GUITheme was changed to SYSTEM");
                                    break;
                                case (DEFAULT_THEME):
                                    GUIThemes.setDefaultLookAndFeel(MainWindow.this);
                                    logger.info("GUITheme was changed to DEFAULT");
                                    break;
                            }
                            properties.setProperty("theme_code", Integer.toString(i));
                            saveProperties();
                            MainWindow.this.revalidate();
                            break;
                        }
                }
            }
        };
        // Help
        JMenu help = new JMenu();
        help.setIcon(createIcon("images/help.png"));
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String siteAddres = "www/swing_home/help.com";
                try {
                    URI uri = new URL(siteAddres).toURI();
                    java.awt.Desktop.getDesktop().browse(uri);
                } catch (URISyntaxException ex) {
                    logger.error("{} was caught while trying to create url: {}", ex.getClass().getName(), siteAddres, ex);
                } catch (MalformedURLException ex) {
                    logger.error("{} was caught while trying to create url: {}", ex.getClass().getName(), siteAddres, ex);
                } catch (IOException ex) {
                    logger.error("{} was caught while trying to open {}", ex.getClass().getName(), siteAddres, ex);
                }
            }
        });

        // order of menu btns
        //Main menu
        mainMenu.add(menuExit);
        //Settings
        //---Languages
        ButtonGroup langGroup = new ButtonGroup();
        for (JMenuItem lang : langs) {
            lang.addItemListener(langListener);
            langGroup.add(lang);
            menuLang.add(lang);
        }
        settings.add(menuLang);
        //---Themes
        ButtonGroup themeGroup = new ButtonGroup();
        for (JMenuItem theme : guiThemes) {
            theme.addItemListener(themeListener);
            themeGroup.add(theme);
            menuTheme.add(theme);
        }
        settings.add(menuTheme);
        //menuBar
        //---Setings
        menuBar.add(mainMenu);
        menuBar.add(settings);
        //---Help
        menuBar.add(help);
        return menuBar;
    }

    public void setComponentNames() {
        setTitle(labelRes.getString("name"));
        labelFileName.setText(labelRes.getString("label_filename"));
        labelDir.setText(labelRes.getString("label_dir"));
        btnChooseDir.setText(labelRes.getString("btn_choose_dir"));
        btnStartSearch.setText(labelRes.getString("start"));
        menuExit.setText(labelRes.getString("btn_exit"));
        menuTheme.setText(labelRes.getString("menu_theme"));
        menuLang.setText(labelRes.getString("menu_language"));
        chooseDirFrame.setTitle(labelRes.getString("title_choose_dir"));
        langs[RU].setText(labelRes.getString("menu_rus"));
        langs[EN].setText(labelRes.getString("menu_en"));
        langs[FR].setText(labelRes.getString("menu_fr"));
        btnStopSearch.setText(labelRes.getString("btn_stop"));
        labelSearchResults.setText(labelRes.getString("label_res"));
        guiThemes[MODERN_THEME].setText(labelRes.getString("theme_modern"));
        guiThemes[DEFAULT_THEME].setText(labelRes.getString("theme_default"));
        guiThemes[SYSTEM_THEME].setText(labelRes.getString("theme_system"));

        logger.info("app's language is " + Locale.getDefault().getLanguage());
    }

    private void internalization(Locale locale) {
        if (labelRes != null)
            labelRes.clearCache();
        labelRes = ResourceBundle.getBundle(resouceBundleFile, locale, new UTF8Bundle());
        changeLocal(locale);
    }

    private void saveProperties() {
        URL resourceUrl = getClass().getResource(propertyFile);
        try (OutputStream outputStream = new FileOutputStream(new File(resourceUrl.toURI()))) {
            properties.store(outputStream, "");
        } catch (URISyntaxException ex) {
            logger.error("{} was caught while trying to store properties", ex.getClass().getName(), ex);
        } catch (FileNotFoundException ex) {
            logger.error("{} was caught while trying to store properties", ex.getClass().getName(), ex);
        } catch (IOException ex) {
            logger.error("{} was caught while trying to store properties", ex.getClass().getName(), ex);
        }
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(propertyFile));
        } catch (IOException ex) {
            logger.error("{} was caught while trying to load property file", ex.getClass().getName(), ex);
        }
        return properties;
    }

    private void restoreDefaultSettings(Properties properties) {
        //get ResourceBundle relative to saved Locale
        String lang_code = properties.get("language_code").toString();
        langs[Integer.parseInt(lang_code)].setSelected(true);
        String theme_code = properties.get("theme_code").toString();
        guiThemes[Integer.parseInt(theme_code)].setSelected(true);
    }

    public MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel basePanel = new JPanel(new BorderLayout());
        //file name textLabel
        labelFileName = new JLabel();
        //file name text
        textFileName = new JTextField();
        textFileName.setSize(30, 15);
        //dir name label
        labelDir = new JLabel();
        //dir name textField
        textDir = new JTextField();
        textDir.setEditable(false);
        //choose dir
        chooseDirFrame = new FileChooseFrame("");
        btnChooseDir = new JButton();
        searchDir = null;
        btnChooseDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDirFrame.startDialog();
                searchDir = chooseDirFrame.getChoosedDir();
                if (searchDir != null) {
                    textDir.setText(searchDir.toString());
                    revalidate();
                    logger.info("dir for searching: \"{}\"", searchDir);
                }
            }
        });
        //File's tree
        fileTree = new FileTree(new DefaultMutableTreeNode("."));
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(fileTree);

        // stop searching
        btnStopSearch = new JButton();
        btnStopSearch.setEnabled(false);
        btnStopSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileFinderWorker != null) {
                    fileFinderWorker.stopSearching();
                    btnStartSearch.setEnabled(true);
                    btnStopSearch.setEnabled(false);
                }
            }
        });

        // searching
        btnStartSearch = new JButton();
        getRootPane().setDefaultButton(btnStopSearch);
        fileFinderWorker = null;
        btnStartSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String fileName = textFileName.getText();
                if (searchDir == null) {
                    JOptionPane.showMessageDialog(MainWindow.this, labelRes.getString("msg_warning_no_dir"),
                            labelRes.getString("title_warning"), JOptionPane.ERROR_MESSAGE);
                } else if (fileName == null || !(fileName.length() > 0)) {
                    JOptionPane.showMessageDialog(MainWindow.this, labelRes.getString("msg_warning_no_filename"),
                            labelRes.getString("title_warning"), JOptionPane.ERROR_MESSAGE);
                } else {
                    logger.info("file name for searching: \"{}\"", fileName);
                    // It isn't cool but I stopped on this variant
                    fileTree.refreshStart();
                    fileTree.revalidate();
                    fileFinderWorker = new JFileFinder(searchDir, fileName, fileTree, btnStartSearch, btnStopSearch);
                    fileFinderWorker.execute();
                    btnStartSearch.setEnabled(false);
                    btnStopSearch.setEnabled(true);
                }
            }
        });
        //label Search results
        labelSearchResults = new JLabel();

        //Menu
        JComponent menuBar = createMenuBar();

        //restore settings
        properties = loadProperties();
        restoreDefaultSettings(properties);
        //   Exit action

        //set componet's names relative to saved locale
        setComponentNames();
        //Left panel
        basePanel.add(menuBar,BorderLayout.NORTH);
        JPanel panelLeft = new JPanel(new MigLayout());
        panelLeft.add(labelFileName, "cell 0 0");
        panelLeft.add(textFileName, "growx");
        panelLeft.add(labelDir, "cell 0 1");
        panelLeft.add(textDir, "growx");
        panelLeft.add(btnChooseDir, "wrap");
        panelLeft.add(btnStartSearch, "cell 1 2");
        panelLeft.add(btnStopSearch);
        //Right panel
        JPanel panelRight = new JPanel(new MigLayout());
        panelRight.add(labelSearchResults, "wrap");
        scrollpane.setPreferredSize(new Dimension(400, 310));
        panelRight.add(scrollpane);

        basePanel.add(panelLeft, BorderLayout.WEST);
        basePanel.add(panelRight, BorderLayout.EAST);

        getContentPane().add(basePanel);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame fr = new MainWindow();
            }
        });

    }
}
