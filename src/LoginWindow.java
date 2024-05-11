import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.function.Consumer;

public class LoginWindow extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel centerPanel;
    private AuthenticationSystem authSystem;

    public LoginWindow(AuthenticationSystem authSystem) {
        this.authSystem = authSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        centerPanel.add(new JLabel("Email:"), gbc);
        centerPanel.add(emailField, gbc);
        centerPanel.add(new JLabel("Password:"), gbc);
        centerPanel.add(passwordField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        JPanel southPanel = new JPanel();
        southPanel.add(loginButton);
        add(southPanel, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentSizesAndFonts();
            }
        });

        setSize(600, 400); // Set initial larger size
        setMinimumSize(new Dimension(400, 300)); // Set a reasonable minimum size
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void adjustComponentSizesAndFonts() {
        float ratio = Math.min(getWidth() / 400f, getHeight() / 300f);
        Font font = new Font("Arial", Font.PLAIN, (int) (12 * ratio));
        Arrays.stream(centerPanel.getComponents()).forEach(c -> c.setFont(font));
        loginButton.setFont(font);
        repaint();
    }

    private void performLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        User user = authSystem.login(email, password);
        if (user != null) {
            dispose();  // Close the login window
            Consumer<Boolean> postLoginAction = success -> {
                if (user instanceof Student) {
                    new StudentWindow(user).setVisible(true);
                } else if (user instanceof Instructor) {
                    new InstructorWindow(user).setVisible(true);
                }
            };
            new StatusWindow("Login successful for " + user.getRole() + ": " + user.getName(), postLoginAction);
        } else {
            new StatusWindow("Login failed for email: " + email, null);
        }
    }

    public static void main(String[] args) {
        AuthenticationSystem authSystem = new AuthenticationSystem();
        AuthenticationSystemLoader loader = new AuthenticationSystemLoader(authSystem);
        loader.loadUsers("C:\\Users\\erkeb\\IdeaProjects\\EduChat Scholar\\src\\students.txt", "student");
        loader.loadUsers("C:\\Users\\erkeb\\IdeaProjects\\EduChat Scholar\\src\\instructors.txt", "instructor");
        new LoginWindow(authSystem);
    }
}

class StatusWindow extends JFrame {
    public StatusWindow(String message, Consumer<Boolean> postLoginAction) {
        setTitle("Login Status");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            dispose();
            if (postLoginAction != null) {
                postLoginAction.accept(true);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(300, 200));  // Ensure it has a reasonable minimum size
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
