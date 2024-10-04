import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginWindow() {
        setTitle("Login");
        setSize(300, 120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));

        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = authenticateUser(username, password);

                if (role != null) {
                    switch (role) {
                        case "manager":
                            new ClientTable().setVisible(true);
                            break;
                        case "analytics":
                            new Analytics().setVisible(true);
                            break;
                        case "warehouse":
                            new ProductTable().setVisible(true);
                            break;
                        case "client":
                            new ClientWindow().setVisible(true);
                            break;
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginWindow.this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        loginPanel.add(loginButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        loginPanel.add(exitButton);

        add(loginPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }

    private String authenticateUser(String username, String password) {
        if ("manager".equals(username) && "password".equals(password)) {
            return "manager";
        } else if ("analytics".equals(username) && "password".equals(password)) {
            return "analytics";
        } else if ("warehouse".equals(username) && "password".equals(password)) {
            return "warehouse";
        } else if ("client".equals(username) && "password".equals(password)) {
            return "client";
        }
        return null;
    }
}
