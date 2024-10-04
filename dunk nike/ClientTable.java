import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClientTable extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:comp.db";
    private final JTable table;
    private final DefaultTableModel model;

    public ClientTable() {
        setTitle("Clients Table");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = DBConnect.loadTableData("clients");
        table = new JTable(model);

        for (int i = 0; i < model.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellEditor(null);
        }


        add(new JScrollPane(table), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

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


        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int columnIndexToSort = 2;

                List<RowSorter.SortKey> sortKeys = new ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.ASCENDING));
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

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientTable().setVisible(true);
        });
    }

    private void addRecord() {

        JFrame addFrame = new JFrame("Add Record");
        addFrame.setSize(280, 280);
        addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addFrame.setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        JTextField orderNumbersField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField additionalInfoField = new JTextField(20);
        inputPanel.add(new JLabel("Order numbers:"));
        inputPanel.add(orderNumbersField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Additional info:"));
        inputPanel.add(additionalInfoField);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String internalNumber = orderNumbersField.getText();
                String name = nameField.getText();
                String phone = phoneField.getText();
                String preferredOrder = additionalInfoField.getText();


                String sql = "INSERT INTO clients (order_number, name, phone, additional_info) VALUES (?, ?, ?, ?)";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setString(1, internalNumber);
                    pstmt.setString(2, name);
                    pstmt.setString(3, phone);
                    pstmt.setString(4, preferredOrder);


                    pstmt.executeUpdate();


                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);


                        model.addRow(new Object[]{id, internalNumber, name, phone, preferredOrder});
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
        String internalNumber = (String) table.getValueAt(selectedRow, 1);
        String name = (String) table.getValueAt(selectedRow, 2);
        String phone = (String) table.getValueAt(selectedRow, 3);
        String preferredOrder = (String) table.getValueAt(selectedRow, 4);


        JFrame editFrame = new JFrame("Edit Record");
        editFrame.setSize(280, 280);
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        JTextField orderNumbersField = new JTextField(internalNumber, 20);
        JTextField nameField = new JTextField(name, 20);
        JTextField phoneField = new JTextField(phone, 20);
        JTextField additionalInfoField = new JTextField(preferredOrder, 20);
        inputPanel.add(new JLabel("Order numbers:"));
        inputPanel.add(orderNumbersField);
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Additional info:"));
        inputPanel.add(additionalInfoField);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String newInternalNumber = orderNumbersField.getText();
                String newName = nameField.getText();
                String newPhone = phoneField.getText();
                String newPreferredOrder = additionalInfoField.getText();


                String sql = "UPDATE clients SET order_number = ?, name = ?, phone = ?, additional_info = ? WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setString(1, newInternalNumber);
                    pstmt.setString(2, newName);
                    pstmt.setString(3, newPhone);
                    pstmt.setString(4, newPreferredOrder);
                    pstmt.setInt(5, id);


                    pstmt.executeUpdate();


                    model.setValueAt(newInternalNumber, selectedRow, 1);
                    model.setValueAt(newName, selectedRow, 2);
                    model.setValueAt(newPhone, selectedRow, 3);
                    model.setValueAt(newPreferredOrder, selectedRow, 4);

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

                String sql = "DELETE FROM clients WHERE id = ?";

                try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {


                    pstmt.setString(1, String.valueOf(id));


                    pstmt.executeUpdate();


                    model.removeRow(selectedRow);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
