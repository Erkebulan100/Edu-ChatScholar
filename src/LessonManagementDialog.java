import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.stream.Collectors;

public class LessonManagementDialog extends JDialog {
    private JTextArea lessonsArea;

    public LessonManagementDialog(Frame owner, String studentName) {
        super(owner, "Manage Courses", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        lessonsArea = new JTextArea();
        lessonsArea.setBorder(BorderFactory.createTitledBorder("Courses"));
        add(new JScrollPane(lessonsArea), BorderLayout.CENTER);

        Map<String, String> courses = LessonManagement.getCoursesForStudent(studentName);
        String courseText = courses.entrySet().stream()
                .map(entry -> entry.getKey() + " - Credits: " + entry.getValue())
                .collect(Collectors.joining("\n"));
        lessonsArea.setText(courseText);

        JButton saveButton = new JButton("Save Courses");
        saveButton.addActionListener(e -> {
            Map<String, String> updatedCourses = parseCoursesFromText(lessonsArea.getText());
            LessonManagement.setCoursesForStudent(studentName, updatedCourses);
            JOptionPane.showMessageDialog(this, "Courses updated.");
            dispose();
        });
        add(saveButton, BorderLayout.SOUTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentsFont();
            }
        });

        adjustComponentsFont();
        setResizable(true);
    }

    private Map<String, String> parseCoursesFromText(String text) {
        return java.util.Arrays.stream(text.split("\n"))
                .map(line -> line.split(" - Credits: "))
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    private void adjustComponentsFont() {
        float size = Math.min(getWidth(), getHeight()) / 25f;
        Font resizedFont = new Font("Dialog", Font.PLAIN, (int) size);

        lessonsArea.setFont(resizedFont);
        JButton saveButton = (JButton) getContentPane().getComponent(1);
        saveButton.setFont(resizedFont);
    }
}
