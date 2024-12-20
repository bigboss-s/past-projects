import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WarehouseGUI extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JTable deliveryTable;
    private DefaultTableModel deliveryTableModel;
    private JTable rightMerchandiseTable;
    private DefaultTableModel rightMerchandiseTableModel;
    private JComboBox<String> inventoryCategoryComboBox;

    public WarehouseGUI() {
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
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel();
        JLabel itemDetailLabel = new JLabel();

        inventoryCategoryComboBox = new JComboBox<>(getCategoryStringArray());

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchField);
        searchPanel.add(inventoryCategoryComboBox);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

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

        searchButton.addActionListener(e -> {
            RowFilter<Object, Object> termFilter = RowFilter.regexFilter("(?i)" + searchField.getText().toLowerCase(), inventoryTable.getColumnModel().getColumnIndex("Name"));
            RowFilter<Object, Object> categoryFilter = RowFilter.regexFilter("(?i)" + (String) inventoryCategoryComboBox.getSelectedItem(), inventoryTable.getColumnModel().getColumnIndex("Category"));
            List<RowFilter<Object, Object>> regexList = new ArrayList<>();
            regexList.add(termFilter);
            regexList.add(categoryFilter);
            rowSorter.setRowFilter(RowFilter.andFilter(regexList));
        });

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewMerchandiseDialog("New Merchandise entry");
            }
        });

        searchPanel.add(addButton);

        JButton editButton = new JButton("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow != -1) {
                String merchName = (String) inventoryTable.getValueAt(selectedRow, inventoryTable.getColumnModel().getColumnIndex("Name"));
                updateMerchandiseDialog(Merchandise.getByName(merchName));
            }
        });

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editButton.setEnabled(inventoryTable.getSelectedRow() != -1);
            }
            int selectedRow = inventoryTable.getSelectedRow();
            String merchName = (String) inventoryTable.getValueAt(selectedRow, inventoryTable.getColumnModel().getColumnIndex("Name"));
            itemDetailLabel.setText(Merchandise.getByName(merchName).getDetailsFormatted());
        });

        searchPanel.add(editButton);

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

        JScrollPane scrollPane = new JScrollPane(inventoryTable);

        JSplitPane inventorySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, itemDetailLabel);
        inventorySplitPane.setResizeWeight(0.75);
        inventorySplitPane.setDividerSize(5);

        panel.add(inventorySplitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Delivery ID", "Delivered/Shipped Date", "Origin/Destination", "Status"};
        deliveryTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deliveryTable = new JTable(deliveryTableModel);

        JScrollPane leftScrollPane = new JScrollPane(deliveryTable);
        panel.add(leftScrollPane, BorderLayout.CENTER);

        String[] rightColumnNames = {"Product ID", "Name", "Quantity"};
        rightMerchandiseTableModel = new DefaultTableModel(rightColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        rightMerchandiseTable = new JTable(rightMerchandiseTableModel);

        JPanel searchPanel = new JPanel();
        String[] dateStrings = Delivery.getDeliveryDateStrings();
        String[] strings = new String[dateStrings.length + 1];
        System.arraycopy(dateStrings, 0, strings, 1, dateStrings.length);
        strings[0] = "";

        JComboBox<String> dateComboBox = new JComboBox<>(strings);

        JButton searchButton = new JButton("Search");

        searchPanel.add(dateComboBox);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        JLabel rejectionLabel = new JLabel();

//        deliveryTable.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                rightMerchandiseTableModel.getDataVector().removeAllElements();
//                rightMerchandiseTableModel.fireTableDataChanged();
//                int row = deliveryTable.rowAtPoint(e.getPoint());
//                if (row >= 0) {
//                    String id = (String) deliveryTableModel.getValueAt(row, 0);
//                    Delivery delivery = Delivery.getDeliveries().stream().filter(d -> d.packageId.equals(id)).findFirst().orElse(null);
//                    for (Transport t : Transport.getTransports()) {
//                        if (t.delivery == delivery) {
//                            rightMerchandiseTableModel.addRow(new Object[]{t.merchandise.getId(), t.merchandise.getName(), t.quantity});
//                            if (t.delivery instanceof InboundDeliveryUndelivered) {
//                                if (((InboundDeliveryUndelivered) t.delivery).isRejected()) {
//                                    rejectionLabel.setText(((InboundDeliveryUndelivered) t.delivery).getRejectionFormString());
//                                } else {
//                                    rejectionLabel.setText("");
//                                }
//                            }
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//            }
//        });

        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(deliveryTableModel);
        deliveryTable.setRowSorter(rowSorter);


        searchButton.addActionListener(e -> {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + (String) dateComboBox.getSelectedItem(), deliveryTable.getColumnModel().getColumnIndex("Delivered/Shipped Date")));
        });


        JScrollPane rightScrollPane = new JScrollPane(rightMerchandiseTable);

        JSplitPane detailSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScrollPane, rejectionLabel);
        detailSplitPane.setDividerSize(5);
        detailSplitPane.setResizeWeight(0);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, detailSplitPane);
        splitPane.setDividerSize(5);
        splitPane.setResizeWeight(0.75);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    public void addInventoryRow(Object[] row) {
        inventoryTableModel.addRow(row);
    }

    public void addDeliveryRow(Object[] row) {
        deliveryTableModel.addRow(row);
    }

    public void addNewMerchandiseDialog(String title) {
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

        String[] categoryString = Category.getCategoryStrings();
        String[] newCategoryString = new String[categoryString.length+1];
        System.arraycopy(categoryString, 0, newCategoryString, 0, categoryString.length);
        newCategoryString[categoryString.length] = "New...";

        JComboBox<String> categoryComboBox = new JComboBox<>(newCategoryString);
        JTextField newCategoryField = new JTextField("New category...", 20);
        newCategoryField.setEnabled(false);
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        categoryPanel.add(newCategoryField, BorderLayout.SOUTH);
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JCheckBox descriptionCheckBox = new JCheckBox("Add Description?");
        panel.add(descriptionCheckBox, gbc);

        JTextField descriptionField = new JTextField(20);
        descriptionField.setEnabled(false); // Initially disabled
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        descriptionCheckBox.addActionListener(e -> {
            descriptionField.setEnabled(descriptionCheckBox.isSelected());
        });

        categoryComboBox.addActionListener(e -> {
            if (Objects.equals(categoryComboBox.getSelectedItem(), "New...")) {
                newCategoryField.setEnabled(true);
            } else {
                newCategoryField.setEnabled(false);
            }
            panel.revalidate();
            panel.repaint();
        });

        do {
            int result = JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String weightStr = weightField.getText();
                String rowStr = rowField.getText();
                String columnStr = columnField.getText();
                String quantityStr = quantityField.getText();
                String category = Objects.equals(categoryComboBox.getSelectedItem(), "New...") ? newCategoryField.getText() : (String) categoryComboBox.getSelectedItem();
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
                        boolean isNewCategory = !Arrays.asList(getCategoryStringArray()).contains(category);
                        Merchandise merchandise = new Merchandise(name, Float.parseFloat(weightStr), Integer.parseInt(rowStr), Integer.parseInt(columnStr), Integer.parseInt(quantityStr));
                        if (isNewCategory) {
                            Category cat = new Category(category);
                            merchandise.setCategory(cat);
                        } else {
                            merchandise.setCategory(Category.getByName(category));
                        }
                        if (descriptionCheckBox.isSelected()) {
                            merchandise.setDescription(description);
                        }

                        inventoryTableModel.getDataVector().removeAllElements();
                        for (Merchandise merch : Merchandise.getMerchandise()) {
                            addInventoryRow(merch.getFormatToRow());
                        }
                        inventoryCategoryComboBox.removeAllItems();
                        for (String str : getCategoryStringArray()) {
                            inventoryCategoryComboBox.addItem(str);
                        }
                        inventoryTableModel.fireTableDataChanged();
                        JOptionPane.showMessageDialog(panel, "Entry successful");

                        if (categoryComboBox.getSelectedItem().equals("New...")) {
                            categoryComboBox.insertItemAt(category, categoryComboBox.getItemCount() - 1);
                            categoryComboBox.setSelectedItem(category);
                        }
                        break;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (true);

    }

    private void updateMerchandiseDialog(Merchandise merchandise){
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(merchandise.getName(), 20);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel weightLabel = new JLabel("Weight:");
        JTextField weightField = new JTextField(String.valueOf(merchandise.getWeight()), 20);
        panel.add(weightLabel, gbc);
        gbc.gridx = 1;
        panel.add(weightField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel rowLabel = new JLabel("Row:");
        JTextField rowField = new JTextField(String.valueOf(merchandise.getRow()),20);
        panel.add(rowLabel, gbc);
        gbc.gridx = 1;
        panel.add(rowField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel columnLabel = new JLabel("Column:");
        JTextField columnField = new JTextField(String.valueOf(merchandise.getColumn()),20);
        panel.add(columnLabel, gbc);
        gbc.gridx = 1;
        panel.add(columnField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(String.valueOf(merchandise.getQuantity()), 20);
        panel.add(quantityLabel, gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel categoryLabel = new JLabel("Category:");

        String[] categoryString = Category.getCategoryStrings();
        String[] newCategoryString = new String[categoryString.length+1];
        System.arraycopy(categoryString, 0, newCategoryString, 0, categoryString.length);
        newCategoryString[categoryString.length] = "New...";

        JComboBox<String> categoryComboBox = new JComboBox<>(newCategoryString);
        JTextField newCategoryField = new JTextField("New category...", 20);
        newCategoryField.setEnabled(false);
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.add(categoryComboBox, BorderLayout.CENTER);
        categoryPanel.add(newCategoryField, BorderLayout.SOUTH);
        panel.add(categoryLabel, gbc);
        gbc.gridx = 1;
        panel.add(categoryPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descriptionLabel = new JLabel("Description");
        panel.add(descriptionLabel, gbc);

        JTextField descriptionField = new JTextField(merchandise.getDescription(), 20);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (categoryComboBox.getSelectedItem().equals("New...")) {
                    newCategoryField.setEnabled(true);
                    panel.revalidate();
                    panel.repaint();
                } else {
                    newCategoryField.setEnabled(false);
                }
            }
        });

        do {
            int result = JOptionPane.showConfirmDialog(null, panel, "Edit merchandise: "+merchandise.getName(), JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String weightStr = weightField.getText();
                String rowStr = rowField.getText();
                String columnStr = columnField.getText();
                String quantityStr = quantityField.getText();
                String category = Objects.equals(categoryComboBox.getSelectedItem(), "New...") ? newCategoryField.getText() : (String) categoryComboBox.getSelectedItem();
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
                        boolean isNewCategory = false;
                        if (!Arrays.stream(Category.getCategoryStrings()).anyMatch(c -> c.equals(category))){
                            isNewCategory = true;
                        }
                        merchandise.editMerchandise(name, Float.parseFloat(weightStr), Integer.parseInt(rowStr), Integer.parseInt(columnStr), Integer.parseInt(quantityStr));
                        if (isNewCategory){
                            Category cat = new Category(category);
                            merchandise.setCategory(cat);
                        } else {
                            merchandise.setCategory(Category.getByName(category));
                        }
                        merchandise.setDescription(description);
                        new Update(LocalDate.now(), merchandise, (WarehouseWorker) Utils.getCurrentUser());
                        inventoryTableModel.getDataVector().removeAllElements();
                        for (Merchandise merch : Merchandise.getMerchandise()) {
                            addInventoryRow(merch.getFormatToRow());
                        }
                        inventoryCategoryComboBox.removeAllItems();
                        for (String str : getCategoryStringArray()) {
                            inventoryCategoryComboBox.addItem(str);
                        }
                        inventoryTableModel.fireTableDataChanged();
                        JOptionPane.showMessageDialog(panel, "Edit successful");

                        if (categoryComboBox.getSelectedItem().equals("New...")) {
                            categoryComboBox.insertItemAt(category, categoryComboBox.getItemCount() - 1);
                            categoryComboBox.setSelectedItem(category);
                        }
                        break;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, errorMessage.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        } while (true);
    }

    private String[] getCategoryStringArray() {
        String[] catStrings = Category.getCategoryStrings();
        String[] strings = new String[catStrings.length + 1];
        System.arraycopy(catStrings, 0, strings, 1, catStrings.length);
        strings[0] = "";
        return strings;
    }
}
