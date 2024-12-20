import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class WarehouseManagementGUI extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JTable deliveryTable;
    private DefaultTableModel deliveryTableModel;
    private JTable deliveryInventoryTable;
    private DefaultTableModel deliveryInventoryTableModel;
    private JComboBox<String> inventoryCategoryComboBox;

    public WarehouseManagementGUI() {
        setTitle("Inventory Management");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel inventoryPanel = createInventoryPanel();
        tabbedPane.addTab("Inventory", inventoryPanel);

        JPanel deliveryPanel = createDeliveryPanel();
        tabbedPane.addTab("Inbound Deliveries", deliveryPanel);

        add(tabbedPane);

        JMenuBar jMenuBar = new JMenuBar();
        JMenuItem createSampleData = new JMenuItem("Create sample data");
        createSampleData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    Utils.createSampleData();
                    JOptionPane.showMessageDialog(null, "Sample data created, please restart the app");
                    dispatchEvent(new WindowEvent(WarehouseManagementGUI.this, WindowEvent.WINDOW_CLOSING));
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jMenuBar.add(createSampleData);
        setJMenuBar(jMenuBar);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Utils.saveExtent();
            }
        });
    }

    public JPanel createInventoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel upperPanel = new JPanel();
        JPanel detailPanel = new JPanel();

        JLabel itemDetailLabel = new JLabel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        editButton.setEnabled(false);

        String[] columnNames = {"Product ID", "Name", "Category", "Row/Column", "Quantity", "Weight", "Description"};
        inventoryTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryTableModel);

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(inventoryTableModel);
        inventoryTable.setRowSorter(rowSorter);

        String[] strings = Utils.addEmptyString(Category.getCategoryStrings());
        inventoryCategoryComboBox = new JComboBox<>(strings);

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow < 0) {
                editButton.setEnabled(false);
                return;
            }
            String merchName = (String) inventoryTable.getValueAt(selectedRow, inventoryTable.getColumnModel().getColumnIndex("Name"));
            itemDetailLabel.setText(Merchandise.getByName(merchName).getDetailsFormatted());
            editButton.setEnabled(true);
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createMerchandiseDialog();
            }
        });

        searchButton.addActionListener(e -> {
            RowFilter<Object, Object> termFilter = RowFilter.regexFilter("(?i)" + searchField.getText().toLowerCase(), inventoryTable.getColumnModel().getColumnIndex("Name"));
            RowFilter<Object, Object> categoryFilter = RowFilter.regexFilter("(?i)" + (String) inventoryCategoryComboBox.getSelectedItem(), inventoryTable.getColumnModel().getColumnIndex("Category"));
            List<RowFilter<Object, Object>> regexList = new ArrayList<>();
            regexList.add(termFilter);
            regexList.add(categoryFilter);
            rowSorter.setRowFilter(RowFilter.andFilter(regexList));
        });

        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                String merchName = (String) inventoryTable.getValueAt(selectedRow, inventoryTable.getColumnModel().getColumnIndex("Name"));
                createMerchandiseDialog(Merchandise.getByName(merchName));
            }
        });

        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        upperPanel.add(searchField);
        upperPanel.add(inventoryCategoryComboBox);
        upperPanel.add(searchButton);
        upperPanel.add(new JSeparator());
        upperPanel.add(addButton);
        upperPanel.add(editButton);

        detailPanel.add(itemDetailLabel);

        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

        JSplitPane inventorySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inventoryScrollPane, detailPanel);
        inventorySplitPane.setResizeWeight(0.6);
        inventorySplitPane.setDividerSize(5);

        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(inventorySplitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createDeliveryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        JPanel detailsPanel = new JPanel();

        JComboBox<String> dateComboBox = new JComboBox<>(Utils.addEmptyString(Delivery.getDeliveryDateStrings()));

        JButton searchButton = new JButton("Search");
        JLabel detailsLabel = new JLabel();

        String[] deliveryColNames = {"Delivery ID", "Delivered/Shipped Date", "Origin/Destination", "Status"};
        deliveryTableModel = new DefaultTableModel(deliveryColNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deliveryTable = new JTable(deliveryTableModel);

        String[] inventoryColNames = {"Product ID", "Name", "Quantity", "Weight"};
        deliveryInventoryTableModel = new DefaultTableModel(inventoryColNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deliveryInventoryTable = new JTable(deliveryInventoryTableModel);

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(deliveryTableModel);
        deliveryTable.setRowSorter(rowSorter);

        deliveryTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                deliveryInventoryTableModel.getDataVector().removeAllElements();
                deliveryInventoryTableModel.fireTableDataChanged();
                int row = deliveryTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String id = (String) deliveryTableModel.getValueAt(row, 0);
                    Delivery delivery = Delivery.getDeliveries().stream().filter(d -> d.packageId.equals(id)).findFirst().orElse(null);
                    for (Transport t : Transport.getTransports()) {
                        if (t.delivery == delivery) {
                            deliveryInventoryTableModel.addRow(t.getFormatToRow());
                            detailsLabel.setText(t.delivery.getDetailsFormatted());
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        searchButton.addActionListener(e -> {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + (String) dateComboBox.getSelectedItem(), deliveryTable.getColumnModel().getColumnIndex("Delivered/Shipped Date")));
        });

        searchPanel.add(dateComboBox);
        searchPanel.add(searchButton);

        detailsPanel.add(detailsLabel);

        JScrollPane deliveryScrollPane = new JScrollPane(deliveryTable);
        JScrollPane inventoryScrollPane = new JScrollPane(deliveryInventoryTable); // Add JScrollPane here
        JScrollPane detailsScrollPane = new JScrollPane(detailsLabel);

        JSplitPane detailsSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inventoryScrollPane, detailsScrollPane); // Use inventoryScrollPane
        detailsSplitPane.setResizeWeight(0.5);
        detailsSplitPane.setDividerSize(5);

        JSplitPane deliveryInventorySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, deliveryScrollPane, detailsSplitPane);
        deliveryInventorySplitPane.setDividerSize(5);
        deliveryInventorySplitPane.setResizeWeight(0.6);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(deliveryInventorySplitPane, BorderLayout.CENTER);

        return mainPanel;
    }


    private void createMerchandiseDialog() {
        createMerchandiseDialog(null);
    }

    private void createMerchandiseDialog(Merchandise merchandise) {
        boolean isEdit = (merchandise != null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel weightLabel = new JLabel("Weight:");
        JTextField weightField = new JTextField(20);
        panel.add(weightLabel, gbc);
        gbc.gridx = 1;
        panel.add(weightField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel rowLabel = new JLabel("Row:");
        JTextField rowField = new JTextField(20);
        panel.add(rowLabel, gbc);
        gbc.gridx = 1;
        panel.add(rowField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel columnLabel = new JLabel("Column:");
        JTextField columnField = new JTextField(20);
        panel.add(columnLabel, gbc);
        gbc.gridx = 1;
        panel.add(columnField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(20);
        panel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");
        panel.add(categoryLabel, gbc);

        gbc.gridx++;
        JPanel categoryPanel = new JPanel(new BorderLayout());
        JComboBox<String> categoryComboBox = new JComboBox<>(Utils.appendString(Category.getCategoryStrings(), "New category..."));
        JTextField newCategoryField = new JTextField("Input new category...", 20);
        newCategoryField.setEnabled(false);

        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        categoryPanel.add(newCategoryField, BorderLayout.SOUTH);
        panel.add(categoryPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        categoryComboBox.addActionListener(e -> {
            if (Objects.equals(categoryComboBox.getSelectedItem(), "New category...")) {
                newCategoryField.setEnabled(true);
                newCategoryField.setText("");
            } else {
                newCategoryField.setText("New category...");
                newCategoryField.setEnabled(false);
            }
            panel.revalidate();
            panel.repaint();
        });

        String title;
        JTextField descriptionField;

        JLabel noteLabel = new JLabel("Notes");

        List<JTextArea> notes = new ArrayList<>();

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        JButton addNoteButton = new JButton("Add note");
        addNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNoteToDialog("", notesPanel, notes);
                panel.revalidate();
                panel.repaint();
                Window window = SwingUtilities.getWindowAncestor(panel);
                if (window instanceof JFrame) {
                    ((JFrame) window).pack();
                } else if (window instanceof JDialog) {
                    ((JDialog) window).pack();
                }
            }
        });

        if (isEdit) {
            nameField.setText(merchandise.getName());
            weightField.setText(String.valueOf(merchandise.getWeight()));
            rowField.setText(String.valueOf(merchandise.getRow()));
            columnField.setText(String.valueOf(merchandise.getColumn()));
            quantityField.setText(String.valueOf(merchandise.getQuantity()));

            JLabel descriptionLabel = new JLabel("Description");
            panel.add(descriptionLabel, gbc);

            gbc.gridx = 1;
            descriptionField = new JTextField(merchandise.getDescription(), 20);
            panel.add(descriptionField, gbc);

            for (String noteStr : merchandise.getNotes()){
                addNoteToDialog(noteStr, notesPanel, notes);
            }

            title = "Edit merchandise " + merchandise.getName();
        } else {
            JCheckBox descriptionCheckBox = new JCheckBox("Add Description?");
            panel.add(descriptionCheckBox, gbc);

            gbc.gridx = 1;
            descriptionField = new JTextField(20);
            descriptionField.setEnabled(false);
            panel.add(descriptionField, gbc);

            descriptionCheckBox.addActionListener(e -> {
                descriptionField.setEnabled(descriptionCheckBox.isSelected());
            });

            title = "Add merchandise";
        }

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(noteLabel, gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        panel.add(notesPanel, gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        panel.add(addNoteButton, gbc);

        do {
            int result = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String weightStr = weightField.getText();
                String rowStr = rowField.getText();
                String columnStr = columnField.getText();
                String quantityStr = quantityField.getText();
                String category = Objects.equals(categoryComboBox.getSelectedItem(), "New category...") ? newCategoryField.getText() : (String) categoryComboBox.getSelectedItem();
                String description = descriptionField.getText();

                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder();

                if (name.isEmpty()) {
                    errorMessage.append("Name cannot be empty.\n");
                    isValid = false;
                }

                try {
                    float weight = Float.parseFloat(weightStr);
                    if (weight < 0) {
                        errorMessage.append("Weight cannot be less than 0.\n");
                        isValid = false;
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Weight must be a valid float.\n");
                    isValid = false;
                }

                try {
                    int row = Integer.parseInt(rowStr);
                    if (row < 0) {
                        errorMessage.append("Row cannot be less than 0.\n");
                        isValid = false;
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Row must be a valid integer.\n");
                    isValid = false;
                }

                try {
                    int column = Integer.parseInt(columnStr);
                    if (column < 0) {
                        errorMessage.append("Column cannot be less than 0.\n");
                        isValid = false;
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Column must be a valid integer.\n");
                    isValid = false;
                }

                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity < 0) {
                        errorMessage.append("Quantity cannot be less than 0.\n");
                        isValid = false;
                    }
                } catch (NumberFormatException ex) {
                    errorMessage.append("Quantity must be a valid integer.\n");
                    isValid = false;
                }

                if (category.isEmpty()) {
                    errorMessage.append("Category cannot be empty.\n");
                    isValid = false;
                }

                if (isValid) {
                    try {
                        boolean isNewCategory = !Arrays.asList(Category.getCategoryStrings()).contains(category);

                        if (isEdit) {
                            merchandise.editMerchandise(name, Float.parseFloat(weightStr), Integer.parseInt(rowStr), Integer.parseInt(columnStr), Integer.parseInt(quantityStr));

                        } else {
                            merchandise = new Merchandise(name, Float.parseFloat(weightStr), Integer.parseInt(rowStr), Integer.parseInt(columnStr), Integer.parseInt(quantityStr));
                        }

                        if (isNewCategory) {
                            Category newCategory = new Category(category);
                            merchandise.setCategory(newCategory);
                        } else {
                            merchandise.setCategory(Category.getByName(category));
                        }
                        merchandise.setDescription(description);

                        List<String> newNotes = new ArrayList<>();
                        for (JTextArea note : notes){
                            String str = note.getText();
                            if (!str.isEmpty()){
                                newNotes.add(str);
                            }
                        }
                        merchandise.updateNotes(newNotes);

                        inventoryTableModel.getDataVector().removeAllElements();
                        for (Merchandise merch : Merchandise.getMerchandise()) {
                            addInventoryRow(merch.getFormatToRow());
                        }
                        inventoryCategoryComboBox.removeAllItems();
                        for (String str : Utils.addEmptyString(Category.getCategoryStrings())) {
                            inventoryCategoryComboBox.addItem(str);
                        }
                        inventoryTableModel.fireTableDataChanged();

                        JOptionPane.showMessageDialog(panel, isEdit ? "Edit for "+merchandise.getName()+" successful" : "Entry "+merchandise.getName()+" successful");
                        break;

                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "An error occurred:\n "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (true);
    }

    private void addNoteToDialog(String note, JPanel notesPanel, List<JTextArea> notesList) {
        JTextArea noteArea = new JTextArea(note, 5, 20);
        notesList.add(noteArea);
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        notesPanel.add(noteScrollPane);
    }
    public void addInventoryRow(Object[] row) {
        inventoryTableModel.addRow(row);
    }

    public void addDeliveryRow(Object[] row) {
        deliveryTableModel.addRow(row);
    }
}
