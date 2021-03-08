package lphyscripter;

import javax.swing.*;

public class MainPanel extends JPanel {

    JTabbedPane mainPane;

    DataPanel dataPanel;

    public MainPanel() {

        mainPane = new JTabbedPane(SwingConstants.TOP);

        dataPanel = new DataPanel();

        mainPane.add("Data", dataPanel);
    }


    public String getCode() {
        return null;
    }
}
