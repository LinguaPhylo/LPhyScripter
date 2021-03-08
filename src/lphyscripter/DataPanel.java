package lphyscripter;

import lphy.evolution.alignment.Alignment;
import lphy.graphicalModel.Value;
import lphy.utils.LoggerUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class DataPanel extends JPanel {

    final static int NAME_COLUMN = 0;
    final static int FILE_COLUMN = 1;
    final static int TAXA_COLUMN = 2;
    final static int SITES_COLUMN = 3;
    final static int TYPE_COLUMN = 4;

    final static int NR_OF_COLUMNS = 5;

    final static int STRUT_SIZE = 5;

    /**
     * alignments that form a partition. These can be FilteredAlignments *
     */
    List<Value<Alignment>> alignments;
    int partitionCount;
    Object[][] tableData;
    JTable table;
    JTextField nameEditor;
    List<JButton> linkButtons;
    List<JButton> unlinkButtons;
    JButton splitButton;

    /**
     * The button for deleting an alignment in the alignment list.
     */
    JButton delButton;

    private JScrollPane scrollPane;
    private JButton addButton;
    private JButton replaceButton;

    public DataPanel() {
        this(null);
    }

    public DataPanel(Value<Alignment> alignment) {

        alignments = new ArrayList<>();
        if (alignment != null) alignments.add(alignment);
        linkButtons = new ArrayList<>();
        unlinkButtons = new ArrayList<>();
        partitionCount = alignments.size();

        // override BoxLayout in superclass
        setLayout(new BorderLayout());

        add(createTable(), BorderLayout.CENTER);

        Color focusColor = UIManager.getColor("Focus.color");
        Border focusBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, focusColor);

        // this should place the add/remove/split buttons at the bottom of the window.
        add(createAddRemoveSplitButtons(), BorderLayout.SOUTH);
    }

    private JComponent createAddRemoveSplitButtons() {
        Box buttonBox = Box.createHorizontalBox();

        addButton = new JButton("+");
        addButton.setName("+");
        addButton.setToolTipText("Add item to the list");
        addButton.addActionListener(e -> addItem());
        buttonBox.add(Box.createHorizontalStrut(STRUT_SIZE));
        buttonBox.add(addButton);
        buttonBox.add(Box.createHorizontalStrut(STRUT_SIZE));

        delButton = new JButton("-");
        delButton.setName("-");
        delButton.setToolTipText("Delete selected items from the list");
        buttonBox.add(delButton);
        buttonBox.add(Box.createHorizontalStrut(STRUT_SIZE));

        replaceButton = new JButton("r");
        replaceButton.setName("r");
        replaceButton.setToolTipText("Replace alignment by one loaded from file");
        replaceButton.addActionListener(e -> replaceItem());
        buttonBox.add(Box.createHorizontalStrut(STRUT_SIZE));
        buttonBox.add(replaceButton);
        buttonBox.add(Box.createHorizontalStrut(STRUT_SIZE));


        splitButton = new JButton("Split");
        splitButton.setName("Split");
        splitButton.setToolTipText("Split alignment into partitions, for example, codon positions");
        splitButton.addActionListener(e -> splitItem());
        buttonBox.add(splitButton);

        buttonBox.add(Box.createHorizontalGlue());

        return buttonBox;
    }

    private void replaceItem() {
    }

    private void splitItem() {
    }

    private void addItem() {

    }

    private JScrollPane createTable() {

        String[] columnData = new String[]{"Name", "File", "Taxa", "Sites", "Data Type"};
        initTableData();

        // set up table.
        // special features: background shading of rows
        // custom editor allowing only Date column to be edited.
        table = new JTable(tableData, columnData) {
            private static final long serialVersionUID = 1L;

            // method that induces table row shading
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                // even index, selected or not selected
                if (isCellSelected(Index_row, Index_col)) {
                    comp.setBackground(Color.gray);
                } else if (Index_row % 2 == 0 && !isCellSelected(Index_row, Index_col)) {
                    comp.setBackground(new Color(237, 243, 255));
                } else {
                    comp.setBackground(Color.white);
                }
                JComponent jcomp = (JComponent) comp;
                switch (Index_col) {
                    case NAME_COLUMN:
                        jcomp.setToolTipText("Set " + table.getColumnName(Index_col).toLowerCase() + " for this partition");
                        break;
                    case FILE_COLUMN:
                    case TAXA_COLUMN:
                    case SITES_COLUMN:
                    case TYPE_COLUMN:
                        jcomp.setToolTipText("Report " + table.getColumnName(Index_col).toLowerCase() + " for this partition");
                        break;
                    default:
                        jcomp.setToolTipText(null);
                }
                return comp;
            }
        };
        int size = table.getFont().getSize();
        table.setRowHeight(25 * size / 13);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setName("alignmenttable");

        TableColumn col = table.getColumnModel().getColumn(NAME_COLUMN);
        nameEditor = new JTextField();
        nameEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                processPartitionName();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                processPartitionName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                processPartitionName();
            }
        });

        col.setCellEditor(new DefaultCellEditor(nameEditor));

        // // set up editor that makes sure only doubles are accepted as entry
        // // and only the Date column is editable.
        table.setDefaultEditor(Object.class, new TableCellEditor() {
            JTextField m_textField = new JTextField();
            int m_iRow, m_iCol;

            @Override
            public boolean stopCellEditing() {
                //Log.warning.println("stopCellEditing()");
                table.removeEditor();
                String text = m_textField.getText();
                try {
                    Double.parseDouble(text);
                } catch (Exception e) {
                    return false;
                }
                tableData[m_iRow][m_iCol] = text;
                return true;
            }

            @Override
            public boolean isCellEditable(EventObject anEvent) {
                //Log.warning.println("isCellEditable()");
                return table.getSelectedColumn() == 0;
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowNr,
                                                         int colNr) {
                return null;
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                return false;
            }

            @Override
            public void removeCellEditorListener(CellEditorListener l) {
            }

            @Override
            public Object getCellEditorValue() {
                return null;
            }

            @Override
            public void cancelCellEditing() {
            }

            @Override
            public void addCellEditorListener(CellEditorListener l) {
            }

        });

        // show alignment viewer when double clicking a row
        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });

        scrollPane = new JScrollPane(table);

        int rowsToDisplay = 3;
        Dimension d = table.getPreferredSize();
        scrollPane.setPreferredSize(
                new Dimension(d.width, table.getRowHeight() * rowsToDisplay + table.getTableHeader().getHeight()));

        return scrollPane;
    }

    private void processPartitionName() {
    }

    void initTableData() {
        if (tableData == null) {
            tableData = new Object[partitionCount][NR_OF_COLUMNS];
        }
        for (int i = 0; i < partitionCount; i++) {
            Value<Alignment> alignmentValue = alignments.get(i);
            Alignment alignment = alignmentValue.value();

            // partition name
            tableData[i][NAME_COLUMN] = alignmentValue.getId();

            // alignment
                tableData[i][FILE_COLUMN] = alignment;
            // # taxa
            tableData[i][TAXA_COLUMN] = alignment.ntaxa();
            // # sites
            tableData[i][SITES_COLUMN] = alignment.nchar();
            // Data type
            tableData[i][TYPE_COLUMN] = alignment.getSequenceType();
        }
    }
}