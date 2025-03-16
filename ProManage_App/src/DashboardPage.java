import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;

public class DashboardPage extends JFrame {
    private JPanel contentPanel;

    public DashboardPage() {
        setTitle("Dashboard - ProManage");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1200, 700);
        setMinimumSize(new Dimension(1000, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(4, 1, 10, 10));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        JButton taskButton = new JButton("Task Tracker");
        JButton contactButton = new JButton("Contacts");
        JButton reminderButton = new JButton("Reminders");
        JButton logoutButton = new JButton("Logout");

        styleSidebarButton(taskButton);
        styleSidebarButton(contactButton);
        styleSidebarButton(reminderButton);
        styleSidebarButton(logoutButton);

        sidebar.add(taskButton);
        sidebar.add(contactButton);
        sidebar.add(reminderButton);
        sidebar.add(logoutButton);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel appNameLabel = new JLabel("ProManage", JLabel.LEFT);
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 26));
        appNameLabel.setForeground(new Color(44, 62, 80));
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JLabel dashboardLabel = new JLabel("Dashboard", JLabel.CENTER);
        dashboardLabel.setFont(new Font("Arial", Font.BOLD, 28));

        headerPanel.add(appNameLabel, BorderLayout.WEST);
        headerPanel.add(dashboardLabel, BorderLayout.CENTER);

        // Main Content Panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel(getGreetingMessage() + " Welcome back!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));

        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Add components to frame
        add(sidebar, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Button Actions
        taskButton.addActionListener(e -> openTaskManagementPage());
        contactButton.addActionListener(e -> openContactManagementPage());
        reminderButton.addActionListener(e -> openReminderPage());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        setVisible(true);
    }

    private void styleSidebarButton(JButton button) {
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
    }

    private void openTaskManagementPage() {
        dispose();
        new TaskManagementPage();
    }
    
    private void openContactManagementPage() {
        dispose();
        new ContactManagementPage();
    }

    private void openReminderPage() {
        dispose();
        new ReminderPage();
    }

    private String getGreetingMessage() {
        LocalTime time = LocalTime.now();
        int hour = time.getHour();
        if (hour >= 5 && hour < 12) {
            return "Good Morning!";
        } else if (hour >= 12 && hour < 18) {
            return "Good Afternoon!";
        } else {
            return "Good Evening!";
        }
    }

    public static void main(String[] args) {
        new DashboardPage();
    }
}
