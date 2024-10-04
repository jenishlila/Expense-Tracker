import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.Statement;

// Expense class to represent each expense
class Expense {
    private double amount;
    private String category;
    private String description;

    public Expense(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Amount: $" + amount + " | Category: " + category + " | Description: " + description;
    }
}

public class ExpenseTrackerGUI extends JFrame {
    private ArrayList<Expense> expenses;
    private DefaultTableModel tableModel;

    // GUI Components
    private JTextField amountField;
    private JTextField categoryField;
    private JTextField descriptionField;
    private JTable expenseTable;
    private JLabel totalLabel;
    private Connection connection;

    public ExpenseTrackerGUI() {
        expenses = new ArrayList<>();
        initUI();
    }

    // Method to initialize the User Interface
    private void initUI() {
        
        // Create the main frame
        setTitle("Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for adding expenses
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Fields to input expense details
        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JButton addButton = new JButton("Add Expense");
        inputPanel.add(addButton);

        totalLabel = new JLabel("Total Expenses: $0");
        inputPanel.add(totalLabel);

        // Table to display expenses
        String[] columnNames = {"Amount", "Category", "Description", "Update", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(expenseTable);

        // Add action listener to "Add Expense" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        // Layout the main frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setVisible(true);
        showValues();
    }
    public void connectToDatabase() {
        try {
            // SQLite connection string
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java","root", "");
            System.out.println("Connected to SQLite database.");
            // Statement statement = null;
            // ResultSet resultSet = null;

            // statement = connection.createStatement();
            // resultSet = statement.executeQuery("SELECT * FROM expenses");
            // System.out.println("here");
            // resultSet.next();
            // System.out.println(resultSet.getString("amount"));
            // System.out.println(amountField.getText());
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to add an expense
    private void addExpense() {
        try {

            // Retrieve inputs from text fields
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String description = descriptionField.getText();

            // Add expense to list
            Expense expense = new Expense(amount, category, description);
            expenses.add(expense);

            // Update table
            tableModel.addRow(new Object[]{amount, category, description});
            // System.out.println(expense.toString());
            // Clear fields after input
            connectToDatabase();
            amountField.setText("");
            categoryField.setText("");
            descriptionField.setText("");

            // Update total label
            updateTotal();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to calculate and update the total expenses
    private void updateTotal() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        totalLabel.setText("Total Expenses: $" + total);
    }

    public static void main(String[] args) {
        // Run the Expense Tracker GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExpenseTrackerGUI();
            }
        });
    }
    private void showValues(){
        try{
            connectToDatabase();
            Statement statement = null;
            ResultSet resultSet = null;
            double total = 0;

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM expenses");
            
            while(resultSet.next()){
                total += Double.valueOf(resultSet.getInt("amount"));
                tableModel.addRow(new Object[]{resultSet.getString("amount"), resultSet.getString("category"), resultSet.getString("description")});
            }
            totalLabel.setText("Total Expenses: $" + total);
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
