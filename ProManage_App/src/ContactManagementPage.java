import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import database.MongoDBConnection;
import java.awt.*;

public class ContactManagementPage extends JFrame {
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, numberField;
    private JButton saveButton, deleteButton, modifyButton, backButton;
    private MongoDatabase database;
    private MongoCollection<Document> contactCollection;

    public ContactManagementPage() {
        database = MongoDBConnection.getDatabase();
        contactCollection = database.getCollection("contacts");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setTitle("Contact Management - ProManage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color primaryColor = new Color(30, 144, 255);
        Color textColor = Color.WHITE;

        // 🔹 Header Title (North Region)
        JLabel titleLabel = new JLabel("Contact Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(textColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(primaryColor);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 80));
        add(titleLabel, BorderLayout.NORTH);

        // 🔹 Table for displaying contacts (Center Region)
        tableModel = new DefaultTableModel(new String[]{"Name", "Phone Number"}, 0);
        contactTable = new JTable(tableModel);
        contactTable.setFont(new Font("Arial", Font.PLAIN, 18));
        contactTable.setRowHeight(30);
        JScrollPane tableScrollPane = new JScrollPane(contactTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // 🔹 Input Panel for Name and Phone Number (North Region)
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add a New Contact"));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setPreferredSize(new Dimension(400, 50));

        JLabel numberLabel = new JLabel("Phone Number:");
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberField = new JTextField();
        numberField.setFont(new Font("Arial", Font.PLAIN, 18));
        numberField.setPreferredSize(new Dimension(400, 50));

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(numberLabel);
        inputPanel.add(numberField);

        add(inputPanel, BorderLayout.NORTH);

        // 🔹 Button Panel (South Region)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saveButton = createStyledButton("Save Contact", primaryColor, textColor);
        deleteButton = createStyledButton("Delete Contact", primaryColor, textColor);
        modifyButton = createStyledButton("Modify Contact", primaryColor, textColor);
        backButton = createStyledButton("Back", Color.RED, textColor);

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveContact());
        deleteButton.addActionListener(e -> deleteContact());
        modifyButton.addActionListener(e -> modifyContact());

        backButton.addActionListener(e -> {
            dispose();  // Close the current frame
            new DashboardPage();  // Open the Dashboard Page
        });

        loadContactsFromDatabase();

        setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void saveContact() {
        String name = nameField.getText().trim();
        String number = numberField.getText().trim();

        if (name.isEmpty() || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both Name and Phone Number are required!");
            return;
        }

        Document newContact = new Document("name", name).append("number", number);
        contactCollection.insertOne(newContact);

        tableModel.addRow(new Object[]{name, number});
        nameField.setText("");
        numberField.setText("");
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact to delete.");
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String number = (String) tableModel.getValueAt(selectedRow, 1);

        tableModel.removeRow(selectedRow);
        contactCollection.deleteOne(new Document("name", name).append("number", number));
    }

    private void modifyContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a contact to modify.");
            return;
        }

        String oldName = (String) tableModel.getValueAt(selectedRow, 0);
        String oldNumber = (String) tableModel.getValueAt(selectedRow, 1);

        String newName = JOptionPane.showInputDialog(this, "Enter new name:", oldName);
        String newNumber = JOptionPane.showInputDialog(this, "Enter new number:", oldNumber);

        if (newName != null && newNumber != null && !newName.trim().isEmpty() && !newNumber.trim().isEmpty()) {
            tableModel.setValueAt(newName.trim(), selectedRow, 0);
            tableModel.setValueAt(newNumber.trim(), selectedRow, 1);

            Document filter = new Document("name", oldName).append("number", oldNumber);
            Document update = new Document("$set", new Document("name", newName).append("number", newNumber));
            contactCollection.updateOne(filter, update);
        }
    }

    private void loadContactsFromDatabase() {
        for (Document doc : contactCollection.find()) {
            String name = doc.getString("name");
            String number = doc.getString("number");
            tableModel.addRow(new Object[]{name, number});
        }
    }

    public static void main(String[] args) {
        new ContactManagementPage();
    }
}
