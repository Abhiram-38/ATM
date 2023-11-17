import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ATMGUI extends JFrame implements ActionListener {
    private JTextField textField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton withdrawButton;
    private JButton DepositButton;
    private JButton checkBalanceButton;
    private JButton logoutButton;
    private JButton changePinButton;
    private JLabel balanceLabel;

    private JPanel loginPanel;
    private JPanel postLoginPanel;
    private JPanel buttonPanel; 

    // Define lists to store account details.
    private List<String> accountNumbersList = new ArrayList<>();
    private List<String> pinsList = new ArrayList<>();
    private List<Double> accountBalancesList = new ArrayList<>();

    private boolean loggedIn = false;
    private double currentBalance = 0.0;

    public ATMGUI() {
        setTitle("ATM System");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Font font = new Font("Arial", Font.PLAIN, 16);

        loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 2));

        centerPanel.add(new JLabel("Account Number:"));
        textField = new JTextField();
        textField.setFont(font);
        centerPanel.add(textField);

        centerPanel.add(new JLabel("PIN:"));
        passwordField = new JPasswordField();
        passwordField.setFont(font);
        centerPanel.add(passwordField);

        loginPanel.add(centerPanel, BorderLayout.CENTER);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginButton.setFont(font);
        loginPanel.add(loginButton, BorderLayout.SOUTH);

        postLoginPanel = new JPanel();
        postLoginPanel.setLayout(new BorderLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(this);
        withdrawButton.setFont(font);
        addComponentToPanel(buttonPanel, withdrawButton, 0, 0, GridBagConstraints.WEST);

        checkBalanceButton = new JButton("Check Balance");
        checkBalanceButton.addActionListener(this);
        checkBalanceButton.setFont(font);
        addComponentToPanel(buttonPanel, checkBalanceButton, 1, 0, GridBagConstraints.EAST);

        changePinButton = new JButton("Change PIN");
        changePinButton.addActionListener(this);
        changePinButton.setFont(font);
        addComponentToPanel(buttonPanel, changePinButton, 0, 1, GridBagConstraints.WEST);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        logoutButton.setFont(font);
        addComponentToPanel(buttonPanel, logoutButton, 1, 1, GridBagConstraints.EAST);

        DepositButton = new JButton("Deposit");
        DepositButton.addActionListener(this);
        DepositButton.setFont(font);
        addComponentToPanel(buttonPanel, DepositButton, 2, 0, GridBagConstraints.EAST);

        postLoginPanel.add(buttonPanel, BorderLayout.CENTER);

        balanceLabel = new JLabel();
        balanceLabel.setFont(font);
        postLoginPanel.add(balanceLabel, BorderLayout.NORTH);

        // Load account data from CSV on initialization
        loadAccountDataFromCSV();

        // Initially, only show the login panel
        add(loginPanel);
    }

    // Helper method to add a component with GridBagConstraints
    private void addComponentToPanel(JPanel panel, Component component, int gridx, int gridy, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = anchor;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    private void loadAccountDataFromCSV() {
        String csvFilePath = "E:/java project/myproject/src/account_data.csv"; // Replace with your actual path

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 3) {
                    accountNumbersList.add(values[0]);
                    pinsList.add(values[1]);
                    accountBalancesList.add(Double.parseDouble(values[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load account data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void saveAccountDataToCSV() {
        String csvFilePath = "E:/java project/myproject/src/account_data.csv"; // Replace with your actual path
    
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
            for (int i = 0; i < accountNumbersList.size(); i++) {
                bw.write(accountNumbersList.get(i) + "," + pinsList.get(i) + "," + accountBalancesList.get(i));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save account data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to save account data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!loggedIn) {
            if (e.getSource() == loginButton) {
                String accountNumber = textField.getText();
                String pin = new String(passwordField.getPassword());

                int accountIndex = accountNumbersList.indexOf(accountNumber);
                if (accountIndex != -1 && pin.equals(pinsList.get(accountIndex))) {
                    loggedIn = true;
                    currentBalance = accountBalancesList.get(accountIndex);
                    loginPanel.setVisible(false);
                    add(postLoginPanel);
                    balanceLabel.setText("Current Balance: Rs." + currentBalance);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Login", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            if (e.getSource() == withdrawButton) {
                String amountStr = JOptionPane.showInputDialog(this, "Enter Withdrawal Amount:");
                try {
                    double amount = Double.parseDouble(amountStr);
            
                    if (amount > 0 && amount <= currentBalance) {
                        currentBalance -= amount;
                        balanceLabel.setText("Current Balance: Rs." + currentBalance);
            
                        // Update account balance in the list
                        int accountIndex = accountNumbersList.indexOf(textField.getText());
                        if (accountIndex != -1) {
                            accountBalancesList.set(accountIndex, currentBalance);
                        }
            
                        // Save the updated data to the CSV file
                        saveAccountDataToCSV();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Withdrawal Amount", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == checkBalanceButton) {
                balanceLabel.setText("Current Balance: " + currentBalance);
            } else if (e.getSource() == changePinButton) {
                String newPin = JOptionPane.showInputDialog(this, "Enter New PIN:");
                if (newPin != null && !newPin.isEmpty()) {
                    int accountIndex = accountNumbersList.indexOf(textField.getText());
                    if (accountIndex != -1) {
                        // Update the PIN in memory
                        pinsList.set(accountIndex, newPin);
            
                        // Save the updated data to the CSV file
                        saveAccountDataToCSV();
            
                        JOptionPane.showMessageDialog(this, "PIN changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
               
            }else if (e.getSource() == DepositButton) {
                String amountStr = JOptionPane.showInputDialog(this, "Enter Deposit Amount:");
                try {
                    double amount = Double.parseDouble(amountStr);
            
                    
                    currentBalance += amount;
                    balanceLabel.setText("Current Balance: Rs." + currentBalance);
            
                    // Update account balance in the list
                    int accountIndex = accountNumbersList.indexOf(textField.getText());
                    if (accountIndex != -1) {
                        accountBalancesList.set(accountIndex, currentBalance);
                    }
            
                    // Save the updated data to the CSV file
                    saveAccountDataToCSV();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Amount Format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e.getSource() == logoutButton) {
                loggedIn = false;
                currentBalance = 0.0;
                postLoginPanel.setVisible(false);
                loginPanel.setVisible(true);
                textField.setText("");
                passwordField.setText("");
                balanceLabel.setText("");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMGUI atm = new ATMGUI();
            atm.setVisible(true);
        });
    }
}