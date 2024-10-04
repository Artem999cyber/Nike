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


public class ClientWindow extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:comp.db";
    private final JTable table;
    private final DefaultTableModel model;

    public ClientWindow() {
        setTitle("Products Table for client");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        model = DBConnect.loadTableData("computer_parts");
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

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


        JButton addClientButton = new JButton("Buy");
        addClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel clientPanel = new JPanel(new GridLayout(4, 2));
                clientPanel.add(new JLabel("Name:"));
                JTextField nameField = new JTextField(10);
                clientPanel.add(nameField);
                clientPanel.add(new JLabel("Phone:"));
                JTextField phoneField = new JTextField(10);
                clientPanel.add(phoneField);
                clientPanel.add(new JLabel("Additional info:"));
                JTextField additionalInfoField = new JTextField(10);
                clientPanel.add(additionalInfoField);

                int result = JOptionPane.showConfirmDialog(ClientWindow.this, clientPanel, "Add Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    String phone = phoneField.getText();
                    String additionalInfo = additionalInfoField.getText();

                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String orderNumber = (String) table.getValueAt(selectedRow, 1);

                        try (Connection conn = DriverManager.getConnection(DB_URL);
                             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO clients (order_number, name, phone, additional_info) VALUES (?, ?, ?, ?)")) {
                            pstmt.setString(1, orderNumber);
                            pstmt.setString(2, name);
                            pstmt.setString(3, phone);
                            pstmt.setString(4, additionalInfo);
                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(ClientWindow.this, "A record of your order has been entered into the database", "Successfully", JOptionPane.INFORMATION_MESSAGE);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(ClientWindow.this, "Please select a row from the table.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        buttonPanel.add(addClientButton);
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

                int result = JOptionPane.showConfirmDialog(ClientWindow.this, pricePanel, "Filter by Price", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    double minPrice = Double.parseDouble(minPriceField.getText());
                    double maxPrice = Double.parseDouble(maxPriceField.getText());

                    RowFilter<DefaultTableModel, Object> filter = new RowFilter<DefaultTableModel, Object>() {
                        @Override
                        public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Object> entry) {
                            double price = Double.parseDouble(entry.getStringValue(3)); // Предполагается, что цена находится в 4-м столбце
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
            new ClientWindow().setVisible(true);
        });
    }

}
