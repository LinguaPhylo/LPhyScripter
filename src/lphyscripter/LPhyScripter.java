package lphyscripter;

import lphy.app.LinguaPhyloStudio;
import lphy.utils.LoggerUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

import static lphy.app.Utils.saveToFile;

public class LPhyScripter {

    private static String APP_NAME = "LPhy Scripter";
    private static String VERSION = "0.01";

    static {
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PhyloProb");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private static final int MAX_WIDTH = 1600;
    private static final int MAX_HEIGHT = 1200;

    private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    private JFrame frame;

    private MainPanel mainPanel;

    public LPhyScripter() {

        JMenuBar menuBar;
        JMenu fileMenu;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem saveAsMenuItem = new JMenuItem("Save LPhy Script to File...");
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MASK));
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        mainPanel = new MainPanel();

        frame = new JFrame(APP_NAME + " version " + VERSION);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int width = Math.min(MAX_WIDTH, dim.width * 9 / 10);
        int height = Math.min(MAX_HEIGHT, dim.height * 9 / 10);

        frame.setSize(width, height);
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        saveAsMenuItem.addActionListener(e -> saveToFile(mainPanel.getCode()));

    }

    public static void main(String[] args) {

        LPhyScripter app = new LPhyScripter();
    }
}
