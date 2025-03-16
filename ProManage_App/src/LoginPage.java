import javax.swing.*;
import java.awt.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import database.MongoDBConnection;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("ProManage - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top banner with app name
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185)); // Dark blue
        titlePanel.setPreferredSize(new Dimension(getWidth(), 100)); // Full-width banner

        JLabel title = new JLabel("ProManage", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(title, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH); // Add title bar to the top

        // Centered form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        // Login Button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(52, 152, 219));
        loginButton.addActionListener(e -> checkLogin());
        buttonPanel.add(loginButton);

        // Signup Button
        JButton signupButton = new JButton("Signup");
        styleButton(signupButton, new Color(46, 204, 113));
        signupButton.addActionListener(e -> goToSignup());
        buttonPanel.add(signupButton);

        // Add form and buttons to the center
        gbc.gridy = 4;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(160, 40));
    }

    private void checkLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> usersCollection = database.getCollection("users");

        Document user = usersCollection.find(new Document("username", username).append("password", password)).first();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Login successful!", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new DashboardPage(); // Redirect to dashboard
        }
    }

    private void goToSignup() {
        dispose();
        new SignupPage();
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
