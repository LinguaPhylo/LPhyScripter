package lphyscripter;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    JTabbedPane mainPane;

    DataPanel dataPanel;

    public MainPanel() {

        mainPane = new JTabbedPane(SwingConstants.TOP);

        dataPanel = new DataPanel();

        mainPane.add("Data", dataPanel);

        add(mainPane, BorderLayout.CENTER);
    }


    public String getCode() {
        return null;
    }
}
