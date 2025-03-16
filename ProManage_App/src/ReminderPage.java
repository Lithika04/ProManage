import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import database.MongoDBConnection;

public class ReminderPage extends JFrame {
    private DefaultTableModel tableModel;
    private JTable reminderTable;
    private JTextField dateField, descriptionField;
    private MongoCollection<Document> reminderCollection;

    public ReminderPage() {
        setTitle("Reminders - ProManage");

        // Enable Full-Screen but allow Fixed Restore Size
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Connect to MongoDB Collection
        reminderCollection = MongoDBConnection.getCollection("reminders");

        // Header Panel (Blue Theme)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel headerLabel = new JLabel("Reminders", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Input Panel (Styled Like Contact Page)
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add a New Reminder"));

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 18));
        dateField.setPreferredSize(new Dimension(400, 50));

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 18));
        descriptionField = new JTextField();
        descriptionField.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionField.setPreferredSize(new Dimension(400, 50));

        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(descLabel);
        inputPanel.add(descriptionField);

        // Table for Reminders
        tableModel = new DefaultTableModel(new String[]{"Date", "Description"}, 0);
        reminderTable = new JTable(tableModel);
        reminderTable.setFont(new Font("Arial", Font.PLAIN, 18));
        reminderTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(reminderTable);

        // Load existing reminders from MongoDB
        loadReminders();

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add Reminder");
        JButton deleteButton = createStyledButton("Delete Selected");
        JButton backButton = createStyledButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        // Button Actions
        addButton.addActionListener(e -> addReminder());
        deleteButton.addActionListener(e -> deleteReminder());
        backButton.addActionListener(e -> {
            dispose();
            new DashboardPage();
        });

        // Add Components
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void addReminder() {
        String date = dateField.getText();
        String description = descriptionField.getText();

        if (!date.isEmpty() && !description.isEmpty()) {
            // Add to Table
            tableModel.addRow(new Object[]{date, description});

            // Save to MongoDB
            Document reminder = new Document("date", date).append("description", description);
            reminderCollection.insertOne(reminder);

            // Clear Fields
            dateField.setText("");
            descriptionField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter both date and description.");
        }
    }

    private void deleteReminder() {
        int selectedRow = reminderTable.getSelectedRow();
        if (selectedRow != -1) {
            String date = (String) tableModel.getValueAt(selectedRow, 0);
            String description = (String) tableModel.getValueAt(selectedRow, 1);

            // Remove from MongoDB
            Document query = new Document("date", date).append("description", description);
            reminderCollection.deleteOne(query);

            // Remove from Table
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reminder to delete.");
        }
    }

    private void loadReminders() {
        MongoCursor<Document> cursor = reminderCollection.find().iterator();
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            tableModel.addRow(new Object[]{doc.getString("date"), doc.getString("description")});
        }
    }

    public static void main(String[] args) {
        new ReminderPage();
    }
}
