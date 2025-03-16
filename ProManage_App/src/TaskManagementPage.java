import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import database.MongoDBConnection;
import java.awt.*;

public class TaskManagementPage extends JFrame {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField taskNameField;
    private JButton addButton, updateButton, removeButton, completeButton, backButton;
    private MongoDatabase database;
    private MongoCollection<Document> taskCollection;

    public TaskManagementPage() {
        database = MongoDBConnection.getDatabase();
        taskCollection = database.getCollection("tasks");

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);
        setTitle("Task Manager - ProManage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Color primaryColor = new Color(30, 144, 255);
        Color textColor = Color.WHITE;

        JLabel titleLabel = new JLabel("Task Manager", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(textColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(primaryColor);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 80));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Task", "Status"}, 0);
        taskTable = new JTable(tableModel);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 18));
        taskTable.setRowHeight(30);
        JScrollPane tableScrollPane = new JScrollPane(taskTable);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add a New Task"));
        inputPanel.setBackground(Color.WHITE);

        taskNameField = new JTextField();
        taskNameField.setFont(new Font("Arial", Font.PLAIN, 24));
        taskNameField.setPreferredSize(new Dimension(400, 50));

        addButton = createStyledButton("Add Task", primaryColor, textColor);
        addButton.setPreferredSize(new Dimension(150, 50));

        inputPanel.add(taskNameField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        updateButton = createStyledButton("Update Task", primaryColor, textColor);
        removeButton = createStyledButton("Remove Task", primaryColor, textColor);
        completeButton = createStyledButton("Mark Completed", primaryColor, textColor);
        backButton = createStyledButton("Back", Color.RED, textColor);

        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        updateButton.addActionListener(e -> updateTask());
        removeButton.addActionListener(e -> removeTask());
        completeButton.addActionListener(e -> completeTask());

        backButton.addActionListener(e -> {
            dispose();  // Close Task Manager Page
            new DashboardPage();  // Open Dashboard Page
        });

        loadTasksFromDatabase();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    setSize(900, 600);
                }
            }
        });

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

    private void addTask() {
        String taskName = taskNameField.getText().trim();
        if (taskName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task Name is required!");
            return;
        }

        Document newTask = new Document("task", taskName).append("status", "Pending");
        taskCollection.insertOne(newTask);

        tableModel.addRow(new Object[]{taskName, "Pending"});
        taskNameField.setText("");
    }

    private void updateTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to update.");
            return;
        }

        String currentTask = (String) tableModel.getValueAt(selectedRow, 0);
        String newTaskName = JOptionPane.showInputDialog(this, "Enter new Task Name:", currentTask);

        if (newTaskName != null && !newTaskName.trim().isEmpty()) {
            tableModel.setValueAt(newTaskName.trim(), selectedRow, 0);

            Document filter = new Document("task", currentTask);
            Document update = new Document("$set", new Document("task", newTaskName));
            taskCollection.updateOne(filter, update);
        }
    }

    private void removeTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to remove.");
            return;
        }

        String taskToRemove = (String) tableModel.getValueAt(selectedRow, 0);
        tableModel.removeRow(selectedRow);

        taskCollection.deleteOne(new Document("task", taskToRemove));
    }

    private void completeTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to mark as completed.");
            return;
        }

        tableModel.setValueAt("Completed", selectedRow, 1);

        String completedTask = (String) tableModel.getValueAt(selectedRow, 0);
        Document filter = new Document("task", completedTask);
        Document update = new Document("$set", new Document("status", "Completed"));
        taskCollection.updateOne(filter, update);
    }

    private void loadTasksFromDatabase() {
        for (Document doc : taskCollection.find()) {
            String task = doc.getString("task");
            String status = doc.getString("status");
            tableModel.addRow(new Object[]{task, status});
        }
    }

    public static void main(String[] args) {
        new TaskManagementPage();
    }
}
