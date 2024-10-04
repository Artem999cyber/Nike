import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.File;


public class ProductTable extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:comp.db";
    private final JTable table;
    private final DefaultTableModel model;

    public ProductTable() {
        setTitle("Products Table for seller");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = DBConnect.loadTableData("computer_parts");
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Установка Comparator для столбца цен
        sorter.setComparator(3, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                double price1 = Double.parseDouble(o1.toString());
                double price2 = Double.parseDouble(o2.toString());
                return Double.compare(price1, price2);
            }
        });

        sorter.setComparator(4, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int price1 = Integer.parseInt(o1.toString());
                int price2 = Integer.parseInt(o2.toString());
                return Integer.compare(price1, price2);
            }
        });

        JPanel buttonPanel = new JPanel();
        Dimension preferredSize = new Dimension(200, 25);


        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                addRecord();
            }
        });
        buttonPanel.add(addButton);


        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                editRecord();
            }
        });
        buttonPanel.add(editButton);


        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                deleteRecord();
            }
        });
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(10));


        JTextField searchField = new JTextField();
        searchField.setPreferredSize(preferredSize);
        buttonPanel.add(searchField);


        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                if (searchText.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });
        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalStrut(10));


        JButton filterPriceButton = new JButton("Filter by Price");
        filterPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel pricePanel = new JPanel(new GridLayout(2, 2));
                pricePanel.add(new JLabel("Min Price:"));
                JTextField minPriceField = new JTextField(10);
                pricePanel.add(minPriceField);
                pricePanel.add(new JLabel("Max Price:"));
                JTextField maxPriceField = new JTextField(10);
                pricePanel.add(maxPriceField);

                int result = JOptionPane.showConfirmDialog(ProductTable.this, pricePanel, "Filter by Price", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    double minPrice = Double.parseDouble(minPriceField.getText());
                    double maxPrice = Double.parseDouble(maxPriceField.getText());

                    RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                            double price = Double.parseDouble(entry.getStringValue(3));
                            return price >= minPrice && price <= maxPrice;
                        }
                    };
                    sorter.setRowFilter(filter);
                }
            }
        });
        buttonPanel.add(filterPriceButton);


        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
                sorter.setSortKeys(sortKeys);
                sorter.sort();
            }
        });
        buttonPanel.add(sortButton);


        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(exitButton);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {

                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {

                        editButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    } else {

                        editButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                    }
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (row >= 0 && column == 6) {
                    String imagePath = (String) table.getValueAt(row, column);
                    if (!imagePath.isEmpty()) {
                        try {
                            ImageIcon icon = new ImageIcon(imagePath);
                            JOptionPane.showMessageDialog(null, icon, "Image Preview", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProductTable().setVisible(true);
        });
    }

    private void addRecord() {

        JFrame addFrame = new JFrame("Add Computer Part");
        addFrame.setSize(280, 400);
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        JTextField partNumberField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JTextField additionalInfoField = new JTextField(20);
        JTextField imageUrlField = new JTextField(20);
        inputPanel.add(new JLabel("Part Number:"));
        inputPanel.add(partNumberField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Year of Manufacture:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Additional Info:"));
        inputPanel.add(additionalInfoField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);


        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(addFrame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageUrlField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(browseButton);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String partNumber = partNumberField.getText();
                String name = nameField.getText();
                String price = priceField.getText();
                String year = yearField.getText();
                String additionalInfo = additionalInfoField.getText();
                String imageUrl = imageUrlField.getText();


                try {
                    Double.parseDouble(price);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addFrame, "Please enter a valid price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                try {
                    Integer.parseInt(year);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addFrame, "Please enter a valid year.", "Invalid Year", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                String sql = "INSERT INTO computer_parts (part_number, name, price, year_of_manufacture, additional_info, image_url) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setString(1, partNumber);
                    pstmt.setString(2, name);
                    pstmt.setDouble(3, Double.parseDouble(price));
                    pstmt.setString(4, year);
                    pstmt.setString(5, additionalInfo);
                    pstmt.setString(6, imageUrl);


                    pstmt.executeUpdate();


                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);


                        model.addRow(new Object[]{id, partNumber, name, price, year, additionalInfo, imageUrl});
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                addFrame.dispose();
            }
        });

        addFrame.add(inputPanel, BorderLayout.CENTER);
        addFrame.add(saveButton, BorderLayout.SOUTH);
        addFrame.setVisible(true);
    }

    private void editRecord() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to edit.");
            return;
        }


        int id = (int) table.getValueAt(selectedRow, 0);
        String partNumber = (String) table.getValueAt(selectedRow, 1);
        String name = (String) table.getValueAt(selectedRow, 2);
        String price = (String) table.getValueAt(selectedRow, 3);
        String year = (String) table.getValueAt(selectedRow, 4);
        String additionalInfo = (String) table.getValueAt(selectedRow, 5);
        String imageUrl = (String) table.getValueAt(selectedRow, 6);


        JFrame editFrame = new JFrame("Edit Computer Part");
        editFrame.setSize(280, 400);
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        JTextField partNumberField = new JTextField(partNumber, 20);
        JTextField nameField = new JTextField(name, 20);
        JTextField priceField = new JTextField(price, 20);
        JTextField yearField = new JTextField(year, 20);
        JTextField additionalInfoField = new JTextField(additionalInfo, 20);
        JTextField imageUrlField = new JTextField(imageUrl, 20);
        inputPanel.add(new JLabel("Part Number:"));
        inputPanel.add(partNumberField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Year of Manufacture:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Additional Info:"));
        inputPanel.add(additionalInfoField);
        inputPanel.add(new JLabel("Image URL:"));
        inputPanel.add(imageUrlField);


        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(editFrame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageUrlField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        inputPanel.add(browseButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newPartNumber = partNumberField.getText();
                String newName = nameField.getText();
                String newPrice = priceField.getText();
                String newYear = yearField.getText();
                String newAdditionalInfo = additionalInfoField.getText();
                String newImageUrl = imageUrlField.getText();


                try {
                    Double.parseDouble(newPrice);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editFrame, "Please enter a valid price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                try {
                    Integer.parseInt(newYear);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editFrame, "Please enter a valid year.", "Invalid Year", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                String sql = "UPDATE computer_parts SET part_number = ?, name = ?, price = ?, year_of_manufacture = ?, additional_info = ?, image_url = ? WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setString(1, newPartNumber);
                    pstmt.setString(2, newName);
                    pstmt.setDouble(3, Double.parseDouble(newPrice));
                    pstmt.setString(4, newYear);
                    pstmt.setString(5, newAdditionalInfo);
                    pstmt.setString(6, newImageUrl);
                    pstmt.setInt(7, id);


                    pstmt.executeUpdate();


                    model.setValueAt(newPartNumber, selectedRow, 1);
                    model.setValueAt(newName, selectedRow, 2);
                    model.setValueAt(newPrice, selectedRow, 3);
                    model.setValueAt(newYear, selectedRow, 4);
                    model.setValueAt(newAdditionalInfo, selectedRow, 5);
                    model.setValueAt(newImageUrl, selectedRow, 6);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                editFrame.dispose();
            }
        });

        editFrame.add(inputPanel, BorderLayout.CENTER);
        editFrame.add(saveButton, BorderLayout.SOUTH);
        editFrame.setVisible(true);
    }

    private void deleteRecord() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {

            int id = (int) table.getValueAt(selectedRow, 0);


            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                String sql = "DELETE FROM computer_parts WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setInt(1, id);


                    pstmt.executeUpdate();


                    model.removeRow(selectedRow);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
