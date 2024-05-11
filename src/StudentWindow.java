import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

public class StudentWindow extends JFrame {
    private JButton viewGradesButton, chooseCoursesButton, deleteCoursesButton;

    public StudentWindow(User user) {
        setTitle("Student Dashboard - " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10)); // Adjusted for one less button

        add(new JLabel("Welcome, " + user.getName(), SwingConstants.CENTER));

        chooseCoursesButton = new JButton("Choose Courses");
        chooseCoursesButton.addActionListener(e -> new CourseSelectionDialog(this, user.getName(), false).setVisible(true));
        add(chooseCoursesButton);

        deleteCoursesButton = new JButton("View and Delete Courses");
        deleteCoursesButton.addActionListener(e -> new CourseSelectionDialog(this, user.getName(), true).setVisible(true));
        add(deleteCoursesButton);

        viewGradesButton = new JButton("View My Grades");
        add(viewGradesButton);

        viewGradesButton.addActionListener(e -> {
            // Always show the grades window, even if no grades are available.
            SwingUtilities.invokeLater(() -> {
                GradesWindow gradesWindow = new GradesWindow(user.getName());
                gradesWindow.display(); // Open the grades window.
            });
        });



        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustButtonFonts();
            }
        });

        setSize(800, 600); // Set initial size for visibility
        setLocationRelativeTo(null); // Center on screen
        setVisible(true);
    }

    private void adjustButtonFonts() {
        float ratio = Math.min(getWidth() / 800f, getHeight() / 600f);
        Font font = new Font("Arial", Font.PLAIN, (int) (16 * ratio));
        updateComponentFonts(this.getContentPane(), font);
    }

    private void updateComponentFonts(Container container, Font font) {
        for (Component c : container.getComponents()) {
            c.setFont(font);
            if (c instanceof Container) {
                updateComponentFonts((Container) c, font);
            }
        }
    }
}
